package com.prizowo.examplemod.custom.customentity;

import com.prizowo.examplemod.Examplemod;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class MyModelLayers {
    public static final ModelLayerLocation MY_HUMANOID = new ModelLayerLocation(
        ResourceLocation.fromNamespaceAndPath(Examplemod.MODID, "my_humanoid"), "main");
}
