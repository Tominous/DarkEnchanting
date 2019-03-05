package monotheistic.mongoose.darkenchanting.listeners;

import com.gitlab.avelyn.architecture.base.Component;
import monotheistic.mongoose.darkenchanting.mobs.WitchMerchantFactory;

import java.nio.file.Path;

public class Listeners extends Component {
    public Listeners(Path dataFolder, WitchMerchantFactory factory) {
        addChild(new DropsListener(dataFolder));
        addChild(new WitchInteraction(dataFolder, factory));
}
}
