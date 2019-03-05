package monotheistic.mongoose.darkenchanting.utils;

import monotheistic.mongoose.core.utils.PluginImpl;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

public class NBTUtils {
  private static final Plugin NBT_WORKER = new PluginImpl("NBT_WORKER");

  public static org.bukkit.inventory.ItemStack addTag(org.bukkit.inventory.ItemStack itemStack, String key, String value) {
    final ItemMeta itemMeta = itemStack.getItemMeta();
    final CustomItemTagContainer container = itemMeta.getCustomTagContainer();
    container.setCustomTag(new NamespacedKey(NBT_WORKER, key), ItemTagType.STRING, value);
    itemStack.setItemMeta(itemMeta);
    return itemStack;
  }

  public static boolean checkIfHasTag(String tag, org.bukkit.inventory.ItemStack item) {
    return item.getItemMeta().getCustomTagContainer().hasCustomTag(new NamespacedKey(NBT_WORKER, tag), ItemTagType.STRING);
  }

  public static Optional<String> getString(String key, org.bukkit.inventory.ItemStack itemStack) {
    return Optional.ofNullable(itemStack.getItemMeta().getCustomTagContainer().getCustomTag(new NamespacedKey(NBT_WORKER, key), ItemTagType.STRING));
  }

}
