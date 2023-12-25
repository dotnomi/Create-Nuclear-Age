package net.dotnomi.nuclearage.block;

import net.minecraft.world.level.block.Block;

public class RadiatedBlock{
    private final Block block;
    private final int radiationUnitsPerTick;


    public RadiatedBlock(Block block, int radiationUnitsPerTick) {
        this.block = block;
        this.radiationUnitsPerTick = radiationUnitsPerTick;
    }

    public Block getBlock() {
        return block;
    }

    public int getRadiation() {
        return radiationUnitsPerTick;
    }
}
