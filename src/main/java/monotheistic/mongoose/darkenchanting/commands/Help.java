package monotheistic.mongoose.darkenchanting.commands;

import monotheistic.mongoose.core.components.commands.CommandPart;
import monotheistic.mongoose.core.components.commands.CommandPartInfo;
import monotheistic.mongoose.core.components.commands.PluginInfo;
import monotheistic.mongoose.core.utils.MiscUtils;
import monotheistic.mongoose.darkenchanting.Info;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.of;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;

public class Help extends CommandPart {
  private final List<CommandPart> cmds = new ArrayList<>();
  private final int pageAmt;

  public Help(CommandPart... commands) {
    super(new CommandPartInfo("Help Command", "help", "help", 1));
    cmds.addAll(Arrays.asList(commands));
    cmds.add(this);
    this.pageAmt = cmds.size() / 7 + cmds.size() % 7 > 0 ? 1 : 0;
  }

  @Override
  protected @NotNull Optional<Boolean> initExecute(CommandSender commandSender, String s, String[] strings, PluginInfo pluginInfo, List<Object> list) {
    int page;
    if (strings.length < 1) {
      page = 1;
    } else page = MiscUtils.parseInt(strings[0]).orElse(1);
    final ChatColor mainColor = Info.INFO.getMainColor();
    final ChatColor secondColor = Info.INFO.getSecondaryColor();
    if (page > pageAmt) {
      commandSender.sendMessage(Info.INFO.getDisplayName() + ": " + RED + "Page out of range!");
      return of(true);
    }
    StringBuilder builder = new StringBuilder();
    builder.append(String.format("-----" + Info.INFO.getDisplayName() + WHITE + " Commands %s/%s-----\n", page, pageAmt));
    cmds.stream().skip((page - 1) * 7).forEach(cmd ->
            builder.append(mainColor).append(cmd.getPartName()).append("->").append(secondColor)
                    .append(cmd.getPartDescription()).append("\n")
    );
    commandSender.sendMessage(builder.toString());
    return of(true);
  }

}

