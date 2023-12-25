package net.dotnomi.nuclearage.configuration;

import net.dotnomi.nuclearage.CreateNuclearAge;
import net.dotnomi.nuclearage.block.RadiatedBlock;
import net.dotnomi.nuclearage.item.RadiatedItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = CreateNuclearAge.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConfig {

    /*
     * SERVER CONFIGURATION
     */

    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.IntValue TEST_VALUE =
            SERVER_BUILDER.comment("A random test integer").defineInRange("test-value", 0, 0, 100);
    public static final ForgeConfigSpec SERVER_CONFIG = SERVER_BUILDER.build();


    /*
     * CLIENT CONFIGURATION
     */

    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec CLIENT_CONFIG = CLIENT_BUILDER.build();


    /*
     * COMMON CONFIGURATION
     */

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> RADIOACTIVE_BLOCK_STRINGS = COMMON_BUILDER
            .comment("List of all radioactive blocks.")
            .defineListAllowEmpty("radioactivity.blocks", List.of(
                    "nuclearage:radiocalcite_block,20",
                    "nuclearage:radionite_block,15",
                    "nuclearage:radiorite_block,10",
                    "nuclearage:radioschist_block,25",
                    "nuclearage:radiosite_block,8",
                    "nuclearage:radiotuffite_block,18"
                    ), ModConfig::validateBlockName);
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> RADIOACTIVE_ITEM_STRINGS = COMMON_BUILDER
            .comment("List of all radioactive items.")
            .defineListAllowEmpty("radioactivity.items", List.of(
                    "nuclearage:radiocalcite,18",
                    "nuclearage:radionite,13",
                    "nuclearage:radiorite,8",
                    "nuclearage:radioschist,23",
                    "nuclearage:radiosite,6",
                    "nuclearage:radiotuffite,16"
            ), ModConfig::validateBlockName);
    public static final ForgeConfigSpec COMMON_CONFIG = COMMON_BUILDER.build();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        if (SERVER_CONFIG.isLoaded()) {
            ServerConfig.testValue = TEST_VALUE.get();
        }

        if (COMMON_CONFIG.isLoaded()) {
            CommonConfig.RADIOACTIVE_BLOCKS = RADIOACTIVE_BLOCK_STRINGS.get().stream()
                    .map(blockEntry -> {
                        String[] parameters = blockEntry.split(",");
                        if (parameters.length < 2) return null;

                        if (validateBlockName(parameters[0])) {
                            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(parameters[0]));
                            int radiationUnitsPerTick = Math.max(Integer.parseInt(parameters[1]), 0);
                            return new RadiatedBlock(block, radiationUnitsPerTick);
                        }
                        return null;
                    }).collect(Collectors.toSet());

            CommonConfig.RADIOACTIVE_ITEMS = RADIOACTIVE_ITEM_STRINGS.get().stream()
                    .map(blockEntry -> {
                        String[] parameters = blockEntry.split(",");
                        if (parameters.length < 2) return null;

                        if (validateItemName(parameters[0])) {
                            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(parameters[0]));
                            int radiationUnitsPerTick = Math.max(Integer.parseInt(parameters[1]), 0);
                            return new RadiatedItem(item, radiationUnitsPerTick);
                        }
                        return null;
                    }).collect(Collectors.toSet());
        }
    }

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
    }

    private static boolean validateBlockName(final Object obj)
    {
        return obj instanceof final String blockName && ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(blockName));
    }

}
