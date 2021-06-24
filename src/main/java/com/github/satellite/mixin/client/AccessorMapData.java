package com.github.satellite.mixin.client;

import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(MapData.class)
public interface AccessorMapData {
    @Accessor("mapDecorations")
    Map<String, MapDecoration> getMapDecorations();
}