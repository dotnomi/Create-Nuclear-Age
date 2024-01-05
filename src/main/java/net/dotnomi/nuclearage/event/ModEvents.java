package net.dotnomi.nuclearage.event;

import net.dotnomi.nuclearage.CreateNuclearAge;
import net.dotnomi.nuclearage.command.AddRadiationCommand;
import net.dotnomi.nuclearage.command.SetRadiationCommand;
import net.dotnomi.nuclearage.command.SubtractRadiationCommand;
import net.dotnomi.nuclearage.effect.ModEffects;
import net.dotnomi.nuclearage.effect.damagesource.RadiationDamageSource;
import net.dotnomi.nuclearage.networking.ModMessages;
import net.dotnomi.nuclearage.networking.packet.RadiationDataSyncS2CPacket;
import net.dotnomi.nuclearage.radiation.EntityRadiation;
import net.dotnomi.nuclearage.radiation.EntityRadiationProvider;
import net.dotnomi.nuclearage.util.RadiationHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraftforge.server.command.ConfigCommand;

import java.util.List;
import java.util.Objects;

public class ModEvents {

    @Mod.EventBusSubscriber(modid = CreateNuclearAge.MOD_ID)
    public static class ForgeEvents {
        private static MobEffectInstance radiationStage1;
        private static MobEffectInstance radiationStage2;
        private static MobEffectInstance radiationStage3;

        @SubscribeEvent
        public static void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.getServer().getTickCount() % 20 == 0) {
                radiationStage1 = new MobEffectInstance(ModEffects.RADIATION.get(), 20 * 30, 0);
                radiationStage2 = new MobEffectInstance(ModEffects.RADIATION.get(), 20 * 30, 1);
                radiationStage3 = new MobEffectInstance(ModEffects.RADIATION.get(), 20 * 30, 2);
                List<ServerPlayer> players = event.getServer().getPlayerList().getPlayers();

                for (ServerPlayer player : players) {
                    Level world = player.level();
                    BlockPos playerPosition = player.getOnPos();
                    int radiationEntityRadius = 2 * 16; // 4 chunks radius

                    if (RadiationHelper.hasRadioavtiveItemsInInventory(player)) {
                        player.getCapability(EntityRadiationProvider.ENTITY_RADIATION).ifPresent(radiation -> {
                            radiation.addRadiation(RadiationHelper.getInventoryRadiation(player));
                        });
                    }

                    List<Entity> entities = world.getEntitiesOfClass(Entity.class, new AABB(
                            playerPosition.offset(-radiationEntityRadius, -radiationEntityRadius, -radiationEntityRadius),
                            playerPosition.offset(radiationEntityRadius, radiationEntityRadius, radiationEntityRadius)
                    ));

                    for (Entity entity : entities) {
                        if ((entity instanceof LivingEntity livingEntity)) {
                            if (RadiationHelper.isNearRadiactiveItem(entity, world))
                                livingEntity.getCapability(EntityRadiationProvider.ENTITY_RADIATION).ifPresent(radiation -> {
                                    radiation.addRadiation(RadiationHelper.getNearbyItemRadiation(entity, world, 12));
                                });
                            if (RadiationHelper.isNearRadioactiveBlock(entity, world))
                                livingEntity.getCapability(EntityRadiationProvider.ENTITY_RADIATION).ifPresent(radiation -> {
                                    radiation.addRadiation(RadiationHelper.getNearbyBlockRadiation(entity, world, 12));
                                });
                            if (RadiationHelper.isNearRadioactiveContainer(entity, world))
                                livingEntity.getCapability(EntityRadiationProvider.ENTITY_RADIATION).ifPresent(radiation -> {
                                    radiation.addRadiation(RadiationHelper.getNearbyContainerRadiation(entity, world, 12));
                                });

                            livingEntity.getCapability(EntityRadiationProvider.ENTITY_RADIATION).ifPresent(entityRadiation -> {
                                int radiation = entityRadiation.getRadiation();

                                if (livingEntity instanceof Player)
                                    ModMessages.sendToPlayer(new RadiationDataSyncS2CPacket(radiation), player);

                                //player.sendSystemMessage(Component.literal(livingEntity.getType().toShortString() + " | RU: " + radiation + " | H: " + livingEntity.getHealth()));


                                //int amplifier = Objects.requireNonNull(livingEntity.getEffect(ModEffects.RADIATION.get())).getAmplifier();

                                if (radiation >= 350000 && radiation < 750000) {
                                    if (!livingEntity.hasEffect(ModEffects.RADIATION.get()) || livingEntity.getEffect(ModEffects.RADIATION.get()).getAmplifier() != 0) {
                                        livingEntity.removeEffect(ModEffects.RADIATION.get());
                                        livingEntity.addEffect(radiationStage1);
                                        player.sendSystemMessage(Component.literal(event.getServer().getTickCount() + "T | " + radiation + " | Stage 1"));
                                    }
                                } else if (radiation >= 750000 && radiation < 1000000) {
                                    if (!livingEntity.hasEffect(ModEffects.RADIATION.get()) || livingEntity.getEffect(ModEffects.RADIATION.get()).getAmplifier() != 1) {
                                        livingEntity.removeEffect(ModEffects.RADIATION.get());
                                        livingEntity.addEffect(radiationStage2);
                                        player.sendSystemMessage(Component.literal(event.getServer().getTickCount() + "T | " + radiation + " | Stage 2"));
                                    }
                                } else if (radiation >= 1000000) {
                                    if (!livingEntity.hasEffect(ModEffects.RADIATION.get()) || livingEntity.getEffect(ModEffects.RADIATION.get()).getAmplifier() != 2) {
                                        livingEntity.removeEffect(ModEffects.RADIATION.get());
                                        livingEntity.addEffect(radiationStage3);
                                        player.sendSystemMessage(Component.literal(event.getServer().getTickCount() + "T | " + radiation + " | Stage 3"));
                                    }
                                } else {
                                    livingEntity.removeEffect(ModEffects.RADIATION.get());
                                }
                            });

                            //player.sendSystemMessage(Component.literal("Config Value: " + ServerConfig.testValue));

                        }
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

        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {
            new SetRadiationCommand(event.getDispatcher());
            new AddRadiationCommand(event.getDispatcher());
            new SubtractRadiationCommand(event.getDispatcher());

            ConfigCommand.register(event.getDispatcher());
        }
    }

    @Mod.EventBusSubscriber(modid = CreateNuclearAge.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventBusEvents {

    }

}
