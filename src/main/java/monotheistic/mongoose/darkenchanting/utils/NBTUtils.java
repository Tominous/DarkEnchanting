package monotheistic.mongoose.darkenchanting.utils;

import monotheistic.mongoose.core.utils.ItemBuilder;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class NBTUtils {
    public static org.bukkit.inventory.ItemStack addTag(org.bukkit.inventory.ItemStack itemStack, Map<String, String> nbtTag) {
        final ItemStack stack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound compound = stack.getTag() != null ? stack.getTag() : new NBTTagCompound();
        nbtTag.forEach(compound::setString);
        stack.setTag(compound);
        return CraftItemStack.asBukkitCopy(stack);
    }

    public static org.bukkit.inventory.ItemStack addTag(org.bukkit.inventory.ItemStack itemStack, String key, String value) {
        final Map<String, String> map = new HashMap<>();
        map.put(key, value);
        return addTag(itemStack, map);
    }

    public static boolean checkIfHasTag(String tag, org.bukkit.inventory.ItemStack item) {
        final NBTTagCompound nbtTagCompound = CraftItemStack.asNMSCopy(item).getTag();
        return nbtTagCompound!=null&&nbtTagCompound.hasKey(tag);
    }

    public static Optional<String> getString(String key, org.bukkit.inventory.ItemStack itemStack) {
        final NBTTagCompound tag = CraftItemStack.asNMSCopy(itemStack).getTag();
        if (tag != null)
            return Optional.ofNullable(tag.getString(key));
        return Optional.empty();
    }

    public static org.bukkit.inventory.ItemStack addTag(ItemBuilder builder, String key, String value) {
        return addTag(builder.build(), key, value);
    }
}
