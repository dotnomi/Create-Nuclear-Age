package net.dotnomi.nuclearage.item;

import net.minecraft.world.item.Item;

public class RadiatedItem {
    private final Item item;
    private final int radiationUnitsPerTick;


    public RadiatedItem(Item item, int radiationUnitsPerTick) {
        this.item = item;
        this.radiationUnitsPerTick = radiationUnitsPerTick;
    }

    public Item getItem() {
        return item;
    }

    public int getRadiation() {
        return radiationUnitsPerTick;
    }
}
