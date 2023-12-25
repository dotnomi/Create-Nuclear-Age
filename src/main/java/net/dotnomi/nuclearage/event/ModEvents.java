package net.dotnomi.nuclearage.event;

import net.dotnomi.nuclearage.CreateNuclearAge;
import net.dotnomi.nuclearage.block.ModBlocks;
import net.dotnomi.nuclearage.client.ClientRadiationData;
import net.dotnomi.nuclearage.configuration.ModConfig;
import net.dotnomi.nuclearage.configuration.ServerConfig;
import net.dotnomi.nuclearage.effect.ModEffects;
import net.dotnomi.nuclearage.networking.ModMessages;
import net.dotnomi.nuclearage.networking.packet.RadiationDataSyncS2CPacket;
import net.dotnomi.nuclearage.radiation.EntityRadiation;
import net.dotnomi.nuclearage.radiation.EntityRadiationProvider;
import net.dotnomi.nuclearage.util.RadiationChecks;
import net.dotnomi.nuclearage.util.VectorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * TODO: Item Container on Ground Bug - forever radiation
 */

public class ModEvents {

    @Mod.EventBusSubscriber(modid = CreateNuclearAge.MOD_ID)
    public static class ForgeEvents {

        @SubscribeEvent
        public static void onServerTick(TickEvent.ServerTickEvent event) {
            List<ServerPlayer> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
            MobEffectInstance radiationStage1 = new MobEffectInstance(ModEffects.RADIATION.get(), 20 * 30, 0);
            MobEffectInstance radiationStage2 = new MobEffectInstance(ModEffects.RADIATION.get(), 20 * 30, 1);
            MobEffectInstance radiationStage3 = new MobEffectInstance(ModEffects.RADIATION.get(), 20 * 30, 2);

            for (ServerPlayer player : players) {
                Level world = player.level();
                BlockPos playerPosition = player.getOnPos();
                int radiationEntityRadius = 2 * 16; // 4 chunks radius

                if (RadiationChecks.hasRadioavtiveItemsInInventory(player)) {
                    player.getCapability(EntityRadiationProvider.ENTITY_RADIATION).ifPresent(radiation -> {
                        radiation.addRadiation(100);
                        ModMessages.sendToPlayer(new RadiationDataSyncS2CPacket(radiation.getRadiation()), player);
                    });
                }

                List<Entity> entities = world.getEntitiesOfClass(Entity.class, new AABB(
                        playerPosition.offset(-radiationEntityRadius, -radiationEntityRadius, -radiationEntityRadius),
                        playerPosition.offset(radiationEntityRadius, radiationEntityRadius, radiationEntityRadius)
                ));

                for (Entity entity : entities) {
                    if ((entity instanceof LivingEntity livingEntity)) {
                        if (RadiationChecks.isNearRadiactiveItem(entity, world))
                            livingEntity.getCapability(EntityRadiationProvider.ENTITY_RADIATION).ifPresent(radiation -> {
                                radiation.addRadiation(100);
                                if (livingEntity instanceof Player)
                                    ModMessages.sendToPlayer(new RadiationDataSyncS2CPacket(radiation.getRadiation()), player);
                            });
                        if (RadiationChecks.isNearRadioactiveBlock(entity, world))
                            livingEntity.getCapability(EntityRadiationProvider.ENTITY_RADIATION).ifPresent(radiation -> {
                                radiation.addRadiation(100);
                                if (livingEntity instanceof Player)
                                    ModMessages.sendToPlayer(new RadiationDataSyncS2CPacket(radiation.getRadiation()), player);
                            });
                        if (RadiationChecks.isNearRadioactiveContainer(entity, world))
                            livingEntity.getCapability(EntityRadiationProvider.ENTITY_RADIATION).ifPresent(radiation -> {
                                radiation.addRadiation(100);
                                if (livingEntity instanceof Player)
                                    ModMessages.sendToPlayer(new RadiationDataSyncS2CPacket(radiation.getRadiation()), player);
                            });

                        livingEntity.getCapability(EntityRadiationProvider.ENTITY_RADIATION).ifPresent(entityRadiation -> {
                            int radiation = entityRadiation.getRadiation();

                            //player.sendSystemMessage(Component.literal(livingEntity.getType().toShortString() + " | RU: " + radiation + " | H: " + livingEntity.getHealth()));

                            if (radiation >= 35000 && radiation < 75000) {
                                livingEntity.removeEffect(ModEffects.RADIATION.get());
                                livingEntity.addEffect(radiationStage1);
                            } else if (radiation >= 75000 && radiation < 100000) {
                                livingEntity.removeEffect(ModEffects.RADIATION.get());
                                livingEntity.addEffect(radiationStage2);
                            } else if (radiation >= 100000) {
                                livingEntity.removeEffect(ModEffects.RADIATION.get());
                                livingEntity.addEffect(radiationStage3);
                            } else {
                                livingEntity.removeEffect(ModEffects.RADIATION.get());
                            }
                        });

                        //player.sendSystemMessage(Component.literal("Config Value: " + ServerConfig.testValue));

                    }
                }
            }
        }

        @SubscribeEvent
        public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof LivingEntity) {
                if (!event.getObject().getCapability(EntityRadiationProvider.ENTITY_RADIATION).isPresent()) {
                    event.addCapability(new ResourceLocation(CreateNuclearAge.MOD_ID, "properties"), new EntityRadiationProvider());
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerCloned(PlayerEvent.Clone event) {
            if (event.isWasDeath()) {
                event.getOriginal().getCapability(EntityRadiationProvider.ENTITY_RADIATION).ifPresent(oldStore -> {
                    event.getEntity().getCapability(EntityRadiationProvider.ENTITY_RADIATION).ifPresent(newStore -> {
                        newStore.copyFrom(oldStore);
                    });
                });
            }
        }

        @SubscribeEvent
        public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
            event.register(EntityRadiation.class);
        }

        @SubscribeEvent
        public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
            if (!event.getLevel().isClientSide) {
                if (event.getEntity() instanceof ServerPlayer player) {
                    player.getCapability(EntityRadiationProvider.ENTITY_RADIATION).ifPresent(radiation -> {
                        ModMessages.sendToPlayer(new RadiationDataSyncS2CPacket(radiation.getRadiation()), player);
                    });
                }
            }
        }
    }

    @Mod.EventBusSubscriber(modid = CreateNuclearAge.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventBusEvents {

    }

}
