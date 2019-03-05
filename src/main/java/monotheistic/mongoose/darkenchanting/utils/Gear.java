package monotheistic.mongoose.darkenchanting.utils;

import com.gitlab.avelyn.architecture.base.Component;
import org.bukkit.Material;

import static org.bukkit.Material.*;

class Gear extends Component {
  private static Material[] TOOLS;
  private static Material[] ARMOR;

  Gear() {
    onEnable(Gear::setup);
    onDisable(() -> TOOLS = null);
  }

  private static void setup() {
    TOOLS = new Material[]{STONE_AXE, STONE_HOE, STONE_PICKAXE, STONE_SWORD, STONE_SHOVEL,
            WOODEN_AXE, WOODEN_HOE, WOODEN_PICKAXE, WOODEN_SWORD, WOODEN_SHOVEL,
            IRON_AXE, IRON_HOE, IRON_PICKAXE, IRON_SWORD, IRON_SHOVEL,
            GOLDEN_AXE, GOLDEN_HOE, GOLDEN_PICKAXE, GOLDEN_SWORD, GOLDEN_SHOVEL,
            DIAMOND_AXE, DIAMOND_HOE, DIAMOND_PICKAXE, DIAMOND_SWORD, DIAMOND_SHOVEL,
            FISHING_ROD};
    ARMOR = new Material[]{CHAINMAIL_HELMET, CHAINMAIL_BOOTS, CHAINMAIL_LEGGINGS, CHAINMAIL_CHESTPLATE,
            IRON_HELMET, IRON_BOOTS, IRON_LEGGINGS, IRON_CHESTPLATE,
            GOLDEN_HELMET, GOLDEN_BOOTS, GOLDEN_LEGGINGS, GOLDEN_CHESTPLATE,
            DIAMOND_HELMET, DIAMOND_BOOTS, DIAMOND_LEGGINGS, DIAMOND_CHESTPLATE};
  }

  private static boolean isTool(Material material) {
    for (Material arrMaterial : TOOLS) {
      if (arrMaterial == material)
        return true;
    }
    return false;
  }

  private static boolean isArmor(Material material) {
    for (Material arrMaterial : ARMOR) {
      if (arrMaterial == material)
        return true;
    }
    return false;
  }

  static boolean isToolOrArmor(Material material) {
    return isTool(material) || isArmor(material);
  }
}
