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

    private static final ResourceLocation RADIATION_INDICATOR_0 = new ResourceLocation(CreateNuclearAge.MOD_ID,
            "textures/radiation/radiation_indicator_0.png");

    private static final ResourceLocation RADIATION_INDICATOR_1 = new ResourceLocation(CreateNuclearAge.MOD_ID,
            "textures/radiation/radiation_indicator_1.png");

    private static final ResourceLocation RADIATION_INDICATOR_2 = new ResourceLocation(CreateNuclearAge.MOD_ID,
            "textures/radiation/radiation_indicator_2.png");

    private static final ResourceLocation RADIATION_INDICATOR_3 = new ResourceLocation(CreateNuclearAge.MOD_ID,
            "textures/radiation/radiation_indicator_3.png");

    private static final ResourceLocation RADIATION_INDICATOR_4 = new ResourceLocation(CreateNuclearAge.MOD_ID,
            "textures/radiation/radiation_indicator_4.png");

    private static final ResourceLocation RADIATION_INDICATOR_5 = new ResourceLocation(CreateNuclearAge.MOD_ID,
            "textures/radiation/radiation_indicator_5.png");

    private static final ResourceLocation RADIATION_INDICATOR_6 = new ResourceLocation(CreateNuclearAge.MOD_ID,
            "textures/radiation/radiation_indicator_6.png");

    private static final ResourceLocation RADIATION_INDICATOR_7 = new ResourceLocation(CreateNuclearAge.MOD_ID,
            "textures/radiation/radiation_indicator_7.png");

    private static final ResourceLocation RADIATION_INDICATOR_8 = new ResourceLocation(CreateNuclearAge.MOD_ID,
            "textures/radiation/radiation_indicator_8.png");

    private static final ResourceLocation RADIATION_INDICATOR_9 = new ResourceLocation(CreateNuclearAge.MOD_ID,
            "textures/radiation/radiation_indicator_9.png");

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

            int playerRadiationPerSecond = ClientRadiationData.getPlayerRadiationPerSecond();
            String radiation_per_second = playerRadiationPerSecond + " RU/s";

            if (playerRadiationPerSecond < 10) {
                renderRadiationIndicator(guiGraphics, RADIATION_INDICATOR_0, x + relativeX, y + relativeY - 20);
            } else if (playerRadiationPerSecond < 25) {
                renderRadiationIndicator(guiGraphics, RADIATION_INDICATOR_1, x + relativeX, y + relativeY - 20);
            } else if (playerRadiationPerSecond < 50) {
                renderRadiationIndicator(guiGraphics, RADIATION_INDICATOR_2, x + relativeX, y + relativeY - 20);
            } else if (playerRadiationPerSecond < 100) {
                renderRadiationIndicator(guiGraphics, RADIATION_INDICATOR_3, x + relativeX, y + relativeY - 20);
            } else if (playerRadiationPerSecond < 250) {
                renderRadiationIndicator(guiGraphics, RADIATION_INDICATOR_4, x + relativeX, y + relativeY - 20);
            } else if (playerRadiationPerSecond < 500) {
                renderRadiationIndicator(guiGraphics, RADIATION_INDICATOR_5, x + relativeX, y + relativeY - 20);
            } else if (playerRadiationPerSecond < 1000) {
                renderRadiationIndicator(guiGraphics, RADIATION_INDICATOR_6, x + relativeX, y + relativeY - 20);
            } else if (playerRadiationPerSecond < 2500) {
                renderRadiationIndicator(guiGraphics, RADIATION_INDICATOR_7, x + relativeX, y + relativeY - 20);
            } else if (playerRadiationPerSecond < 5000) {
                renderRadiationIndicator(guiGraphics, RADIATION_INDICATOR_8, x + relativeX, y + relativeY - 20);
            } else {
                renderRadiationIndicator(guiGraphics, RADIATION_INDICATOR_9, x + relativeX, y + relativeY - 20);
            }

            guiGraphics.drawString(minecraft.font, radiation_per_second, x + relativeX + 22, y + relativeY + 3 - 18, 0xFFFFFF);

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


            String current_radiation = playerRadiation + " RU";
            RenderHelper.drawTextField(guiGraphics, poseStack, current_radiation, x + relativeX + 22 + 4, y + relativeY + 3, barLength * 16 - 8, 16 - 4, 0xFFFFFF);
        }
    });

    private static void renderRadiationIndicator(GuiGraphics guiGraphics, ResourceLocation resourceLocation, int x, int y) {
        guiGraphics.blit(resourceLocation, x, y, 16, 16, 0, 0, 16, 16, 16,16);
    }
}
