package net.dotnomi.nuclearage.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.dotnomi.nuclearage.configuration.CommonConfig;
import net.dotnomi.nuclearage.networking.ModMessages;
import net.dotnomi.nuclearage.networking.packet.RadiationDataSyncS2CPacket;
import net.dotnomi.nuclearage.radiation.EntityRadiationProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collection;

public class SubtractRadiationCommand {
    public SubtractRadiationCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("radiation")
            .then(Commands.literal("subtract")
            .then(Commands.argument("target", EntityArgument.entities())
            .then(Commands.argument("radiationUnits", IntegerArgumentType.integer(0, CommonConfig.DEADLY_RADIATION_DOSE))
            .executes(context -> subtractRadiation(
                EntityArgument.getEntities(context, "target"),
                IntegerArgumentType.getInteger(context, "radiationUnits")
            ))))));
    }

    private int subtractRadiation(Collection<? extends Entity> targets, int radiation) {

        for (Entity entity : targets) {
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.getCapability(EntityRadiationProvider.ENTITY_RADIATION).ifPresent(entityRadiation -> {
                    entityRadiation.subtractRadiation(radiation);

                    if (entity instanceof ServerPlayer serverPlayer) {
                        ModMessages.sendToPlayer(new RadiationDataSyncS2CPacket(entityRadiation.getRadiation(), -radiation), serverPlayer);
                    }
                });
            }
        }

        return Command.SINGLE_SUCCESS;
    }
}
