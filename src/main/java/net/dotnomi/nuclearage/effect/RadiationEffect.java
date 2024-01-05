package net.dotnomi.nuclearage.effect;

import com.simibubi.create.foundation.damageTypes.DamageTypeBuilder;
import net.dotnomi.nuclearage.effect.damagesource.RadiationDamageSource;
import net.dotnomi.nuclearage.effect.damagetype.ModDamageTypes;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class RadiationEffect extends MobEffect {

    final float stageOneAmplifier = 1.0F / 64;
    final float stageTwoAmplifier = 1.0F / 16;
    final float stageThreeAmplifier = 1.0F / 2;
    private int tickTimer;

    public RadiationEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
        float stageAmplifier = 0;

        if (tickTimer % 5 == 0) {
            DamageType damageType = new DamageTypeBuilder(DamageTypes.GENERIC).msgId("radiation").build();
            DamageType killDamageType = new DamageTypeBuilder(DamageTypes.GENERIC_KILL).msgId("radiation.player").build();

            DamageSource damageSource = new DamageSource(Holder.direct(damageType));

            if (pLivingEntity instanceof Player player) {
                if (pLivingEntity.getLastAttacker() != null && player.getHealth() <= 1) {
                    damageSource = new DamageSource(Holder.direct(killDamageType), pLivingEntity.getLastAttacker());
                } else {
                    damageSource = new DamageSource(Holder.direct(damageType));
                }
            }

            if (pAmplifier == 0) stageAmplifier = stageOneAmplifier;
            else if (pAmplifier == 1) stageAmplifier = stageTwoAmplifier;
            else if (pAmplifier >= 2) stageAmplifier = stageThreeAmplifier;
            if (pLivingEntity instanceof Player player) player.causeFoodExhaustion(stageAmplifier);
            pLivingEntity.hurt(damageSource, stageAmplifier * 2);

            tickTimer = 0;
        }
        tickTimer++;
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }
}
