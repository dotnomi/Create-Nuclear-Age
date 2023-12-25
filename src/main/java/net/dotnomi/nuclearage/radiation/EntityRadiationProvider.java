package net.dotnomi.nuclearage.radiation;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityRadiationProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<EntityRadiation> ENTITY_RADIATION = CapabilityManager.get(new CapabilityToken<>() {});

    private EntityRadiation radiation = null;

    private final LazyOptional<EntityRadiation> optional = LazyOptional.of(this::createEntityRadiation);

    private EntityRadiation createEntityRadiation() {
        if (radiation == null) {
            radiation = new EntityRadiation();
        }

        return radiation;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        if (capability == ENTITY_RADIATION) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        createEntityRadiation().saveNBTData(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        createEntityRadiation().loadNBTData(tag);
    }
}
