package monotheistic.mongoose.darkenchanting;

import monotheistic.mongoose.core.components.commands.PluginInfo;
import org.bukkit.ChatColor;

public interface Info {
  PluginInfo INFO = new PluginInfo("DarkEnchanting", "de", ChatColor.DARK_PURPLE, ChatColor.DARK_RED);

  static String mustBeAPlayer() {
    return INFO.getDisplayName() + ChatColor.RED + " You must be a player to use this command!";
  }
}
