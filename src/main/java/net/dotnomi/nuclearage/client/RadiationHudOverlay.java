package net.dotnomi.nuclearage.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.dotnomi.nuclearage.CreateNuclearAge;
import net.dotnomi.nuclearage.configuration.CommonConfig;
import net.dotnomi.nuclearage.item.ModItems;
import net.dotnomi.nuclearage.util.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class RadiationHudOverlay {

    private static final ResourceLocation RADIATION_ICON = new ResourceLocation(CreateNuclearAge.MOD_ID,
            "textures/radiation/radiation.png");

    private static final ResourceLocation RADIATION_BAR = new ResourceLocation(CreateNuclearAge.MOD_ID,
            "textures/radiation/radiation_bar.png");

    public static final IGuiOverlay HUD_RADIATION = ((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        Minecraft minecraft = Minecraft.getInstance();
        PoseStack poseStack = guiGraphics.pose();
        Player player = minecraft.player;
        if (player == null) return;

        if (player.getMainHandItem().getItem() == ModItems.GEIGER_COUNTER.get()
                || player.getOffhandItem().getItem() == ModItems.GEIGER_COUNTER.get()) {
            int x = 0;
            int y = screenHeight;

            int relativeX = 2; // -91
            int relativeY = -19 ; // -66

            RenderSystem.setShader(GameRenderer::getPositionShader);
            RenderSystem.setShaderColor(1.0F,1.0F,1.0F,1.0F);
            RenderSystem.setShaderTexture(0, RADIATION_ICON);

            int spaceBetweenBorderAndHotbar = (minecraft.getWindow().getGuiScaledWidth() - (20 * 9)) / 2;
            int freeSpace = spaceBetweenBorderAndHotbar - 32;
            if (!player.getOffhandItem().isEmpty()) freeSpace -= 32;

            guiGraphics.blit(RADIATION_ICON, x + relativeX, y + relativeY, 16, 16, 0, 0, 16, 16, 16,16);

            int barLength = (int) Math.floor((double) freeSpace / 16);
            if (barLength < 2) return;
            for (int i = 0; i < barLength; i++) {
                if (i == 0) {
                    guiGraphics.blit(RADIATION_BAR, x + relativeX + 22, y + relativeY, 16, 16,
                            16,16,16,16,32,32);
                }
                else if (i == barLength - 1) {
                    guiGraphics.blit(RADIATION_BAR, x + relativeX + 22 + (i * 16), y + relativeY, 16, 16,
                            16,16,16,16,32,32);
                }
                else {
                    guiGraphics.blit(RADIATION_BAR, x + relativeX + 22 + (i * 16), y + relativeY, 16, 16,
                            16,16,16,16,32,32);
                }
            }

            int maxEntityRadiation = CommonConfig.DEADLY_RADIATION_DOSE;
            int playerRadiation = ClientRadiationData.getPlayerRadiation();

            float radiationPercentage = (float) playerRadiation / maxEntityRadiation * 100;

            int startColor = 0xFFFFD83F;
            int endColor = 0xFFFF3F4F;
            RenderHelper.fillHorizontalGradient(guiGraphics,x + relativeX + 22, y + relativeY + 2, barLength * 16, 12, startColor, endColor, radiationPercentage);

            for (int i = 0; i < barLength; i++) {
                if (i == 0) {
                    guiGraphics.blit(RADIATION_BAR, x + relativeX + 22, y + relativeY, 16, 16,
                            0,0,16,16,32,32);
                }
                else if (i == barLength - 1) {
                    guiGraphics.blit(RADIATION_BAR, x + relativeX + 22 + (i * 16), y + relativeY, 16, 16,
                            16,0,16,16,32,32);
                }
                else {
                    guiGraphics.blit(RADIATION_BAR, x + relativeX + 22 + (i * 16), y + relativeY, 16, 16,
                            0,16,16,16,32,32);
                }
            }

            String radiationStrength = playerRadiation + " RU";

            RenderHelper.drawTextField(guiGraphics, poseStack, radiationStrength, x + relativeX + 22 + 4, y + relativeY + 3, barLength * 16 - 8, 16 - 4, 0xFFFFFF, true, 0xFF0000);
        }
    });
}
