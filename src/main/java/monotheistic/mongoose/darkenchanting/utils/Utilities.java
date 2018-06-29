package monotheistic.mongoose.darkenchanting.utils;

import com.gitlab.avelyn.architecture.base.Component;

public class Utilities extends Component {
    public Utilities(){
        addChild(new Enchantments());
        addChild(new Gear());
    }
}
