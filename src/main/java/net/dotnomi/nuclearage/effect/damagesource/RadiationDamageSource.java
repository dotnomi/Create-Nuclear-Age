package net.dotnomi.nuclearage.effect.damagesource;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;

public class RadiationDamageSource extends DamageSource {
    public RadiationDamageSource(DamageType damageType) {
        super(Holder.direct(damageType));
    }

    public RadiationDamageSource(DamageType damageType, Entity entity) {
        super(Holder.direct(damageType), null, entity);
    }
}
