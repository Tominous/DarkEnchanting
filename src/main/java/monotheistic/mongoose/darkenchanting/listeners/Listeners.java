package monotheistic.mongoose.darkenchanting.listeners;

import com.gitlab.avelyn.architecture.base.Component;
import monotheistic.mongoose.darkenchanting.Main;
import monotheistic.mongoose.darkenchanting.mobs.WitchMerchantFactory;

public class Listeners extends Component {
public Listeners(Main main, WitchMerchantFactory factory){
    addChild(new DropsListener(main));
    addChild(new WitchInteraction(main, factory));
}
}
