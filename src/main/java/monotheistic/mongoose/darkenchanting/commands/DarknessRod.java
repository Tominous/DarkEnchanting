package monotheistic.mongoose.darkenchanting.commands;

import monotheistic.mongoose.core.components.commands.CommandPart;
import monotheistic.mongoose.core.components.commands.CommandPartInfo;
import monotheistic.mongoose.core.components.commands.PluginInfo;
import monotheistic.mongoose.darkenchanting.listeners.Items;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.of;
import static monotheistic.mongoose.darkenchanting.Info.INFO;

public class DarknessRod extends CommandPart {

  public DarknessRod() {
    super(new CommandPartInfo("Gives the road of darkness", "darknessrod", "darknessrod", 0));
  }

  @Override
  protected @NotNull Optional<Boolean> initExecute(CommandSender commandSender, String s, String[] strings, PluginInfo pluginInfo, List<Object> list) {
    if (strings.length != 0) {
      commandSender.sendMessage(incorrectUsageMessage(pluginInfo));
      return of(true);
    }
    if (!(commandSender instanceof Player)) {
      commandSender.sendMessage(INFO.getDisplayName() + ChatColor.RED + " You must be a player to use this command!");
      return of(true);
    }
    Optional<ItemStack> rod = Items.ALTAR_INTERACT(false);
    rod.ifPresent(item -> addOrDrop((Player) commandSender, item));
    return of(true);
  }

  private void addOrDrop(Player player, ItemStack item) {
    if (player.getInventory().addItem(item).isEmpty())
      return;
    player.getLocation().getWorld().dropItem(player.getLocation(), item);
  }
}
