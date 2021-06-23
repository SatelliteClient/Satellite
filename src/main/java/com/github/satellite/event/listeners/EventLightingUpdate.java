package com.github.satellite.event.listeners;

import com.github.satellite.event.Event;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;

public class EventLightingUpdate extends Event<EventJump> {

    BlockPos pos;
    EnumSkyBlock lightType;

    public EventLightingUpdate(BlockPos pos, EnumSkyBlock lightType) {
        this.pos = pos;
        this.lightType = lightType;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public EnumSkyBlock getLightType() {
        return lightType;
    }

    public void setLightType(EnumSkyBlock lightType) {
        this.lightType = lightType;
    }

}
