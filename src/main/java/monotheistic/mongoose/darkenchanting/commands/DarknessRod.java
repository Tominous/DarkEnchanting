package monotheistic.mongoose.darkenchanting.commands;

import monotheistic.mongoose.core.components.commands.SubCommand;
import monotheistic.mongoose.core.strings.PluginStrings;
import monotheistic.mongoose.darkenchanting.listeners.Items;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class DarknessRod extends SubCommand {
    public DarknessRod() {
        super("darknessrod", "Gives the rod of darkness", "darknessrod", 7);
    }

    @Override
    protected boolean onCommand(CommandSender commandSender, String[] strings) {
        if (strings.length != 0) {
            commandSender.sendMessage(invalidSyntax());
            return false;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(PluginStrings.mustBeAPlayer());
            return true;
        }
        Optional<ItemStack> rod = Items.ALTAR_INTERACT(false);
        rod.ifPresent(item -> addOrDrop((Player) commandSender, item));
        return true;
    }

    private void addOrDrop(Player player, ItemStack item) {
        if (player.getInventory().addItem(item).isEmpty())
            return;
        player.getLocation().getWorld().dropItem(player.getLocation(), item);
    }
}
