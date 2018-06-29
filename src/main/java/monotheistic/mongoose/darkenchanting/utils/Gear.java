package monotheistic.mongoose.darkenchanting.utils;

import com.gitlab.avelyn.architecture.base.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.Material.*;

public class Gear extends Component {
    private static Material[] TOOLS;
    private static Material[] ARMOR;

     Gear() {
        onEnable(Gear::setup);
        onDisable(() -> TOOLS = null);
    }

    private static void setup() {
        TOOLS = new Material[26];
        TOOLS[0] = (STONE_AXE);
        TOOLS[1] = (STONE_HOE);
        TOOLS[2] = (STONE_PICKAXE);
        TOOLS[3] = (STONE_SWORD);
        TOOLS[4] = (STONE_SPADE);
        TOOLS[5] = WOOD_AXE;
        TOOLS[6] = WOOD_HOE;
        TOOLS[7] = WOOD_PICKAXE;
        TOOLS[8] = WOOD_SWORD;
        TOOLS[9] = WOOD_SPADE;
        TOOLS[10] = IRON_AXE;
        TOOLS[11] = IRON_HOE;
        TOOLS[12] = IRON_PICKAXE;
        TOOLS[13] = IRON_SWORD;
        TOOLS[14] = IRON_SPADE;
        TOOLS[15] = GOLD_AXE;
        TOOLS[16] = GOLD_HOE;
        TOOLS[17] = GOLD_PICKAXE;
        TOOLS[18] = GOLD_SPADE;
        TOOLS[19] = GOLD_SWORD;
        TOOLS[20] = DIAMOND_AXE;
        TOOLS[21] = DIAMOND_HOE;
        TOOLS[22] = DIAMOND_PICKAXE;
        TOOLS[23] = DIAMOND_SWORD;
        TOOLS[24] = DIAMOND_SPADE;
        TOOLS[25] = FISHING_ROD;
        ARMOR = new Material[16];
        ARMOR[0] = CHAINMAIL_HELMET;
        ARMOR[1] = CHAINMAIL_BOOTS;
        ARMOR[2] = CHAINMAIL_LEGGINGS;
        ARMOR[3] = CHAINMAIL_CHESTPLATE;
        ARMOR[4] = IRON_HELMET;
        ARMOR[5] = IRON_BOOTS;
        ARMOR[6] = IRON_LEGGINGS;
        ARMOR[7] = IRON_CHESTPLATE;
        ARMOR[8] = GOLD_HELMET;
        ARMOR[9] = GOLD_BOOTS;
        ARMOR[10] = GOLD_LEGGINGS;
        ARMOR[11] = GOLD_CHESTPLATE;
        ARMOR[12] = DIAMOND_HELMET;
        ARMOR[13] = DIAMOND_BOOTS;
        ARMOR[14] = DIAMOND_LEGGINGS;
        ARMOR[15] = DIAMOND_CHESTPLATE;

    }

    public static boolean isTool(ItemStack item) {
        return isTool(item.getType());
    }

    public static boolean isTool(Material material) {
        for (Material arrMaterial : TOOLS) {
            if (arrMaterial == material)
                return true;
        }
        return false;
    }

    public static boolean isArmor(Material material) {
        for (Material arrMaterial : ARMOR) {
            if (arrMaterial == material)
                return true;
        }
        return false;
    }

    public static boolean isArmor(ItemStack item) {
        return isArmor(item.getType());
    }

    public static boolean isToolOrArmor(Material material) {
        return isTool(material) || isArmor(material);
    }

    public static boolean isToolOrArmor(ItemStack item) {
        return isToolOrArmor(item.getType());
    }
}
