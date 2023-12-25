package net.dotnomi.nuclearage.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class VectorHelper {

    public static Vec3 getArmPosition(Player player, boolean isLeftArm) {
        float yaw = player.getYRot();
        float armOffset = isLeftArm ? 0.35F : -0.35F;
        float radianYaw = (float) Math.toRadians(yaw);

        double offsetX = Math.sin(radianYaw) * armOffset;
        double offsetZ = Math.cos(radianYaw) * armOffset;

        return player.position().add(offsetX, 1.5, offsetZ);
    }

    public static BlockPos getBlockPosition(Vec3 position) {
        int x = (int) Math.floor(position.x);
        int y = (int) Math.floor(position.y);
        int z = (int) Math.floor(position.z);

        return new BlockPos(x, y, z);
    }
}
