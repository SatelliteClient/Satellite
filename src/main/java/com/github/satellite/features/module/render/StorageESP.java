package com.github.satellite.features.module.render;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventRenderWorld;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.utils.render.RenderUtils;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class StorageESP extends Module {
	public StorageESP() {
		super("StorageESP", Keyboard.KEY_NONE, Category.RENDER);
	}

	CopyOnWriteArrayList<BlockPos> Strages = new CopyOnWriteArrayList<BlockPos>();

    @Override
    public void onEvent(Event<?> e) {
    	if(e instanceof EventUpdate) {
    		for(BlockPos pos : Strages) {
        		if(!(mc.world.getBlockState(pos).getBlock() instanceof BlockChest)) {
        			Strages.remove(Strages.indexOf(pos));
        		}
    		}
    	}
    	
    	if(e instanceof EventRenderWorld) {
    		for(TileEntity pos : mc.world.loadedTileEntityList) {
    			if(pos.getBlockType() instanceof BlockChest)
    				RenderUtils.drawBlockBox(pos.getPos(), new Color(255, 255, 255, 0x40));
    			if(pos.getBlockType() instanceof BlockEnderChest)
    				RenderUtils.drawBlockBox(pos.getPos(), new Color(108, 0, 143, 0x40));
    		}
    	}
    	super.onEvent(e);
    }

	@Override
	public void onEnable() {
		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}
}
