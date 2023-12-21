package net.dotnomi.nuclearage.event;

import com.simibubi.create.foundation.utility.RaycastHelper;
import com.simibubi.create.foundation.utility.worldWrappers.RayTraceWorld;
import net.dotnomi.nuclearage.CreateNuclearAge;
import net.dotnomi.nuclearage.block.ModBlocks;
import net.dotnomi.nuclearage.effect.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.List;

public class ModEvents {

    @Mod.EventBusSubscriber(modid = CreateNuclearAge.MOD_ID)
    public static class ForgeEvents {

        @SubscribeEvent
        public static void onServerTick(TickEvent.ServerTickEvent event) {
            List<ServerPlayer> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();

            for (ServerPlayer player: players) {
                if (isNearRadioactiveBlock(player, player.level())) {
                    player.addEffect(new MobEffectInstance(ModEffects.RADIATION.get(), 5, 0));
                }
            }
        }

        private static boolean isNearRadioactiveBlock(Entity entity, Level world) {
            BlockPos entityPosition = new BlockPos(entity.getOnPos());
            int radius = 12;

            for (BlockPos blockPosition : BlockPos.betweenClosed(
                    entityPosition.offset(-radius, -radius, -radius),
                    entityPosition.offset(radius, radius, radius))) {
                if (ModBlocks.getLevelOneRadiationBlocks().contains(world.getBlockState(blockPosition).getBlock())) {
                    if (!isShieldedFromRadiation(entity, world, blockPosition)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private static boolean isShieldedFromRadiation(Entity entity, Level world, BlockPos radiationSource) {
            if (entity instanceof Player player) {
                Vec3 entityEyePosition = entity.getEyePosition();
                Vec3 entityLeftArmPosition = getArmPosition(player, true);
                Vec3 entityRightArmPosition = getArmPosition(player, false);
                Vec3 entityFootPosition = entity.position().add(0,0.5F,0);
                Vec3 radiationSourcePosition = Vec3.atCenterOf(radiationSource);

                BlockHitResult eyeHitResult = world.clip(
                        new ClipContext(entityEyePosition, radiationSourcePosition, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)
                );

                BlockHitResult leftArmHitResult = world.clip(
                        new ClipContext(entityLeftArmPosition, radiationSourcePosition, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)
                );

                BlockHitResult rightArmHitResult = world.clip(
                        new ClipContext(entityRightArmPosition, radiationSourcePosition, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)
                );

                BlockHitResult footHitResult = world.clip(
                        new ClipContext(entityFootPosition, radiationSourcePosition, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)
                );

                return isRayBlocked(eyeHitResult, world)
                        && isRayBlocked(leftArmHitResult, world)
                        && isRayBlocked(rightArmHitResult, world)
                        && isRayBlocked(footHitResult, world);
            } else if (entity instanceof Mob mob) {
                Vec3 entityPosition = entity.position().add(0,0.5F,0);
                Vec3 radiationSourcePosition = Vec3.atCenterOf(radiationSource);

                BlockHitResult hitResult = world.clip(
                        new ClipContext(entityPosition, radiationSourcePosition, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)
                );

                return isRayBlocked(hitResult, world);
            }
            return false;
        }

        private static boolean isRayBlocked(BlockHitResult blockHitResult, Level world) {
            if (blockHitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos hitBlockPosition = blockHitResult.getBlockPos();
                BlockState hitBlockState = world.getBlockState(hitBlockPosition);

                return (!ModBlocks.getLevelOneRadiationBlocks().contains(hitBlockState.getBlock()) && hitBlockState.getBlock() != Blocks.AIR);
            }
            return false;
        }

        private static Vec3 getArmPosition(Player player, boolean isLeftArm) {
            float yaw = player.getYRot();
            float armOffset = isLeftArm ? 0.35F : -0.35F;
            float radianYaw = (float) Math.toRadians(yaw);

            double offsetX = Math.sin(radianYaw) * armOffset;
            double offsetZ = Math.cos(radianYaw) * armOffset;

            return player.position().add(offsetX, 1.5, offsetZ);
        }
    }

    @Mod.EventBusSubscriber(modid = CreateNuclearAge.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventBusEvents {

    }

}
