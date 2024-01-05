package net.dotnomi.nuclearage.util;

import net.dotnomi.nuclearage.block.ModBlocks;
import net.dotnomi.nuclearage.configuration.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RadiationHelper {

    public static final List<Block> defaultExclusionList = List.of(
            Blocks.AIR,
            Blocks.VOID_AIR,
            Blocks.STRUCTURE_VOID
    );

    public static int getInventoryRadiation(ServerPlayer player) {
        int radiation = 0;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack itemStack = player.getInventory().getItem(i);

            radiation += CommonConfig.RADIOACTIVE_ITEMS.getOrDefault(itemStack.getItem(),0) * itemStack.getCount();
            radiation += getItemContainerRadiation(itemStack);
        }

        return radiation;
    }

    public static int getItemContainerRadiation(ItemStack itemStack) {
        int radiation = 0;
        LazyOptional<IItemHandler> itemHandlerOptional = itemStack.getCapability(CapabilityManager.get(new CapabilityToken<>() {}));

        if (itemHandlerOptional.isPresent()) {
            IItemHandler itemHandler = itemHandlerOptional.orElse(null);
            if (itemHandler != null) {
                for (int i = 0; i < itemHandler.getSlots(); i++) {
                    radiation += CommonConfig.RADIOACTIVE_ITEMS.getOrDefault(itemHandler.getStackInSlot(i).getItem(),0) * itemHandler.getStackInSlot(i).getCount();
                }
            }
        }

        return radiation;
    }

    public static int getNearbyItemRadiation(Entity entity, Level world, int radius) {
        BlockPos entityPosition = new BlockPos(entity.getOnPos());
        int radiation = 0;

        List<ItemEntity> itemEntities = world.getEntitiesOfClass(ItemEntity.class, new AABB(
                entityPosition.offset(-radius, -radius, -radius),
                entityPosition.offset(radius, radius, radius)
        ));

        for (ItemEntity itemEntity : itemEntities) {
            ItemStack itemStack = itemEntity.getItem();

            if (!isShieldedFromRadiation(entity, world, new BlockPos(VectorHelper.getBlockPosition(itemEntity.getEyePosition())), defaultExclusionList)) {
                radiation += CommonConfig.RADIOACTIVE_ITEMS.getOrDefault(itemStack.getItem(), 0) * itemStack.getCount();
                radiation += RadiationHelper.getItemContainerRadiation(itemStack);
            }
        }
        return radiation;
    }

    public static int getNearbyBlockRadiation(Entity entity, Level world, int radius) {
        BlockPos entityPosition = new BlockPos(entity.getOnPos());
        int radiation = 0;

        for (BlockPos blockPosition : BlockPos.betweenClosed(
                entityPosition.offset(-radius, -radius, -radius),
                entityPosition.offset(radius, radius, radius))) {

            List<Block> exclusionList = new ArrayList<>(defaultExclusionList);
            exclusionList.addAll(ModBlocks.getLevelOneRadiationBlocks());

            if (!isShieldedFromRadiation(entity, world, blockPosition, exclusionList)) {
                radiation += CommonConfig.RADIOACTIVE_BLOCKS.getOrDefault(world.getBlockState(blockPosition).getBlock(), 0);
            }
        }
        return radiation;
    }

    public static int getNearbyContainerRadiation(Entity entity, Level world, int radius) {
        BlockPos entityPosition = new BlockPos(entity.getOnPos());
        int radiation = 0;

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
                        List<Block> exclusionList = defaultExclusionList;
                        exclusionList.add(blockState.getBlock());

                        if (!isShieldedFromRadiation(entity, world, blockPosition, exclusionList)) {
                            radiation += CommonConfig.RADIOACTIVE_ITEMS.getOrDefault(itemStack.getItem(),0) * itemStack.getCount();
                            radiation += RadiationHelper.getItemContainerRadiation(itemStack);
                        }
                    }
                }
            }
        }
        return radiation;
    }

    public static boolean hasRadioavtiveItemsInInventory(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack itemStack = player.getInventory().getItem(i);

            if (isItemContainerRadioavtive(itemStack) || isItemRadioactive(itemStack)) {
                if (itemStack.getItem() != Items.AIR) return true;
            }
        }

        return false;
    }

    public static boolean isItemContainerRadioavtive(ItemStack itemStack) {
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

    public static boolean isNearRadiactiveItem(Entity entity, Level world) {
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

    public static boolean isNearRadioactiveBlock(Entity entity, Level world) {
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

    public static boolean isNearRadioactiveContainer(Entity entity, Level world) {
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

    public static boolean isItemRadioactive(ItemStack itemStack) {
        return ( ModBlocks.getLevelOneRadiationBlockItems().contains(itemStack.getItem()));
    }

    public static boolean isShieldedFromRadiation(Entity entity, Level world, BlockPos radiationSource, List<Block> shieldExclusionList) {
        if (entity instanceof Player player) {
            Vec3 entityEyePosition = entity.getEyePosition();
            Vec3 entityLeftArmPosition = VectorHelper.getArmPosition(player, true);
            Vec3 entityRightArmPosition = VectorHelper.getArmPosition(player, false);
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

    public static boolean isRayBlocked(BlockHitResult blockHitResult, Level world, List<Block> shieldExclusionList) {
        if (blockHitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos hitBlockPosition = blockHitResult.getBlockPos();
            BlockState hitBlockState = world.getBlockState(hitBlockPosition);

            if (hitBlockState.isSolidRender(world, hitBlockPosition))
                return (!shieldExclusionList.contains(hitBlockState.getBlock()));
        }
        return false;
    }

    private static boolean contains(Set<Block> blockSet, ItemStack itemStack) {
        return false;

    }

}
