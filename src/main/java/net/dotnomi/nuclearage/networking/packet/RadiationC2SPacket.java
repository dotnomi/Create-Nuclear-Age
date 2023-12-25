package net.dotnomi.nuclearage.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RadiationC2SPacket {
    public  RadiationC2SPacket() {

    }

    public RadiationC2SPacket(FriendlyByteBuf buffer) {

    }

    public void toBytes(FriendlyByteBuf buffer) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            assert player != null;
            ServerLevel world = player.server.getLevel(player.level().dimension());

            player.sendSystemMessage(Component.literal("Send by Server"));

        });
        return true;
    }
}
