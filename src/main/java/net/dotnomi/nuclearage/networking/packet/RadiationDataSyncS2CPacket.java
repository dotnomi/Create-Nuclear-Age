package net.dotnomi.nuclearage.networking.packet;

import net.dotnomi.nuclearage.client.ClientRadiationData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RadiationDataSyncS2CPacket {
    private final int radiation;

    public  RadiationDataSyncS2CPacket(int radiation) {
        this.radiation = radiation;
    }

    public RadiationDataSyncS2CPacket(FriendlyByteBuf buffer) {
        this.radiation = buffer.readInt();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeInt(radiation);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ClientRadiationData.set(radiation);
        });
        return true;
    }
}
