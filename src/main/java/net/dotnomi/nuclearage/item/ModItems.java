package net.dotnomi.nuclearage.item;

import net.dotnomi.nuclearage.CreateNuclearAge;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CreateNuclearAge.MOD_ID);

    public static final RegistryObject<Item> GRAPHITE = ITEMS.register("graphite",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> COMPRESSED_COAL = ITEMS.register("compressed_coal",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> GEIGER_COUNTER = ITEMS.register("geiger_counter",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> RADIOCALCITE = ITEMS.register("radiocalcite",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> RADIONITE = ITEMS.register("radionite",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> RADIORITE = ITEMS.register("radiorite",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> RADIOSCHIST = ITEMS.register("radioschist",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> RADIOSITE = ITEMS.register("radiosite",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> RADIOTUFFITE = ITEMS.register("radiotuffite",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
