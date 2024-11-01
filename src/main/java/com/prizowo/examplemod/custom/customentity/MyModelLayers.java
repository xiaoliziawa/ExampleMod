package com.prizowo.examplemod.custom.customentity;

import com.prizowo.examplemod.Examplemod;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class MyModelLayers {
    public static final ModelLayerLocation MY_HUMANOID = new ModelLayerLocation(
            Objects.requireNonNull(ResourceLocation.tryBuild(Examplemod.MODID, "my_humanoid")), "main");
}
