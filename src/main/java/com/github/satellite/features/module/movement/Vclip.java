package com.github.satellite.features.module.movement;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.*;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.setting.NumberSetting;
import com.github.satellite.setting.Setting;
import com.github.satellite.utils.MovementUtils;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;

public class Vclip extends Module {

    public Vclip() {
        super("Vclip", 0, Category.EXPLOIT);
    }
    
    NumberSetting maxHeight, addHeight;
    
    @Override
    public void init() {
    	addSetting(maxHeight = new NumberSetting("MaxHeight", null, 10, 0, 100, 1));
    	addSetting(addHeight = new NumberSetting("AddHeight", null, 0, 0, 100, 1));
    }
    
    @Override
    public void onEnable() {
    	MovementUtils.vClip(maxHeight.value);
    	MovementUtils.move(0, -maxHeight.value, 0);
    	MovementUtils.move(0,  addHeight.value, 0);
    	toggle();
    }
    
}
