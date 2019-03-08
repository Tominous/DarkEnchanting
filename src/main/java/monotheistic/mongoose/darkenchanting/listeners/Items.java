package monotheistic.mongoose.darkenchanting.listeners;

import com.gitlab.avelyn.architecture.base.Component;
import com.gitlab.avelyn.core.base.Events;
import monotheistic.mongoose.core.files.Configuration;
import monotheistic.mongoose.core.utils.ItemBuilder;
import monotheistic.mongoose.core.utils.PluginImpl;
import monotheistic.mongoose.darkenchanting.Info;
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

import java.nio.file.Path;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

public class Items extends Component {
  private static int maxWandUses;
  private static ItemStack BLOOD_DROPLET;
  private static ItemStack ALTAR_INTERACT;
  private final Map<Material, String> nbtTags;

  public Items(Path dataFolder) {
    nbtTags = new IdentityHashMap<>();
    final Configuration configuration = Configuration.loadConfiguration(this.getClass().getClassLoader(), dataFolder, "options.yml");
    final boolean useIt = configuration.getBoolean("altar-item.buyable");
    final int price = configuration.getInt("altar-item.price");
    final int uses = configuration.getInt("altar-item.uses");
    maxWandUses = uses;
    BLOOD_DROPLET = new ItemBuilder(Material.FERMENTED_SPIDER_EYE).
            name(ChatColor.RED + "" + ChatColor.UNDERLINE + ChatColor.BOLD + "Blood Droplet").addItemFlags(ItemFlag.HIDE_ATTRIBUTES).build();
    if (useIt) {
      ItemStack altarItem = new ItemBuilder(Material.STICK).addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
              .name(Info.INFO.getSecondaryColor() + "Wand of Darkness").lore(ChatColor.RED + "" + price + " Blood Shards",
                      Info.INFO.getMainColor() + "Uses Remaining: " + ChatColor.RESET + ChatColor.BOLD + uses + " of " + uses).build();
      altarItem = NBTUtils.setString(altarItem, "enchanting_altar_item", "_");
      ALTAR_INTERACT = NBTUtils.setInt(altarItem, "uses_remaining", uses);
      nbtTags.put(Material.STICK, "enchanting_altar_item");
    }
    nbtTags.put(Material.BOOK, "dark_enchantment");

    onDisable(() -> {
      BLOOD_DROPLET = null;
      ALTAR_INTERACT = null;
    });

    onEnable(() ->
            Bukkit.getPluginManager().registerEvent(CraftItemEvent.class, new Listener() {
            }, EventPriority.NORMAL, ((listener, theEvent) -> {
              if (!(theEvent instanceof CraftItemEvent))
                return;
              CraftItemEvent event = (CraftItemEvent) theEvent;
              for (ItemStack item : event.getInventory().getMatrix()) {
                for (Map.Entry<Material, String> entry : nbtTags.entrySet()) {
                  if (item == null)
                    continue;
                  if ((entry.getKey() == item.getType() && NBTUtils.checkIfHasStringTag(entry.getValue(), item))) {
                    event.setCancelled(true);
                  }
                }
              }
            }), new PluginImpl("ItemCraftEvent"))
    );

    /*
    addChild(Events.listen(CraftItemEvent.class, (CraftItemEvent event) -> {
      for (ItemStack item : event.getInventory().getMatrix()) {
        for (Map.Entry<Material, String> entry : nbtTags.entrySet()) {
          if (item == null)
            continue;
          if ((entry.getKey() == item.getType() && NBTUtils.checkIfHasStringTag(entry.getValue(), item))) {
            event.setCancelled(true);
          }
        }
      }
    }));
    */
    addChild(Events.listen((BrewEvent event) -> {
      if (event.getContents().getIngredient().isSimilar(BLOOD_DROPLET))
        event.setCancelled(true);
    }));
  }

  public static ItemStack BLOOD_DROPLET() {
    return BLOOD_DROPLET.clone();
  }

  public static Optional<ItemStack> ALTAR_INTERACT(boolean withPrice) {
    if (ALTAR_INTERACT == null) {
      return Optional.empty();
    }
    ItemBuilder builder = ItemBuilder.copyOf(ALTAR_INTERACT);
    if (!withPrice) {
      builder.lore(ALTAR_INTERACT.getItemMeta().getLore().get(1));
    }
    return Optional.ofNullable(builder.build());
  }

  public static boolean isAltarItem(ItemStack other) {
    return other != null && NBTUtils.checkIfHasStringTag("enchanting_altar_item", other);
  }

  public static ItemStack modifyUsagesOfAltarItem(ItemStack altarItem) {
      /*
    final String old = ChatColor.stripColor(altarItem.getItemMeta().getLore().get(0));

    int totalUses = MiscUtils.parseInt(old.substring(old.lastIndexOf(32) + 1)).orElseThrow(() ->
            new RuntimeException("Item clicked with had no total usages!"));
            */
    int currentUsages = NBTUtils.getInt(altarItem, "uses_remaining").orElseThrow(() -> new RuntimeException("Item clicked with had no current usages!"));
    /*
    int currentUsages = MiscUtils.parseInt(old.substring(old.indexOf(':') + 2, old.indexOf("of") - 1)).orElseThrow(() ->
            new RuntimeException("Item clicked with had no current usages!"));
            */
    if (--currentUsages <= 0) {
      return null;
    }
    ItemStack newItem = new ItemBuilder(altarItem).lore(Info.INFO.getDisplayName() +
            "Uses Remaining: " + ChatColor.RESET + ChatColor.BOLD + currentUsages + " of " + maxWandUses).build();
    return NBTUtils.setInt(newItem, "uses_remaining", currentUsages);
  }

}
