package com.github.satellite.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({EntityRenderer.class})
public interface AccessorEntityRenderer {
    @Accessor("shaderGroup")
    ShaderGroup getShaderGroup();

    @Accessor("shaderGroup")
    void setShaderGroup(ShaderGroup paramShaderGroup);
}
