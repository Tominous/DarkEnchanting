package monotheistic.mongoose.darkenchanting.listeners;

import com.gitlab.avelyn.architecture.base.Component;
import com.gitlab.avelyn.core.base.Events;
import monotheistic.mongoose.core.files.Configuration;
import monotheistic.mongoose.core.utils.ItemBuilder;
import monotheistic.mongoose.core.utils.MiscUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

class DropsListener extends Component {

  private Map<Tier, ChancedIntRange> killValues;

  DropsListener(Path dataFolder) {
    killValues = parseKills(dataFolder);
    addChild(Events.listen(this::onDeath));
  }

  private void onDeath(EntityDeathEvent event) {
    final LivingEntity entity = event.getEntity();
    if (entity.getKiller() == null)
      return;
    final Tier tier = Tier.getByEntity(entity);
    addDrops(killValues.get(tier), event);
  }

  private void addDrops(ChancedIntRange range, EntityDeathEvent event) {
    if (range == null)
      return;
    int num = range.getNumber();
    if (num == -1)
      return;
    event.getDrops().add(new ItemBuilder(Items.BLOOD_DROPLET()).amount(num).build());

  }


  private Map<Tier, ChancedIntRange> parseKills(Path dataFolder) {
    Map<Tier, ChancedIntRange> map = new HashMap<>();
    final FileConfiguration configuration = Configuration.loadConfiguration(this.getClass().getClassLoader(), dataFolder, "options.yml");
    final boolean playersEnabled = configuration.getBoolean("player-amount.enabled");
    final boolean hostilesEnabled = configuration.getBoolean("hostile-amount.enabled");
    final boolean friendliesEnabled = configuration.getBoolean("nonhostile-amount.enabled");
    if (playersEnabled) {
      final String[] playersStringArr = configuration.getString("player-amount.amount").split("-");
      map.put(Tier.PLAYER, new ChancedIntRange(configuration.getInt("player-amount.chance"), MiscUtils.parseInt(playersStringArr[0]).orElse(10), MiscUtils.parseInt(playersStringArr[1]).orElse(15)));
    }
    if (hostilesEnabled) {
      final String[] playersStringArr = configuration.getString("hostile-amount.amount").split("-");
      map.put(Tier.HOSTILE, new ChancedIntRange(configuration.getInt("hostile-amount.chance"), MiscUtils.parseInt(playersStringArr[0]).orElse(3), MiscUtils.parseInt(playersStringArr[1]).orElse(5)));
    }
    if (friendliesEnabled) {
      final String[] playersStringArr = configuration.getString("nonhostile-amount.amount").split("-");
      map.put(Tier.NON_HOSTILE, new ChancedIntRange(configuration.getInt("nonhostile-amount.chance"), MiscUtils.parseInt(playersStringArr[0]).orElse(1), MiscUtils.parseInt(playersStringArr[1]).orElse(3)));
    }
    return map;
  }

  private enum Tier {
    PLAYER,
    HOSTILE,
    NON_HOSTILE;


    private static Tier getByEntity(LivingEntity entity) {
      if (entity instanceof Monster)
        return HOSTILE;
      else if (entity instanceof Player)
        return PLAYER;
      else return NON_HOSTILE;
    }
  }

  private class ChancedIntRange {
    private int first;
    private int last;
    private int chance;

    private ChancedIntRange(int chance, int first, int last) {
      this.chance = chance;
      this.first = first;
      this.last = last;
    }

    private int getNumber() {
      final ThreadLocalRandom random = ThreadLocalRandom.current();
      if (random.nextInt(101) <= chance) {
        return random.nextInt(first, last + 1);
      } else return -1;
    }
  }
}
