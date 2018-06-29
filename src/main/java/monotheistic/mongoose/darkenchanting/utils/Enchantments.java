package monotheistic.mongoose.darkenchanting.utils;

import com.gitlab.avelyn.architecture.base.Component;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.Optional;

public class Enchantments extends Component {

    static BiMap<Enchantment, String> ENCHANTMENTS = HashBiMap.create();

     Enchantments() {
        onEnable(Enchantments::setup);
        onDisable(() -> ENCHANTMENTS = null);

    }

    public static Optional<Enchantment> getEnchantByDisplayName(String name) {
        final Enchantment enchantment = ENCHANTMENTS.inverse().get(transformString(name));
        return enchantment != null ? Optional.of(enchantment) : Optional.empty();
    }

    public static boolean isEnchantable(Material material) {
        return Gear.isToolOrArmor(material);
    }

    private static String transformString(String string) {
        StringBuilder builder = new StringBuilder();
        for (String word : string.split(" ")) {
            if (word.equalsIgnoreCase("of"))
                builder.append("of");
            else builder.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
            builder.append(" ");
        }
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }

    public static String getDisplayName(Enchantment enchantment) {
        return ENCHANTMENTS.get(enchantment);
    }

    private static void setup() {
        // ARMOR
        ENCHANTMENTS.put(Enchantment.PROTECTION_ENVIRONMENTAL, "Protection");
        ENCHANTMENTS.put(Enchantment.PROTECTION_FIRE, "Fire Protection");
        ENCHANTMENTS.put(Enchantment.PROTECTION_EXPLOSIONS, "Blast Protection");
        ENCHANTMENTS.put(Enchantment.PROTECTION_PROJECTILE, "Projectile Protection");
        ENCHANTMENTS.put(Enchantment.PROTECTION_FALL, "Feather Falling");
        ENCHANTMENTS.put(Enchantment.OXYGEN, "Respiration");
        ENCHANTMENTS.put(Enchantment.WATER_WORKER, "Aqua Affinity");
        ENCHANTMENTS.put(Enchantment.THORNS, "Thorns");
        ENCHANTMENTS.put(Enchantment.DEPTH_STRIDER, "Depth Strider");
        ENCHANTMENTS.put(Enchantment.DURABILITY, "Unbreaking");
        // WEAPONS
        ENCHANTMENTS.put(Enchantment.DAMAGE_ALL, "Sharpness");
        ENCHANTMENTS.put(Enchantment.DAMAGE_UNDEAD, "Smite");
        ENCHANTMENTS.put(Enchantment.DAMAGE_ARTHROPODS, "Bane Of Arthropods");
        ENCHANTMENTS.put(Enchantment.KNOCKBACK, "Knockback");
        ENCHANTMENTS.put(Enchantment.FIRE_ASPECT, "Fire Aspect");
        ENCHANTMENTS.put(Enchantment.LOOT_BONUS_MOBS, "Looting");
        // TOOLS
        ENCHANTMENTS.put(Enchantment.DIG_SPEED, "Efficiency");
        ENCHANTMENTS.put(Enchantment.SILK_TOUCH, "Silk Touch");
        ENCHANTMENTS.put(Enchantment.LOOT_BONUS_BLOCKS, "Fortune");
        // FISHING ROD
        ENCHANTMENTS.put(Enchantment.LUCK, "Luck of the Sea");
        ENCHANTMENTS.put(Enchantment.LURE, "Lure");
        // BOW
        ENCHANTMENTS.put(Enchantment.ARROW_DAMAGE, "Power");
        ENCHANTMENTS.put(Enchantment.ARROW_KNOCKBACK, "Punch");
        ENCHANTMENTS.put(Enchantment.ARROW_FIRE, "Flame");
        ENCHANTMENTS.put(Enchantment.ARROW_INFINITE, "Infinity");
        if (!Bukkit.getVersion().contains("1.8")) {
            // ARMOR
            ENCHANTMENTS.put(Enchantment.FROST_WALKER, "Frost Walker");
            ENCHANTMENTS.put(Enchantment.BINDING_CURSE, "Curse of Binding");
            // WEAPONS
            ENCHANTMENTS.put(Enchantment.SWEEPING_EDGE, "Sweeping Edge");
            // ANYTHING
            ENCHANTMENTS.put(Enchantment.MENDING, "Mending");
            ENCHANTMENTS.put(Enchantment.VANISHING_CURSE, "Curse of Vanishing");

        }
    }
}
