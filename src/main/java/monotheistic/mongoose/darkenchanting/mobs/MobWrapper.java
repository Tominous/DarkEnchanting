package monotheistic.mongoose.darkenchanting.mobs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MobWrapper {
    private LivingEntity livingEntity;

    public MobWrapper(EntityType type, Location location) {
        final Entity entity = location.getWorld().spawnEntity(location, type);
        if (!(entity instanceof LivingEntity)) {
            throw new IllegalArgumentException("EntityType must be of a LivingEntity!");
        } else this.livingEntity = (LivingEntity) entity;
    }

    public MobWrapper(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
    }

    public MobWrapper setRemoveWhenFar(boolean set) {
        livingEntity.setRemoveWhenFarAway(set);
        return this;
    }

    public MobWrapper name(String name) {
        livingEntity.setCustomName(name);
        livingEntity.setCustomNameVisible(true);
        return this;
    }

    public MobWrapper nameFrom(ChatColor color, String... names) {
        name(color + names[new Random().nextInt(names.length)]);
        return this;
    }

    public MobWrapper ai(boolean ai) {
        livingEntity.setAI(ai);

        return this;
    }

    public MobWrapper pickupItems(boolean pickup) {
        livingEntity.setCanPickupItems(pickup);
        return this;
    }

    public MobWrapper gravity(boolean gravity) {
        livingEntity.setGravity(gravity);
        return this;
    }

    public MobWrapper velocity(Vector velocity) {
        livingEntity.setVelocity(velocity);
        return this;
    }

    public MobWrapper glowing(boolean glowing) {
        livingEntity.setGlowing(glowing);
        return this;
    }

    public MobWrapper gliding(boolean gliding) {
        livingEntity.setGliding(gliding);
        return this;
    }

    public MobWrapper setLeashHolder(Entity o) {
        livingEntity.setLeashHolder(o);
        return this;
    }

    public MobWrapper potionEffect(PotionEffect effect) {
        livingEntity.addPotionEffect(effect);
        return this;
    }

    public MobWrapper potionEffect(PotionEffect effect, boolean replaceOthers) {
        livingEntity.addPotionEffect(effect, replaceOthers);
        return this;
    }

    public MobWrapper potionEffects(Collection<PotionEffect> effects) {
        livingEntity.addPotionEffects(effects);
        return this;
    }

    public MobWrapper canPickup(boolean canPickup){
        livingEntity.setCanPickupItems(canPickup);
        return this;
    }

    public MobWrapper potionEffects(PotionEffect... effects) {
        potionEffects(Arrays.asList(effects));
        return this;
    }

    public MobWrapper customNameVisible(boolean visible) {
        livingEntity.setCustomNameVisible(visible);
        return this;
    }

    public MobWrapper doIf(Predicate<MobWrapper> condition, Consumer<MobWrapper> action) {
        if (condition.test(this))
            action.accept(this);
        return this;
    }

    public LivingEntity get() {
        return this.livingEntity;
    }
}
