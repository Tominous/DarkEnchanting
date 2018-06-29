package monotheistic.mongoose.darkenchanting;

import monotheistic.mongoose.core.CorePlugin;
import monotheistic.mongoose.core.components.commands.CommandManager;
import monotheistic.mongoose.core.components.commands.Help;
import monotheistic.mongoose.core.strings.PluginStrings;
import monotheistic.mongoose.darkenchanting.commands.DarknessRod;
import monotheistic.mongoose.darkenchanting.commands.Give;
import monotheistic.mongoose.darkenchanting.commands.SummonWitch;
import monotheistic.mongoose.darkenchanting.listeners.Items;
import monotheistic.mongoose.darkenchanting.listeners.Listeners;
import monotheistic.mongoose.darkenchanting.mobs.WitchMerchantFactory;
import monotheistic.mongoose.darkenchanting.ritual.Altar;
import monotheistic.mongoose.darkenchanting.ritual.Multiblocks;
import monotheistic.mongoose.darkenchanting.utils.Utilities;
import org.bukkit.ChatColor;

public class Main extends CorePlugin {


    public Main() {
        PluginStrings.setup(this, ChatColor.DARK_PURPLE, ChatColor.DARK_RED, "DarkEnchanting", "de");
        final WitchMerchantFactory witchMerchantFactory = new WitchMerchantFactory();
        final CommandManager commandManager = new CommandManager(this);
        addChild(new Items(this));
        addChild(new Utilities());
        addChild(new Listeners(this, witchMerchantFactory));
        addChild(commandManager).add(Help::new).add(new SummonWitch(witchMerchantFactory, this)).add(new Give()).add(new DarknessRod());
        addChild(new Multiblocks());
        onEnable(() -> {
            getCommand(PluginStrings.mainCmdLabel(false)).setExecutor(commandManager);
            Multiblocks.registerMultiblock(new Altar());
        });


    }

}
