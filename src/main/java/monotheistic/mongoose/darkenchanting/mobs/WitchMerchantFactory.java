package monotheistic.mongoose.darkenchanting.mobs;


import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Witch;

import java.util.UUID;

public class WitchMerchantFactory{
    private final AttributeModifier identifier;


    public WitchMerchantFactory() {

        identifier = new AttributeModifier(UUID.fromString("14ce92cf-bf5d-49f1-afdc-aa23239e80ee"), "yona168witchid", 0, AttributeModifier.Operation.ADD_NUMBER);
    }

    public void spawn(final Location location) {
        location.getWorld().strikeLightningEffect(location);
        final Witch witch = (Witch) new MobWrapper(EntityType.WITCH, location).
                ai(false).nameFrom(ChatColor.LIGHT_PURPLE, "Helena", "Urgatha", "Shelly", "Henrietta").setRemoveWhenFar(true).pickupItems(false).get();
        witch.getAttribute(Attribute.GENERIC_ARMOR).addModifier(identifier);
    }

    public boolean isCustomEntity(Witch witch) {
        for (AttributeModifier modifier : witch.getAttribute(Attribute.GENERIC_ARMOR).getModifiers()) {
            if (modifier.getUniqueId().equals(identifier.getUniqueId()))
                return true;
        }
        return false;
    }
}


