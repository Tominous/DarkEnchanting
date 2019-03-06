package monotheistic.mongoose.darkenchanting.utils;

import monotheistic.mongoose.core.utils.PluginImpl;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

public class NBTUtils {
  private static final Plugin NBT_WORKER = new PluginImpl("NBT_WORKER");

  public static boolean checkIfHasStringTag(String tag, org.bukkit.inventory.ItemStack item) {
    return item.getItemMeta().getCustomTagContainer().hasCustomTag(new NamespacedKey(NBT_WORKER, tag), ItemTagType.STRING);
  }

  public static Optional<Integer> getInt(ItemStack itemStack, String key) {
    return getPrimitive(key, itemStack, ItemTagType.INTEGER);
  }

  public static ItemStack setInt(ItemStack itemStack, String key, int value) {
    return setPrimitive(key, value, itemStack, ItemTagType.INTEGER);
  }

  public static Optional<String> getString(ItemStack itemStack, String key) {
    return getPrimitive(key, itemStack, ItemTagType.STRING);
  }

  public static ItemStack setString(ItemStack itemStack, String key, String value) {
    return setPrimitive(key, value, itemStack, ItemTagType.STRING);
  }

  public static <T> Optional<T> getPrimitive(String key, ItemStack itemStack, ItemTagType<T, T> type) {
    return Optional.ofNullable(itemStack.getItemMeta().getCustomTagContainer().getCustomTag(new NamespacedKey(NBT_WORKER, key), type));
  }

  public static <T> ItemStack setPrimitive(String key, T value, ItemStack item, ItemTagType<T, T> tagType) {
    final ItemMeta meta = item.getItemMeta();
    meta.getCustomTagContainer().setCustomTag(new NamespacedKey(NBT_WORKER, key), tagType, value);
    item.setItemMeta(meta);
    return item;
  }

}
