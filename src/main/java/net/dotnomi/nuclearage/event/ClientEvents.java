package net.dotnomi.nuclearage.event;

import net.dotnomi.nuclearage.CreateNuclearAge;
import net.dotnomi.nuclearage.client.RadiationHudOverlay;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ClientEvents {

    @Mod.EventBusSubscriber(modid = CreateNuclearAge.MOD_ID, value = Dist.CLIENT)
    public static class ClientForgeEvents {

    }

    @Mod.EventBusSubscriber(modid = CreateNuclearAge.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {

        @SubscribeEvent
        public static void test(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("radiation", RadiationHudOverlay.HUD_RADIATION);
        }
    }
}
