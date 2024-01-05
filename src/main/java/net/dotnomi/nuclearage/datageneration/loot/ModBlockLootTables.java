package net.dotnomi.nuclearage.datageneration.loot;

import net.dotnomi.nuclearage.block.ModBlocks;
import net.dotnomi.nuclearage.item.ModItems;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.REACTOR_CHAMBER.get());

        this.add(ModBlocks.RADIOCALCITE_BLOCK.get(),
                block -> createOreDrop(ModBlocks.RADIOCALCITE_BLOCK.get(), ModItems.RADIOCALCITE.get()));
        this.add(ModBlocks.RADIONITE_BLOCK.get(),
                block -> createOreDrop(ModBlocks.RADIONITE_BLOCK.get(), ModItems.RADIONITE.get()));
        this.add(ModBlocks.RADIORITE_BLOCK.get(),
                block -> createOreDrop(ModBlocks.RADIORITE_BLOCK.get(), ModItems.RADIORITE.get()));
        this.add(ModBlocks.RADIOSCHIST_BLOCK.get(),
                block -> createOreDrop(ModBlocks.RADIOSCHIST_BLOCK.get(), ModItems.RADIOSCHIST.get()));
        this.add(ModBlocks.RADIOSITE_BLOCK.get(),
                block -> createOreDrop(ModBlocks.RADIOSITE_BLOCK.get(), ModItems.RADIOSITE.get()));
        this.add(ModBlocks.RADIOTUFFITE_BLOCK.get(),
                block -> createOreDrop(ModBlocks.RADIOTUFFITE_BLOCK.get(), ModItems.RADIOTUFFITE.get()));

    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
