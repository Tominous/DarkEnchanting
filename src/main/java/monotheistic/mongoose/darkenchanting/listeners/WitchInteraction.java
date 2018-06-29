package monotheistic.mongoose.darkenchanting.listeners;

import com.gitlab.avelyn.architecture.base.Component;
import com.gitlab.avelyn.core.base.Events;
import monotheistic.mongoose.core.files.Configuration;
import monotheistic.mongoose.core.gui.MyGUI;
import monotheistic.mongoose.core.gui.PaginatorGUI;
import monotheistic.mongoose.core.strings.PluginStrings;
import monotheistic.mongoose.core.utils.ItemBuilder;
import monotheistic.mongoose.core.utils.MiscUtils;
import monotheistic.mongoose.core.utils.ScheduleUtils;
import monotheistic.mongoose.darkenchanting.Main;
import monotheistic.mongoose.darkenchanting.mobs.WitchMerchantFactory;
import monotheistic.mongoose.darkenchanting.utils.Enchantments;
import monotheistic.mongoose.darkenchanting.utils.NBTUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.stream.Collectors;

 class WitchInteraction extends Component {
    private final WitchMerchantFactory factory;
    private final GUIMenus guiMenus;

    WitchInteraction(Main main, WitchMerchantFactory factory) {
        addChild(Events.listen(this::stopDamage), Events.listen(this::openGui));
        this.factory = factory;
        final Configuration configuration = new Configuration(main, "options.yml");
        this.guiMenus = new GUIMenus();
        onEnable(() ->
            guiMenus.bookVals = guiMenus.parseValues(configuration)
        );
        onDisable(() -> guiMenus.bookVals = null);
    }


    private void stopDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Witch && this.factory.isCustomEntity((Witch) event.getEntity()))
            event.setCancelled(true);
    }


    private void openGui(PlayerInteractEntityEvent event) {
        if(!event.getPlayer().hasPermission("de.witch.interact")&&!event.getPlayer().isOp()) {
            return;
        }
        if (event.getRightClicked() instanceof Witch && factory.isCustomEntity((Witch) event.getRightClicked())) {
            if (!event.getPlayer().isSneaking())
                guiMenus.createMainMenu().open(event.getPlayer());
            else {
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_WITCH_DEATH, SoundCategory.HOSTILE
                        , .5f, .5f);
                event.getRightClicked().remove();
            }
        }
    }


    private class GUIMenus {
        private ItemStack[] bookVals;

        private ItemStack[] parseValues(Configuration configuration) {
            return configuration.configuration().getConfigurationSection("enchantments").getKeys(false).stream()
                    .filter(enchString -> configuration.configuration().getBoolean("enchantments." + enchString + ".enabled"))
                    .collect(Collectors.toMap(Enchantment::getByName,
                            enchSection -> configuration.configuration().getInt("enchantments." + enchSection + ".amt"))).entrySet()
                    .stream().map((entry) -> NBTUtils.addTag(new ItemBuilder(Material.BOOK)
                            .name(PluginStrings.mainColor() + Enchantments.getDisplayName(entry.getKey())).lore(ChatColor.RED + "" + entry.getValue() + " Blood Shards")
                            .build(), "dark_enchantment", entry.getKey().getName())).toArray(ItemStack[]::new);

        }

        private MyGUI createMainMenu() {
            final MyGUI gui = new MyGUI(PluginStrings.tag() + ChatColor.RESET + PluginStrings.mainColor() + " Shop", 9);
            gui.set(4, new ItemBuilder(Material.BOOK_AND_QUILL).name(ChatColor.GRAY + "Dark Enchantments").addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                    .build(), (InventoryClickEvent event) -> ScheduleUtils.laterSync(() -> createBookShop().open((Player) event.getWhoClicked()), 1));
            Items.ALTAR_INTERACT(true).ifPresent(item -> gui.set(3, item, (InventoryClickEvent event) -> {
                int price = price(event.getCurrentItem());
                if (price == -1) return;
                proccessBloodDropTransaction(event, price, 0);
            }));
            return gui;
        }

        private int price(ItemStack item) {
            final String priceStr = ChatColor.stripColor(item.getItemMeta().getLore().get(0));
            return MiscUtils.parseInt(priceStr.substring(0, priceStr.indexOf(32))).orElse(-1);
        }

        private void proccessBloodDropTransaction(InventoryClickEvent event, int price, int... linesOfLoreToRemove) {
            final Player player = (Player) event.getWhoClicked();
            final PlayerInventory inv = player.getInventory();
            if (removeItem(inv, Items.BLOOD_DROPLET(), price)) {
                final ItemStack item=event.getCurrentItem();
                final List<String> lore=item.getItemMeta().getLore();
                for(int line: linesOfLoreToRemove){
                    lore.remove(line);
                }
                addOrDrop(player, ItemBuilder.copyOf(item).lore(lore).build());
            }
        }

        private PaginatorGUI createBookShop() {
            return new PaginatorGUI(bookVals, ChatColor.GRAY + "Enchantments Shop", 54, (InventoryClickEvent event) -> {
                if(event.getCurrentItem().getType()!=Material.BOOK)
                    return;
                final String priceStr = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().get(0));
                int price = MiscUtils.parseInt(priceStr.substring(0, priceStr.indexOf(32))).orElse(-1);
                if (price == -1)
                    return;
                proccessBloodDropTransaction(event, price, 0);
            });
        }

        private void addOrDrop(Player player, ItemStack item) {
            if (player.getInventory().addItem(item).isEmpty())
                return;
            player.getLocation().getWorld().dropItem(player.getLocation(), item);
        }

        private boolean removeItem(Inventory inv, ItemStack item, int amt) {
            if (!inv.containsAtLeast(item, amt)) {
                return false;
            }
            ItemStack currentItem;
            for (int i = 0; i < 36; i++) {
                if ((currentItem = inv.getItem(i)) != null && currentItem.isSimilar(item)) {
                    if (currentItem.getAmount() >= amt) {
                        currentItem.setAmount(currentItem.getAmount() - amt);
                        return true;
                    } else {
                        amt -= currentItem.getAmount();
                        inv.setItem(i, null);
                    }
                }
            }
            return false;
        }

    }
}