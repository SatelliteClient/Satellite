package com.github.satellite.features.module.world;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventPlayerInput;
import com.github.satellite.event.listeners.EventRenderWorld;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.utils.BlockUtils;
import com.github.satellite.utils.InventoryUtils;
import com.github.satellite.utils.MathUtils;
import com.github.satellite.utils.render.RenderUtils;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

public class AutoSlime extends Module {

	public AutoSlime() {
		super("AutoSlime", Keyboard.KEY_NONE, Category.WORLD);
	}

	BooleanSetting swing;

	@Override
	public void init() {
		this.swing = new BooleanSetting("Swing", true);
		addSetting(swing);
		super.init();
	}

	public CopyOnWriteArrayList<BlockUtils> placeablePos = new CopyOnWriteArrayList<BlockUtils>();

	@Override
	public void onEvent(Event<?> e) {
		if(e instanceof EventPlayerInput) {
			EventPlayerInput event = (EventPlayerInput)e;
			if(placeablePos.size()>3)
				event.setSneak(true);
		}
		if(e instanceof EventUpdate) {
			if(e.isPre()) {
				if(!mc.player.isSneaking())
					mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
				for(int i=0; i < 1; i++) {
					placeablePos = new CopyOnWriteArrayList<BlockUtils>();

					int s = mc.player.inventory.currentItem;

					if(InventoryUtils.getPlaceableItem() != -1)
						mc.player.inventory.currentItem = InventoryUtils.getPlaceableItem();

					scanBlock(mc.playerController.getBlockReachDistance()-1);

					for(int i1=0; i1<1; i1++) {
						if(!placeablePos.isEmpty() && mc.world.getBlockState(placeablePos.get(0).pos).getBlock().isReplaceable(mc.world, placeablePos.get(0).pos)) {
							placeablePos.sort(Comparator.comparingDouble(p -> p.dist));
							int obsi = InventoryUtils.pickItem(49);
							if(obsi != -1) {mc.player.inventory.currentItem = obsi;}
							placeablePos.get(0).doPlace(swing.isEnable());
						}
					}
					mc.player.inventory.currentItem = s;
				}

				if(!mc.player.isSneaking())
					mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
			}
		}

		if(e instanceof EventRenderWorld) {
			Color color = new Color(255, 255, 255, 0x40);
			for(BlockUtils pos : placeablePos) {
				RenderUtils.drawBlockBox(pos.pos, color);
			}
		}

		if(e instanceof EventMotion) {
			EventMotion event = (EventMotion)e;
			if(!placeablePos.isEmpty()) {
				event.setYaw((float) placeablePos.get(0).rotx);
				event.setPitch((float) placeablePos.get(0).roty);
			}
		}
		super.onEvent(e);
	}

	public void scanBlock(double reach) {
		for(int dy=(int) -reach; dy<reach; dy++)
			for(int dx=(int) -reach; dx<reach; dx++)
				for(int dz=(int) -reach; dz<reach; dz++)
					if(Math.sqrt(dx*dx+dy*dy+dz*dz) <= reach) {
						int x = (int) (mc.player.posX + dx);
						int y = (int) (mc.player.posY + dy);
						int z = (int) (mc.player.posZ + dz);
						scanPos(new BlockPos(x, y, z), Math.sqrt(dx*dx+dy*dy+dz*dz));
					}
	}

	public void scanPos(double x, double y, double z, double dist) {
		scanPos(new BlockPos(x, y, z), dist);
	}

	public void scanPos(BlockPos pos, double dist) {
		int i = 0;

		double[] vec = new double[] {pos.getX(), pos.getZ()};

		double d = MathUtils.getDistanceSq(vec);

		if(d > 999) {i++;}
		if(d > 1002) {i--;}
		if(pos.getY()>mc.player.posY-1) {i=0;}

		if(i <= 0) return;

		BlockUtils block = BlockUtils.isPlaceable(pos, dist, true);
		if(block != null) placeablePos.add(block);
	}

	@Override
	public void onEnable() {
		placeablePos = new CopyOnWriteArrayList<BlockUtils>();
		super.onEnable();
	}
}
