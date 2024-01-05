package net.dotnomi.nuclearage.configuration;

import net.dotnomi.nuclearage.block.RadiatedBlock;
import net.dotnomi.nuclearage.item.RadiatedItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Set;

public class CommonConfig {
    public static HashMap<Block, Integer> RADIOACTIVE_BLOCKS;
    public static HashMap<Item, Integer> RADIOACTIVE_ITEMS;

    public static int DEADLY_RADIATION_DOSE;
}
