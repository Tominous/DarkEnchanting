package monotheistic.mongoose.darkenchanting.commands;

import monotheistic.mongoose.core.components.commands.CommandPart;
import monotheistic.mongoose.core.components.commands.CommandPartInfo;
import monotheistic.mongoose.core.components.commands.PluginInfo;
import monotheistic.mongoose.core.utils.ItemBuilder;
import monotheistic.mongoose.core.utils.MiscUtils;
import monotheistic.mongoose.darkenchanting.Info;
import monotheistic.mongoose.darkenchanting.listeners.Items;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.of;

public class Give extends CommandPart {
  public Give() {
    super(new CommandPartInfo("Gives drops to target player", "givedrops [player] [amt]", "givedrops", 2));
  }

  @Override
  protected @NotNull Optional<Boolean> initExecute(CommandSender commandSender, String s, String[] strings, PluginInfo pluginInfo, List<Object> list) {
    if (strings.length != 2) {
      commandSender.sendMessage(incorrectUsageMessage(pluginInfo));
      return of(true);
    }

    int amount = MiscUtils.parseInt(strings[1]).orElse(-1);
    if (amount <= 0) {
      commandSender.sendMessage(incorrectUsageMessage(pluginInfo));
      return of(false);
    }
    final Player player = Bukkit.getPlayer(strings[0]);
    if (player == null) {
      commandSender.sendMessage(Info.INFO.getDisplayName() + " Target player could not be found!");
      return of(true);
    }

    while (amount > 0) {
      if (amount >= 64) {
        addOrDrop(player, ItemBuilder.copyOf(Items.BLOOD_DROPLET()).amount(64).build());
        amount -= 64;
      } else {
        addOrDrop(player, ItemBuilder.copyOf(Items.BLOOD_DROPLET()).amount(amount).build());
        break;
      }
    }
    commandSender.sendMessage(Info.INFO.getDisplayName() + " Success.");
    return of(true);
  }

  private void addOrDrop(Player player, ItemStack item) {
    if (player.getInventory().addItem(item).isEmpty())
      return;
    player.getLocation().getWorld().dropItem(player.getLocation(), item);
  }
}
