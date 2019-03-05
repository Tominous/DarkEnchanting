package monotheistic.mongoose.darkenchanting.ritual;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Predicate;

abstract class SymmetricMultiblock {

  private final Material[][][] layout;
  private final Material trigger;
  private final Triple<Integer, Integer, Integer> triggerPos;
  private final Predicate<ItemStack> triggerItemChecker;
  private Consumer<PlayerInteractEvent> eventConsumer;

  private SymmetricMultiblock(Material[][][] layout, Material trigger, Predicate<ItemStack> triggerItem, Triple<Integer, Integer, Integer> triggerPos, Consumer<PlayerInteractEvent> onClick) {
    this.layout = layout;
    this.trigger = trigger;
    this.triggerPos = triggerPos;
    this.eventConsumer = onClick;
    this.triggerItemChecker = triggerItem;
  }

  SymmetricMultiblock(Material[][][] layout, Material trigger, Predicate<ItemStack> triggerItem, Triple<Integer, Integer, Integer> triggerPos) {
    this(layout, trigger, triggerItem, triggerPos, null);
  }

  void setAction(Consumer<PlayerInteractEvent> action) {
    this.eventConsumer = action;
  }

  boolean check(Block clickedBlock, ItemStack clickedWith) {
    if (clickedBlock.getType() != trigger)
      return false;
    if (!triggerItemChecker.test(clickedWith))
      return false;
    final int xDiff = -triggerPos.getLeft();
    final int yDiff = -triggerPos.getMiddle();
    final int zDiff = -triggerPos.getRight();
    return iterateFrom(clickedBlock.getRelative(xDiff, yDiff, zDiff));
  }

  private boolean iterateFrom(final Block topLeftBottomLayer) {

    for (int y = 0; y < layout.length; y++) {
      for (int x = 0; x < layout[0].length; x++) {
        for (int z = 0; z < layout[0][0].length; z++) {
          final Block relative = topLeftBottomLayer.getRelative(x, y, z);
          if (layout[y][x][z] == null) continue;
          if (layout[y][x][z] != relative.getType())
            return false;
        }
      }
    }
    return true;

  }


  void doAction(PlayerInteractEvent event) {
    this.eventConsumer.accept(event);
  }

  static class Triple<L, M, R> {
    private final L left;
    private final M middle;
    private final R right;

    Triple(L left, M middle, R right) {
      this.left = left;
      this.middle = middle;
      this.right = right;
    }

    private L getLeft() {
      return left;
    }

    private M getMiddle() {
      return middle;
    }

    private R getRight() {
      return right;
    }
  }
}
