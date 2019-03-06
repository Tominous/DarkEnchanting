package monotheistic.mongoose.darkenchanting.ritual;

import monotheistic.mongoose.darkenchanting.listeners.Items;
import monotheistic.mongoose.darkenchanting.utils.Enchantments;
import monotheistic.mongoose.darkenchanting.utils.NBTUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static org.bukkit.Material.*;

public class Altar extends SymmetricMultiblock {

  public Altar() {
    super(getAltarLayout(), Material.REDSTONE_BLOCK, Items::isAltarItem, new Triple<>(getAltarLayout()[0].length / 2, 0, getAltarLayout()[0][0].length / 2));
    setAction(this::runRitual);
  }

  private static Material[][][] getAltarLayout() {
    final Material[][][] arr = new Material[3][5][5];
    set(arr, 0, 0, 1, CHEST);
    set(arr, 0, 0, 2, STONE_BRICK_STAIRS);
    set(arr, 0, 0, 3, CHEST);
    set(arr, 0, 1, 0, CHEST);
    set(arr, 0, 1, 1, COBBLESTONE);
    set(arr, 0, 1, 3, COBBLESTONE);
    set(arr, 0, 1, 4, CHEST);
    set(arr, 0, 2, 0, STONE_BRICK_STAIRS);
    set(arr, 0, 2, 2, REDSTONE_BLOCK);
    set(arr, 0, 2, 4, STONE_BRICK_STAIRS);
    set(arr, 1, 1, 1, COBBLESTONE);
    set(arr, 1, 1, 2, STONE_BRICK_STAIRS);
    set(arr, 1, 1, 3, COBBLESTONE);
    set(arr, 1, 2, 1, STONE_BRICK_STAIRS);
    set(arr, 1, 2, 3, STONE_BRICK_STAIRS);
    set(arr, 2, 1, 1, STONE_SLAB);
    set(arr, 2, 1, 3, STONE_SLAB);
    return mirrorX(arr);

  }

  private static void set(Material[][][] arr, int y, int x, int z, Material material) {
    arr[y][x][z] = material;
  }

  private static Material[][][] mirrorX(Material[][][] arr) {
    for (int y = 0; y < arr.length; y++)
      for (int x = 0; x < arr[0].length / 2; x++)
        for (int z = 0; z < arr[0][0].length; z++) {
          arr[y][arr[0][0].length - 1 - x][z] = arr[y][x][z];
        }
    return arr;
  }

  private void runRitual(PlayerInteractEvent event) {
    final Player player = event.getPlayer();
    if (!player.hasPermission("de.altar.use") && !player.isOp())
      return;
    final Location clicked = event.getClickedBlock().getLocation();
    if (clicked.getWorld().getTime() < 12000)
      return;
    final Block center = event.getClickedBlock();
    clicked.add(.5, 1, .5);
    final Optional<Item> toEnchant;
    if ((toEnchant = (clicked.getWorld().getNearbyEntities(clicked, .5, .5, .5).stream().filter(entity ->
    {
      if (!(entity instanceof Item))
        return false;
      final ItemStack item = ((Item) entity).getItemStack();
      return item.getEnchantments().isEmpty() && item.getAmount() == 1 && Enchantments.isEnchantable(item.getType());

    }).map(entity -> (Item) entity).findFirst())).isPresent()) {

      final Item item = toEnchant.get();
      final ItemStack enchanting = item.getItemStack();
      final Map<Chest, Enchantment> enchantments = getEnchantmentsInChests(center);
      if (enchantments.isEmpty())
        return;
      final Map<Enchantment, Integer> amounts = new HashMap<>();
      for (Enchantment enchantment : enchantments.values()) {
        if (!enchantment.canEnchantItem(enchanting))
          return;
        amounts.put(enchantment, amounts.getOrDefault(enchantment, 0) + 1);
      }
      for (Map.Entry<Enchantment, Integer> entry : amounts.entrySet())
        if (entry.getValue() > entry.getKey().getMaxLevel())
          return;
      enchantments.keySet().forEach(chest -> chest.getBlockInventory().setItem(13, null));
      lightningStrikeRelativeTo(clicked, 6, 2, 2);
      enchanting.addEnchantments(amounts);
      item.setItemStack(enchanting);

      player.getInventory().setItemInMainHand(Items.modifyUsagesOfAltarItem(event.getItem()));
    }

  }

  private void lightningStrikeRelativeTo(Location center, int amt, int minOffset, int offsetRange) {
    while (amt > 0) {
      final Location location = new Location(center.getWorld(), new Random().nextInt(offsetRange) + minOffset, 0, new Random().nextInt(offsetRange) + minOffset);
      location.setY(center.getWorld().getHighestBlockYAt(location.getBlockX(), location.getBlockY()));
      center.getWorld().strikeLightningEffect(location);
      amt--;
    }
  }

  private Map<Chest, Enchantment> getEnchantmentsInChests(final Block redstoneCenter) {
    final Chest[] chests = new Chest[8];
    chests[0] = getChestRelativeToBlock(redstoneCenter, 2, 0, 1);
    chests[1] = getChestRelativeToBlock(redstoneCenter, 2, 0, -1);
    chests[2] = getChestRelativeToBlock(redstoneCenter, -2, 0, 1);
    chests[3] = getChestRelativeToBlock(redstoneCenter, -2, 0, -1);
    chests[4] = getChestRelativeToBlock(redstoneCenter, 1, 0, 2);
    chests[5] = getChestRelativeToBlock(redstoneCenter, 1, 0, -2);
    chests[6] = getChestRelativeToBlock(redstoneCenter, -1, 0, 2);
    chests[7] = getChestRelativeToBlock(redstoneCenter, -1, 0, -2);
    Map<Chest, Enchantment> enchantments = new HashMap<>();
    for (Chest chest : chests) {
      final ItemStack itemInChest = chest.getBlockInventory().getItem(13);
      if (itemInChest == null)
        continue;
      final Optional<String> enchString = NBTUtils.getString(itemInChest, "dark_enchantment");
      enchString.ifPresent(string -> enchantments.put(chest, Enchantment.getByKey(NamespacedKey.minecraft(string))));
    }
    return enchantments;
  }

  private Chest getChestRelativeToBlock(final Block block, int offsetx, int offsety, int offsetz) {
    return ((Chest) block.getRelative(offsetx, offsety, offsetz).getState());
  }
}
