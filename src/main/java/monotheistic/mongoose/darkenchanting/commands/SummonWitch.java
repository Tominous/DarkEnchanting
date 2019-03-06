package monotheistic.mongoose.darkenchanting.commands;


import monotheistic.mongoose.core.components.commands.CommandPart;
import monotheistic.mongoose.core.components.commands.CommandPartInfo;
import monotheistic.mongoose.core.components.commands.PluginInfo;
import monotheistic.mongoose.core.files.Configuration;
import monotheistic.mongoose.darkenchanting.Info;
import monotheistic.mongoose.darkenchanting.mobs.WitchMerchantFactory;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.util.Optional.of;
import static monotheistic.mongoose.darkenchanting.Info.mustBeAPlayer;
import static org.bukkit.block.BlockFace.DOWN;
import static org.bukkit.block.BlockFace.UP;

public class SummonWitch extends CommandPart {

  private WitchMerchantFactory factory;
  private Map<UUID, Instant> cooldowns;
  private int cooldownMins;

  public SummonWitch(WitchMerchantFactory factory, Path dataFolder) {
    super(new CommandPartInfo("Summons the witch merchant", "summon", "summon", 0));
    cooldownMins = Configuration.loadConfiguration(this.getClass().getClassLoader(), dataFolder, "options.yml").getInt("summon witch cooldown");
    this.factory = factory;
    cooldowns = new HashMap<>();
  }

  @Override
  protected @NotNull Optional<Boolean> initExecute(CommandSender commandSender, String s, String[] strings, PluginInfo pluginInfo, List<Object> list) {
    if (!(commandSender instanceof Player)) {
      commandSender.sendMessage(mustBeAPlayer());
      return of(false);
    }
    final Player player = (Player) commandSender;
    final Instant now = Instant.now();
    Instant last = cooldowns.get(player.getUniqueId());
    if ((last != null && !last.isBefore(now)) && (!(player.hasPermission(Info.INFO.getTag() + ".bypasscooldown") || player.isOp()))) {
      player.sendMessage(Info.INFO.getDisplayName() + ChatColor.RED + " You must wait " + cooldownMins + " minutes between each use of this command!");
    } else if (spawnWitchBy(player)) {
      cooldowns.put(player.getUniqueId(), now.plus(cooldownMins, ChronoUnit.MINUTES));
    }
    return of(true);
  }

  private boolean spawnWitchBy(Player player) {
    final Location toSpawn = player.getLocation().add(new Random().nextInt(3), 0, new Random().nextInt(3));
    if (toSpawn.getBlock().getType() != Material.AIR ||
            toSpawn.getBlock().getRelative(UP).getType() != Material.AIR
            || !toSpawn.getBlock().getRelative(DOWN).getType().isSolid())
      return false;
    toSpawn.setYaw(player.getLocation().getYaw() + 180);
    toSpawn.setPitch(0);
    toSpawn.setX(toSpawn.getX() + .5);
    toSpawn.setZ(toSpawn.getZ() + .5);
    factory.spawn(toSpawn);
    player.playSound(player.getLocation(), Sound.ENTITY_WITCH_DEATH, .5f, .5f);
    return true;
  }


}
