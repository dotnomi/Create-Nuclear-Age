package net.dotnomi.nuclearage.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class RadiationEffect extends MobEffect {
    public RadiationEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
        int amplifier = Math.min(Math.max(pAmplifier, 0), 2);

        if (pLivingEntity instanceof Player player) player.causeFoodExhaustion(0.005F * (float)(amplifier + 1));
        if (pLivingEntity.getHealth() > 1.0F) pLivingEntity.hurt(pLivingEntity.damageSources().magic(),(amplifier + (float) 1) / 4);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }
}
