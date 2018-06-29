package monotheistic.mongoose.darkenchanting.commands;

import monotheistic.mongoose.core.components.commands.SubCommand;
import monotheistic.mongoose.core.strings.PluginStrings;
import monotheistic.mongoose.core.utils.ItemBuilder;
import monotheistic.mongoose.core.utils.MiscUtils;
import monotheistic.mongoose.darkenchanting.listeners.Items;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Give extends SubCommand {
    public Give() {
        super("givedrops", "Gives drops to target player", "givedrops [player] [amount]", 2);
    }

    @Override
    protected boolean onCommand(CommandSender commandSender, String[] strings) {
        if (strings.length != 2) {
            commandSender.sendMessage(invalidSyntax());
            return false;
        }

        int amount = MiscUtils.parseInt(strings[1]).orElse(-1);
        if (amount <= 0) {
            commandSender.sendMessage(invalidSyntax());
            return false;
        }
        final Player player = Bukkit.getPlayer(strings[0]);
        if (player == null) {
            commandSender.sendMessage(PluginStrings.tag() + " Target player could not be found!");
            return true;
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
        commandSender.sendMessage(PluginStrings.tag()+" Success.");
        return true;
    }

    private void addOrDrop(Player player, ItemStack item) {
        if (player.getInventory().addItem(item).isEmpty())
            return;
        player.getLocation().getWorld().dropItem(player.getLocation(), item);
    }

}
