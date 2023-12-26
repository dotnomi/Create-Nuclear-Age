package net.dotnomi.nuclearage;

import com.mojang.logging.LogUtils;
import net.dotnomi.nuclearage.block.ModBlocks;
import net.dotnomi.nuclearage.configuration.CommonConfig;
import net.dotnomi.nuclearage.effect.ModEffects;
import net.dotnomi.nuclearage.event.ModEvents;
import net.dotnomi.nuclearage.item.ModCreativeModeTabs;
import net.dotnomi.nuclearage.item.ModItems;
import net.dotnomi.nuclearage.networking.ModMessages;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file

// Textures: Farbton -47, Sättigung 100, Helligkeit 0 GOLD

@Mod(CreateNuclearAge.MOD_ID)
public class CreateNuclearAge
{
    public static final String MOD_ID = "nuclearage";
    private static final Logger LOGGER = LogUtils.getLogger();

    public CreateNuclearAge()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModCreativeModeTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEffects.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, net.dotnomi.nuclearage.configuration.ModConfig.COMMON_CONFIG);
        //ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, net.dotnomi.nuclearage.configuration.ModConfig.CLIENT_CONFIG);
        //ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, net.dotnomi.nuclearage.configuration.ModConfig.SERVER_CONFIG);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(ModMessages::register);

        CommonConfig.RADIOACTIVE_BLOCKS.forEach((block) -> LOGGER.info("ITEM >> {} / {}", block.getBlock().toString(), block.getRadiation()));


        CommonConfig.RADIOACTIVE_ITEMS.forEach((item, rad) -> LOGGER.info("ITEM >> {} / {}", item.toString(), rad));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        /*if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.GRAPHITE);
            event.accept(ModItems.COMPRESSED_COAL);
        }*/
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new ModEvents());
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
        }
    }
}
