package net.dotnomi.nuclearage.item;

import net.dotnomi.nuclearage.CreateNuclearAge;
import net.dotnomi.nuclearage.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateNuclearAge.MOD_ID);

    public static final RegistryObject<CreativeModeTab> NUCLEAR_AGE_TAB =
            CREATIVE_MODE_TABS.register("nuclear_age_tab", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.GRAPHITE.get()))
                    .title(Component.translatable("creativetab.nuclear_age_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.GRAPHITE.get());
                        pOutput.accept(ModItems.COMPRESSED_COAL.get());

                        pOutput.accept(ModBlocks.RADIOCALCITE_BLOCK.get());
                        pOutput.accept(ModBlocks.RADIONITE_BLOCK.get());
                        pOutput.accept(ModBlocks.RADIORITE_BLOCK.get());
                        pOutput.accept(ModBlocks.RADIOSCHIST_BLOCK.get());
                        pOutput.accept(ModBlocks.RADIOSITE_BLOCK.get());
                        pOutput.accept(ModBlocks.RADIOTUFFITE_BLOCK.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
