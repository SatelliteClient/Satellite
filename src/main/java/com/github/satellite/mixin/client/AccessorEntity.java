package com.github.satellite.mixin.client;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Entity.class})
public interface AccessorEntity {

    @Accessor("width")
    float width();

    @Accessor("height")
    float height();

    @Accessor("width")
    void setWidth(float paramFloat);

    @Accessor("height")
    void setHeight(float paramFloat);

}
