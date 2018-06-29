package monotheistic.mongoose.darkenchanting.listeners;

import com.gitlab.avelyn.architecture.base.Component;
import com.gitlab.avelyn.core.base.Events;
import monotheistic.mongoose.core.files.Configuration;
import monotheistic.mongoose.core.strings.PluginStrings;
import monotheistic.mongoose.core.utils.ItemBuilder;
import monotheistic.mongoose.core.utils.MiscUtils;
import monotheistic.mongoose.darkenchanting.Main;
import monotheistic.mongoose.darkenchanting.utils.NBTUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Items extends Component {
    private static ItemStack BLOOD_DROPLET;
    private static ItemStack ALTAR_INTERACT;
    private final Map<Material, String> nbtTags;
    private final Set<ItemStack> itemStacks;

    public Items(Main main) {
        nbtTags = new IdentityHashMap<>();
        itemStacks = new HashSet<>();
        final Configuration configuration = new Configuration(main, "options.yml");
        final boolean useIt = configuration.configuration().getBoolean("altar-item.buyable");
        final int price = configuration.configuration().getInt("altar-item.price");
        final int uses = configuration.configuration().getInt("altar-item.uses");
        BLOOD_DROPLET = new ItemBuilder(Material.FERMENTED_SPIDER_EYE).
                name(ChatColor.RED + "" + ChatColor.UNDERLINE + ChatColor.BOLD + "Blood Droplet").addItemFlags(ItemFlag.HIDE_ATTRIBUTES).build();
        if (useIt)
            ALTAR_INTERACT = NBTUtils.addTag(new ItemBuilder(Material.STICK).addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                    .name(PluginStrings.secondaryColor() + "Wand of Darkness").lore(ChatColor.RED + "" + price + " Blood Shards",
                            PluginStrings.mainColor() + "Uses Remaining: " + ChatColor.RESET + ChatColor.BOLD + uses + " of " + uses).build(), "enchanting_altar_item", "ignored");
        nbtTags.put(Material.STICK, "enchanting_altar_item");
        nbtTags.put(Material.BOOK, "dark_enchantment");
        itemStacks.add(BLOOD_DROPLET);

        onDisable(() -> {
            BLOOD_DROPLET = null;
            ALTAR_INTERACT = null;
        });
        onEnable(()->{
        Bukkit.getPluginManager().registerEvent(CraftItemEvent.class, new Listener() {
        }, EventPriority.NORMAL, ((listener, theEvent) -> {
            if(!(theEvent instanceof CraftItemEvent))
                return;
            CraftItemEvent event = (CraftItemEvent) theEvent;
            for (ItemStack item : event.getInventory().getMatrix()) {
                for (Map.Entry<Material, String> entry : nbtTags.entrySet()) {
                    if (item == null)
                        continue;
                    if ((entry.getKey() == item.getType() && NBTUtils.checkIfHasTag(entry.getValue(), item))) {
                        event.setCancelled(true);
                    }
                }
                for (ItemStack toCheck : itemStacks) {
                    if (toCheck.isSimilar(item))
                        event.setCancelled(true);
                }
            }
        }), main);
        });
        addChild(Events.listen((BrewEvent event)->{
            if (event.getContents().getIngredient().isSimilar(BLOOD_DROPLET))
                event.setCancelled(true);
        }));


    }

    public static ItemStack BLOOD_DROPLET() {
        return BLOOD_DROPLET;
    }

    public static Optional<ItemStack> ALTAR_INTERACT(boolean withPrice) {
        if (ALTAR_INTERACT == null)
            return Optional.empty();
        if (withPrice)
            return Optional.ofNullable(ItemBuilder.copyOf(ALTAR_INTERACT).build());
        else
            return Optional.ofNullable(ItemBuilder.copyOf(ALTAR_INTERACT).lore(ALTAR_INTERACT.getItemMeta().getLore().get(1)).build());
    }

    public static boolean isAltarItem(ItemStack other) {
        return other != null && NBTUtils.checkIfHasTag("enchanting_altar_item", other);
    }

    public static ItemStack modifyUsagesOfAltarItem(ItemStack altarItem) {
        final String old = ChatColor.stripColor(altarItem.getItemMeta().getLore().get(0));
        int totalUses = MiscUtils.parseInt(old.substring(old.lastIndexOf(32) + 1, old.length())).orElseThrow(() ->
                new RuntimeException("Item clicked with had no total usages!"));
        int currentUsages = MiscUtils.parseInt(old.substring(old.indexOf(':') + 2, old.indexOf("of") - 1)).orElseThrow(() ->
                new RuntimeException("Item clicked with had no current usages!"));
        if (--currentUsages <= 0)
            return null;
        return new ItemBuilder(altarItem).lore(PluginStrings.mainColor() +
                "Uses Remaining: " + ChatColor.RESET + ChatColor.BOLD + currentUsages + " of " + totalUses).build();
    }

}
