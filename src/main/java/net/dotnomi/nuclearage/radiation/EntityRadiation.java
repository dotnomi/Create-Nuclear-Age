package net.dotnomi.nuclearage.radiation;

import net.minecraft.nbt.CompoundTag;

public class EntityRadiation {

    private int radiation;
    private final int MIN_RADIATION = 0;
    private final int MAX_RADIATION = 100000;

    public int getRadiation() {
        return radiation;
    }

    public void addRadiation(int add) {
        radiation = Math.min(radiation + add, MAX_RADIATION);
    }

    public void subtractRadiation(int subtract) {
        radiation = Math.max(radiation - subtract, MIN_RADIATION);
    }

    public void copyFrom(EntityRadiation source) {
        radiation = source.radiation;
    }

    public void saveNBTData(CompoundTag tag) {
        tag.putInt("radiation", radiation);
    }

    public void loadNBTData(CompoundTag tag) {
        radiation = tag.getInt("radiation");
    }
}
