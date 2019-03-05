package monotheistic.mongoose.darkenchanting;

import com.gitlab.avelyn.core.components.ComponentPlugin;
import monotheistic.mongoose.core.components.commands.CommandPart;
import monotheistic.mongoose.core.components.commands.CommandSelector;
import monotheistic.mongoose.darkenchanting.commands.DarknessRod;
import monotheistic.mongoose.darkenchanting.commands.Give;
import monotheistic.mongoose.darkenchanting.commands.Help;
import monotheistic.mongoose.darkenchanting.commands.SummonWitch;
import monotheistic.mongoose.darkenchanting.listeners.Items;
import monotheistic.mongoose.darkenchanting.listeners.Listeners;
import monotheistic.mongoose.darkenchanting.mobs.WitchMerchantFactory;
import monotheistic.mongoose.darkenchanting.ritual.Altar;
import monotheistic.mongoose.darkenchanting.ritual.Multiblocks;
import monotheistic.mongoose.darkenchanting.utils.Utilities;

public class Main extends ComponentPlugin {
  public Main() {
    final WitchMerchantFactory witchMerchantFactory = new WitchMerchantFactory();
    CommandPart[] otherCmds = {new DarknessRod(), new Give(), new SummonWitch(witchMerchantFactory, getDataFolder().toPath())};
    Help help = new Help(otherCmds);
    final CommandSelector selector = new CommandSelector(Info.INFO, help);
    selector.addChild(help);
    selector.addChild(otherCmds);
    addChild(new Items(getDataFolder().toPath()));
    addChild(new Utilities());
    addChild(new Listeners(getDataFolder().toPath(), witchMerchantFactory));
    addChild(selector);
    addChild(new Multiblocks());

    onEnable(() -> {
      Multiblocks.registerMultiblock(new Altar());
      getCommand("de").setExecutor(selector);
    });

  }

}
