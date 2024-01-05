package net.dotnomi.nuclearage.datageneration;

import net.dotnomi.nuclearage.CreateNuclearAge;
import net.dotnomi.nuclearage.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, CreateNuclearAge.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.RADIOCALCITE);
        simpleItem(ModItems.RADIONITE);
        simpleItem(ModItems.RADIORITE);
        simpleItem(ModItems.RADIOSCHIST);
        simpleItem(ModItems.RADIOSITE);
        simpleItem(ModItems.RADIOTUFFITE);

        simpleItem(ModItems.COMPRESSED_COAL);
        simpleItem(ModItems.GRAPHITE);

        simpleItem(ModItems.GEIGER_COUNTER);
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(CreateNuclearAge.MOD_ID,"item/" + item.getId().getPath()));
    }
}
