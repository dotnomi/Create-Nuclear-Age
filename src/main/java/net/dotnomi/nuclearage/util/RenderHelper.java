package net.dotnomi.nuclearage.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

public class RenderHelper {

    public static void fillHorizontalGradient(GuiGraphics guiGraphics, int x, int y, int width, int height, int startColor, int endColor, float percentage) {
        int r1 = (startColor >> 16) & 255;
        int g1 = (startColor >> 8) & 255;
        int b1 = startColor & 255;
        int a1 = (startColor >> 24) & 255;

        int r2 = (endColor >> 16) & 255;
        int g2 = (endColor >> 8) & 255;
        int b2 = endColor & 255;
        int a2 = (endColor >> 24) & 255;

        int renderWidth = (int) (width * (percentage / 100.0f));

        for (int i = 0; i < renderWidth; i++) {
            float ratio = (float) i / (float) (width - 1);
            int r = (int) Mth.lerp(ratio, r1, r2);
            int g = (int) Mth.lerp(ratio, g1, g2);
            int b = (int) Mth.lerp(ratio, b1, b2);
            int a = (int) Mth.lerp(ratio, a1, a2);
            int color = (a << 24) | (r << 16) | (g << 8) | b;

            guiGraphics.fill(x + i, y, x + i + 1, y + height, color);
        }
    }

    public static void drawTextField(GuiGraphics guiGraphics, PoseStack poseStack, String text, int x, int y, int width, int height, int color, boolean outline, int outlineColor) {

        if (outline) {
            guiGraphics.fill(x, y, x + width, y, outlineColor);
            guiGraphics.fill(x, y, x, y + height, outlineColor);
            guiGraphics.fill(x, y + height, x + width, y + height, outlineColor);
            guiGraphics.fill(x + width, y, x + width, y + height, outlineColor);
        }

        drawTextField(guiGraphics, poseStack, text, x, y, width, height, color);
    }

    public static void drawTextField(GuiGraphics guiGraphics, PoseStack poseStack, String text, int x, int y, int width, int height, int color) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        poseStack.pushPose();

        int textWidth = font.width(text);
        int textHeight = font.lineHeight;

        float scaleX = textWidth <= width ? 1.0f : (float) width / textWidth;
        float scaleY = textHeight <= height ? 1.0f : (float) height / textHeight;
        float scale = Math.min(scaleX, scaleY);

        poseStack.scale(scale, scale, scale);

        int scaledX = (int) (x / scale + (width / scale - textWidth) / 2);
        int scaledY = (int) (y / scale + (height / scale - textHeight) / 2);

        guiGraphics.drawString(font, text, scaledX, scaledY, color);

        poseStack.popPose();
    }
}
