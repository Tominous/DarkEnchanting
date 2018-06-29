package monotheistic.mongoose.darkenchanting.commands;

import monotheistic.mongoose.core.components.commands.SubCommand;
import monotheistic.mongoose.core.files.Configuration;
import monotheistic.mongoose.core.strings.PluginStrings;
import monotheistic.mongoose.darkenchanting.Main;
import monotheistic.mongoose.darkenchanting.mobs.WitchMerchantFactory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static monotheistic.mongoose.core.strings.PluginStrings.mustBeAPlayer;
import static org.bukkit.block.BlockFace.DOWN;
import static org.bukkit.block.BlockFace.UP;

public class SummonWitch extends SubCommand {

    private WitchMerchantFactory factory;
    private Map<UUID, Instant> cooldowns;
    private int cooldownMins;

    public SummonWitch(WitchMerchantFactory factory, Main main) {
        super("summon", "Summons the witch merchant", "summon", 3);
        cooldownMins = new Configuration(main, "options.yml").configuration().getInt("summon witch cooldown");
        this.factory = factory;
        cooldowns = new HashMap<>();
    }

    @Override
    protected boolean onCommand(CommandSender commandSender, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(mustBeAPlayer());
            return false;
        }
        final Player player = (Player) commandSender;
        final Instant now = Instant.now();
        Instant last = cooldowns.get(player.getUniqueId());
        if ((last != null && !last.isBefore(now)) && (!(player.hasPermission(requiredPermissions(false) + ".bypasscooldown") || player.isOp()))) {
            player.sendMessage(PluginStrings.tag() + " You must wait " + cooldownMins + " minutes between each use of this command!");
        } else if (spawnWitchBy(player)) {
            cooldowns.put(player.getUniqueId(), now.plus(cooldownMins, ChronoUnit.MINUTES));
        }
        return true;
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
