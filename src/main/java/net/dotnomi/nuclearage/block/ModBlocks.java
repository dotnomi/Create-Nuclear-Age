package net.dotnomi.nuclearage.block;

import net.dotnomi.nuclearage.CreateNuclearAge;
import net.dotnomi.nuclearage.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CreateNuclearAge.MOD_ID);

    public static final RegistryObject<Block> RADIOCALCITE_BLOCK = registerBlock("radiocalcite_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.CALCITE)));

    public static final RegistryObject<Block> RADIONITE_BLOCK = registerBlock("radionite_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.GRANITE)));

    public static final RegistryObject<Block> RADIORITE_BLOCK = registerBlock("radiorite_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIORITE)));

    public static final RegistryObject<Block> RADIOSCHIST_BLOCK = registerBlock("radioschist_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));

    public static final RegistryObject<Block> RADIOSITE_BLOCK = registerBlock("radiosite_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.ANDESITE)));

    public static final RegistryObject<Block> RADIOTUFFITE_BLOCK = registerBlock("radiotuffite_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.TUFF)));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    public static List<Block> getLevelOneRadiationBlocks() {
        return List.of(
                RADIOCALCITE_BLOCK.get(),
                RADIONITE_BLOCK.get(),
                RADIORITE_BLOCK.get(),
                RADIOSCHIST_BLOCK.get(),
                RADIOSITE_BLOCK.get(),
                RADIOTUFFITE_BLOCK.get()
        );
    }

    public static List<Item> getLevelOneRadiationBlockItems() {
        return List.of(
                RADIOCALCITE_BLOCK.get().asItem(),
                RADIONITE_BLOCK.get().asItem(),
                RADIORITE_BLOCK.get().asItem(),
                RADIOSCHIST_BLOCK.get().asItem(),
                RADIOSITE_BLOCK.get().asItem(),
                RADIOTUFFITE_BLOCK.get().asItem()
        );
    }
}
