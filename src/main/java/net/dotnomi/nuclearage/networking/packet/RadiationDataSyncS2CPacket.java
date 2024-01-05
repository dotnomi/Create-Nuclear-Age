package net.dotnomi.nuclearage.networking.packet;

import net.dotnomi.nuclearage.client.ClientRadiationData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RadiationDataSyncS2CPacket {
    private final int current_radiation;
    private final int radiation_per_second;

    public  RadiationDataSyncS2CPacket(int current_radiation, int radiation_per_second) {
        this.current_radiation = current_radiation;
        this.radiation_per_second = radiation_per_second;
    }

    public RadiationDataSyncS2CPacket(FriendlyByteBuf buffer) {
        this.current_radiation = buffer.readInt();
        this.radiation_per_second = buffer.readInt();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeInt(current_radiation);
        buffer.writeInt(radiation_per_second);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ClientRadiationData.setPlayerRadiation(current_radiation);
            ClientRadiationData.setPlayerRadiationPerSecond(radiation_per_second);
        });
        return true;
    }
}
