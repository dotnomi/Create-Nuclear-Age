package net.dotnomi.nuclearage.effect;

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
                        if (pAmplifier == 0) stageAmplifier = stageOneAmplifier;
            else if (pAmplifier == 1) stageAmplifier = stageTwoAmplifier;
            else if (pAmplifier >= 2) stageAmplifier = stageThreeAmplifier;
            if (pLivingEntity instanceof Player player) player.causeFoodExhaustion(stageAmplifier);
            pLivingEntity.hurt(pLivingEntity.damageSources().magic(), stageAmplifier * 2);

            tickTimer = 0;
        }
        tickTimer++;
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }
}
