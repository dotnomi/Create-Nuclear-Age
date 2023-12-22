package net.dotnomi.nuclearage.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class VectorHelper {


    public static BlockPos getBlockPosition(Vec3 position) {
        int x = (int) Math.floor(position.x);
        int y = (int) Math.floor(position.y);
        int z = (int) Math.floor(position.z);

        return new BlockPos(x, y, z);
    }
}
