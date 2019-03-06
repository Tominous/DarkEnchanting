package monotheistic.mongoose.darkenchanting.listeners;

import com.gitlab.avelyn.architecture.base.Component;
import com.gitlab.avelyn.core.base.Events;
import monotheistic.mongoose.core.files.Configuration;
import monotheistic.mongoose.core.gui.GUI;
import monotheistic.mongoose.core.gui.MyGUI;
import monotheistic.mongoose.core.gui.PaginateIcons;
import monotheistic.mongoose.core.utils.ItemBuilder;
import monotheistic.mongoose.core.utils.MiscUtils;
import monotheistic.mongoose.core.utils.ScheduleUtils;
import monotheistic.mongoose.darkenchanting.mobs.WitchMerchantFactory;
import monotheistic.mongoose.darkenchanting.utils.Enchantments;
import monotheistic.mongoose.darkenchanting.utils.NBTUtils;
import org.bukkit.*;
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

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static monotheistic.mongoose.darkenchanting.Info.INFO;

class WitchInteraction extends Component {
  private final WitchMerchantFactory factory;
  private final GUIMenus guiMenus;

  WitchInteraction(Path dataFolder, WitchMerchantFactory factory) {
    addChild(Events.listen(this::stopDamage), Events.listen(this::openGui));
    this.factory = factory;
    final Configuration configuration = Configuration.loadConfiguration(this.getClass().getClassLoader(), dataFolder, "options.yml");
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
    if (!event.getPlayer().hasPermission("de.witch.interact") && !event.getPlayer().isOp()) {
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
    private final PaginateIcons BOOK_SHOP_ICONS = new PaginateIcons() {
      @Override
      public ItemStack forwards() {
        return PaginateIcons.DEFAULT.forwards();
      }

      @Override
      public ItemStack backwards() {
        return PaginateIcons.DEFAULT.backwards();
      }

      @Override
      public ItemStack pageIdentifier(int i) {
        return new ItemBuilder(Material.PAPER).addItemFlags(ItemFlag.HIDE_ATTRIBUTES).name(INFO.getMainColor() + "Page: " + INFO.getSecondaryColor() + i).build();
      }

      @Override
      public String titleForPage(int i) {
        ChatColor first = INFO.getMainColor();
        ChatColor second = INFO.getSecondaryColor();
        return second + "--" + first + "Buy a Book! Page " + i + second + "--";
      }
    };
    private ItemStack[] bookVals;

    private ItemStack[] parseValues(Configuration configuration) {
      return configuration.getConfigurationSection("enchantments").getKeys(false).stream()
              .filter(enchString -> configuration.getBoolean("enchantments." + enchString + ".enabled"))
              .collect(Collectors.toMap(
                      //Key mapper
                      enchSection -> {
                        Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(enchSection));
                        if (ench == null) {
                          throw new IllegalArgumentException("Invalid Enchantment in options.yml! Your enchant: " + enchSection);
                        }
                        return ench;
                      },
                      //Value mapper
                      enchSection -> configuration.getInt("enchantments." + enchSection + ".amt"))).entrySet()
              .stream().map((entry) -> {
                        final ItemStack enchantBook = new ItemBuilder(Material.BOOK)
                                .name(INFO.getMainColor() + Enchantments.getDisplayName(entry.getKey())).lore(ChatColor.RED + "" + entry.getValue() + " Blood Shards")
                                .build();
                        return NBTUtils.setString(enchantBook, "dark_enchantment", entry.getKey().getKey().getKey());
                      }
              ).toArray(ItemStack[]::new);
    }

    private MyGUI createMainMenu() {
      final MyGUI gui = new MyGUI(INFO.getDisplayName() + ChatColor.RESET + INFO.getMainColor() + " Shop", 9);
      gui.set(4, new ItemBuilder(Material.WRITABLE_BOOK).name(ChatColor.GRAY + "Dark Enchantments").addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
              .build(), (InventoryClickEvent event) -> ScheduleUtils.laterSync(() -> {
        final Optional<GUI> shop = createBookShop();
        shop.ifPresent(shopGui -> shopGui.open((Player) event.getWhoClicked()));
      }, 1));
      Items.ALTAR_INTERACT(true).ifPresent(item -> gui.set(3, item, (InventoryClickEvent event) -> {
        int price = price(event.getCurrentItem());
        if (price == -1) return;
        proccessBloodDropTransaction(event, price, true, 0);
      }));
      return gui;
    }

    private int price(ItemStack item) {
      final String priceStr = ChatColor.stripColor(item.getItemMeta().getLore().get(0));
      return MiscUtils.parseInt(priceStr.substring(0, priceStr.indexOf(32))).orElse(-1);
    }

    private void proccessBloodDropTransaction(InventoryClickEvent event, int price, boolean unstackableProduct, int... linesOfLoreToRemove) {
      final Player player = (Player) event.getWhoClicked();
      final PlayerInventory inv = player.getInventory();
      if (removeItem(inv, Items.BLOOD_DROPLET(), price)) {
        final ItemStack item = event.getCurrentItem();
        final List<String> lore = item.getItemMeta().getLore();
        for (int line : linesOfLoreToRemove) {
          lore.remove(line);
        }
        final ItemStack itemStack = ItemBuilder.copyOf(item).lore(lore).build();
        if (unstackableProduct) {
          NBTUtils.setString(itemStack, UUID.randomUUID().toString(), "_");
        }
        addOrDrop(player, itemStack);
      }
    }

    private Optional<GUI> createBookShop() {
      BiFunction<String, Integer, GUI> guiBiFunction = MyGUI::new;
      return GUI.paginatorGui(bookVals, 54, event -> {
        if (event.getCurrentItem().getType() != Material.BOOK) {
          return;
        }
        final String priceStr = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().get(0));
        int price = MiscUtils.parseInt(priceStr.substring(0, priceStr.indexOf(32))).orElse(-1);
        if (price == -1)
          return;
        proccessBloodDropTransaction(event, price, false, 0);
      }, guiBiFunction, BOOK_SHOP_ICONS);
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