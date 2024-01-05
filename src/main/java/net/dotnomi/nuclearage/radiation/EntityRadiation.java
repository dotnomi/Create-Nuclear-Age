package net.dotnomi.nuclearage.radiation;

import net.dotnomi.nuclearage.configuration.CommonConfig;
import net.minecraft.nbt.CompoundTag;

public class EntityRadiation {

    private int current_radiation;

    private int radiation_per_second;
    private final int MIN_RADIATION = 0;
    private final int MAX_RADIATION = CommonConfig.DEADLY_RADIATION_DOSE;

    public int getRadiation() {
        return current_radiation;
    }

    public void addRadiation(int add) {
        current_radiation = Math.min(current_radiation + add, MAX_RADIATION);
    }

    public void setRadiation(int value) {
        current_radiation = Math.min(value, MAX_RADIATION);
    }

    public void subtractRadiation(int subtract) { current_radiation = Math.max(current_radiation - subtract, MIN_RADIATION); }

    public int getRadiationPerSec() {
        return radiation_per_second;
    }

    public void setRadiationPerSec(int value) { radiation_per_second = value; }

    public void copyFrom(EntityRadiation source) {
        current_radiation = source.current_radiation;
    }

    public void saveNBTData(CompoundTag tag) {
        tag.putInt("radiation", current_radiation);
    }

    public void loadNBTData(CompoundTag tag) {
        current_radiation = tag.getInt("radiation");
    }
}
