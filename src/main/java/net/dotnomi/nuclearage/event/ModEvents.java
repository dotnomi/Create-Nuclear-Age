package net.dotnomi.nuclearage.event;

import net.dotnomi.nuclearage.CreateNuclearAge;
import net.dotnomi.nuclearage.block.ModBlocks;
import net.dotnomi.nuclearage.effect.ModEffects;
import net.dotnomi.nuclearage.util.VectorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;

/*
 * TODO: Item Container on Ground Bug - forever radiation
 */

public class ModEvents {

    @Mod.EventBusSubscriber(modid = CreateNuclearAge.MOD_ID)
    public static class ForgeEvents {

        private static final List<Block> defaultExclusionList = List.of(
                Blocks.AIR,
                Blocks.VOID_AIR,
                Blocks.STRUCTURE_VOID
        );

        @SubscribeEvent
        public static void onServerTick(TickEvent.ServerTickEvent event) {
            List<ServerPlayer> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
            MobEffectInstance radiationEffectInstance = new MobEffectInstance(ModEffects.RADIATION.get(), 5, 0);

            for (ServerPlayer player : players) {
                Level world = player.level();
                BlockPos playerPosition = player.getOnPos();
                int radiationEntityRadius = 2 * 16; // 4 chunks radius

                if (hasRadioavtiveItemsInInventory(player))
                    player.addEffect(radiationEffectInstance);

                List<Entity> entities = world.getEntitiesOfClass(Entity.class, new AABB(
                        playerPosition.offset(-radiationEntityRadius, -radiationEntityRadius, -radiationEntityRadius),
                        playerPosition.offset(radiationEntityRadius, radiationEntityRadius, radiationEntityRadius)
                ));

                for (Entity entity : entities) {
                    if ((entity instanceof LivingEntity livingEntity)) {
                        if (isNearRadiactiveItem(entity, world, player))
                            livingEntity.addEffect(radiationEffectInstance);
                        if (isNearRadioactiveBlock(entity, world))
                            livingEntity.addEffect(radiationEffectInstance);
                        if (isNearRadioactiveContainer(entity, world))
                            livingEntity.addEffect(radiationEffectInstance);
                    }
                }
            }
        }

        private static boolean hasRadioavtiveItemsInInventory(Player player) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack itemStack = player.getInventory().getItem(i);

                if (isItemContainerRadioavtive(itemStack) || isItemRadioactive(itemStack)) {
                    if (itemStack.getItem() != Items.AIR) return true;
                }
            }

            return false;
        }

        private static boolean isItemContainerRadioavtive(ItemStack itemStack) {
            LazyOptional<IItemHandler> itemHandlerOptional = itemStack.getCapability(CapabilityManager.get(new CapabilityToken<>() {}));

            if (itemHandlerOptional.isPresent()) {
                IItemHandler itemHandler = itemHandlerOptional.orElse(null);
                if (itemHandler != null) {
                    for (int i = 0; i < itemHandler.getSlots(); i++) {
                        if (isItemRadioactive(itemHandler.getStackInSlot(i))) {
                            return true;
                        }
                    }
                }
            }

            return false;
        }

        private static boolean isNearRadiactiveItem(Entity entity, Level world, Player player) {
            BlockPos entityPosition = new BlockPos(entity.getOnPos());
            int radius = 12;

            List<ItemEntity> itemEntities = world.getEntitiesOfClass(ItemEntity.class, new AABB(
                    entityPosition.offset(-radius, -radius, -radius),
                    entityPosition.offset(radius, radius, radius)
            ));

            for (ItemEntity itemEntity : itemEntities) {
                ItemStack itemStack = itemEntity.getItem();

                if (isItemContainerRadioavtive(itemStack) || isItemRadioactive(itemStack)) {
                    if (!isShieldedFromRadiation(entity, world, new BlockPos(VectorHelper.getBlockPosition(itemEntity.getEyePosition())), defaultExclusionList)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private static boolean isNearRadioactiveBlock(Entity entity, Level world) {
            BlockPos entityPosition = new BlockPos(entity.getOnPos());
            int radius = 12;

            for (BlockPos blockPosition : BlockPos.betweenClosed(
                    entityPosition.offset(-radius, -radius, -radius),
                    entityPosition.offset(radius, radius, radius))) {

                if (ModBlocks.getLevelOneRadiationBlocks().contains(world.getBlockState(blockPosition).getBlock())) {
                    List<Block> exclusionList = new ArrayList<>(defaultExclusionList);
                    exclusionList.addAll(ModBlocks.getLevelOneRadiationBlocks());

                    if (!isShieldedFromRadiation(entity, world, blockPosition, exclusionList)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private static boolean isNearRadioactiveContainer(Entity entity, Level world) {
            BlockPos entityPosition = new BlockPos(entity.getOnPos());
            int radius = 12;

            for (BlockPos blockPosition : BlockPos.betweenClosed(
                    entityPosition.offset(-radius, -radius, -radius),
                    entityPosition.offset(radius, radius, radius))) {
                BlockEntity blockEntity = world.getBlockEntity(blockPosition);

                if (blockEntity != null) {
                    LazyOptional<IItemHandler> itemHandlerOptional = blockEntity.getCapability(CapabilityManager.get(new CapabilityToken<>() {}));

                    if (itemHandlerOptional.isPresent()) {
                        IItemHandler itemHandler = itemHandlerOptional.orElse(null);

                        for (int i = 0; i < itemHandler.getSlots(); i++) {
                            ItemStack itemStack = itemHandler.getStackInSlot(i);
                            BlockState blockState = world.getBlockState(blockPosition);

                            if (isItemContainerRadioavtive(itemStack) || isItemRadioactive(itemStack)) {
                                List<Block> exclusionList = defaultExclusionList;
                                exclusionList.add(blockState.getBlock());

                                if (!isShieldedFromRadiation(entity, world, blockPosition, exclusionList)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
            return false;
        }

        private static boolean isShieldedFromRadiation(Entity entity, Level world, BlockPos radiationSource, List<Block> shieldExclusionList) {
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

                return isRayBlocked(eyeHitResult, world, shieldExclusionList)
                        && isRayBlocked(leftArmHitResult, world, shieldExclusionList)
                        && isRayBlocked(rightArmHitResult, world, shieldExclusionList)
                        && isRayBlocked(footHitResult, world, shieldExclusionList);
            } else if (entity instanceof LivingEntity) {
                Vec3 entityPosition = entity.position().add(0,0.5F,0);
                Vec3 radiationSourcePosition = Vec3.atCenterOf(radiationSource);

                BlockHitResult hitResult = world.clip(
                        new ClipContext(entityPosition, radiationSourcePosition, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)
                );

                return isRayBlocked(hitResult, world, shieldExclusionList);
            }
            return false;
        }

        private static boolean isRayBlocked(BlockHitResult blockHitResult, Level world, List<Block> shieldExclusionList) {
            if (blockHitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos hitBlockPosition = blockHitResult.getBlockPos();
                BlockState hitBlockState = world.getBlockState(hitBlockPosition);

                if (hitBlockState.isSolidRender(world, hitBlockPosition))
                    return (!shieldExclusionList.contains(hitBlockState.getBlock()));
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

        private static boolean isItemRadioactive(ItemStack itemStack) {
            return (ModBlocks.getLevelOneRadiationBlockItems().contains(itemStack.getItem()));
        }
    }

    @Mod.EventBusSubscriber(modid = CreateNuclearAge.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventBusEvents {

    }

}
