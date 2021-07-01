package com.github.satellite.mixin.client;

import net.minecraft.client.network.NetHandlerPlayClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({NetHandlerPlayClient.class})
public interface AccessorNetHandlerPlayClient {
    @Accessor("doneLoadingTerrain")
    boolean doneLoadingTerrain();

    @Accessor("doneLoadingTerrain")
    void setDoneLoadingTerrain(boolean paramBoolean);
}