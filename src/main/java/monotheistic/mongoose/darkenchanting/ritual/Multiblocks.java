package monotheistic.mongoose.darkenchanting.ritual;

import com.gitlab.avelyn.architecture.base.Component;
import com.gitlab.avelyn.core.base.Events;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashSet;
import java.util.Set;

public class Multiblocks extends Component {
    private static Set<SymmetricMultiblock> symmetricMultiblocks;

    public Multiblocks() {
        onEnable(() -> symmetricMultiblocks = new HashSet<>());
        onDisable(() -> symmetricMultiblocks = null);
        addChild(Events.listen((PlayerInteractEvent event) -> {
            if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND)
                return;
            for (SymmetricMultiblock symmetricMultiblock : symmetricMultiblocks) {
                if (symmetricMultiblock.check(event.getClickedBlock(), event.getItem()))
                    symmetricMultiblock.doAction(event);
            }
        }));
    }

    public static void registerMultiblock(SymmetricMultiblock symmetricMultiblock) {
        symmetricMultiblocks.add(symmetricMultiblock);
    }
}
