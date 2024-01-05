package net.dotnomi.nuclearage.item;

import net.dotnomi.nuclearage.CreateNuclearAge;
import net.dotnomi.nuclearage.client.ClientRadiationData;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class ModItemProperties {
    public static void addCustomItemProperties() {
        ItemProperties.register(ModItems.GEIGER_COUNTER.get(), new ResourceLocation(CreateNuclearAge.MOD_ID, "on"),
                (pStack, pLevel, pEntity, pSeed) -> {
                    if (pEntity != null) {
                        if (pEntity instanceof Player player) {
                            assert pLevel != null;
                            if (player.level().isClientSide()) {
                                int playerRadiationPerSecond = ClientRadiationData.getPlayerRadiationPerSecond();
                                
                                if (playerRadiationPerSecond < 100) {
                                    return 0;
                                } else if (playerRadiationPerSecond < 1000) {
                                    return 1;
                                } else {
                                    return 2;
                                }
                            }
                        };
                    }

                    return -1;
                });
    }
}
