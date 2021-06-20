package com.github.satellite.features.module.world;

import com.github.satellite.Satellite;
import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.features.module.ModuleManager;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.ui.HUD.ModuleComparator;
import com.github.satellite.utils.BlockUtils;
import com.github.satellite.utils.InventoryUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;

public class AutoBuild extends Module {

    public AutoBuild() {
        super("AutoBuild", Keyboard.KEY_NONE, Category.WORLD);
    }

    ModeSetting mode;

    @Override
    public void init() {
        this.mode = new ModeSetting("Mode", "Portal", new String[] {"Portal", "Sponge"});
        settings.add(mode);
        super.init();
    }

    Vec3d enablePos;

    @Override
    public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate) {
            setDisplayName("AutoBuild \u00A77" + ((ModeSetting)settings.get(1)).getMode());
            switch(mode.getMode()) {

                case "Portal":
                    BlockPos[] poss = new BlockPos[] {
                            new BlockPos(-1, 0, 0), new BlockPos(0, 0, 0), new BlockPos(1, 0, 0), new BlockPos(2, 0, 0),
                            new BlockPos(-1, 1, 0),                                               new BlockPos(2, 1, 0),
                            new BlockPos(-1, 2, 0),                                               new BlockPos(2, 2, 0),
                            new BlockPos(-1, 3, 0),                                               new BlockPos(2, 3, 0),
                            new BlockPos(-1, 4, 0), new BlockPos(0, 4, 0), new BlockPos(1, 4, 0), new BlockPos(2, 4, 0),
                    };
                    for(BlockPos add : poss) {
                        int sandstone = InventoryUtils.pickItem(49);
                        int currentItem = mc.player.inventory.currentItem;
                        mc.player.inventory.currentItem = sandstone==-1?currentItem:sandstone;
                        BlockUtils util = BlockUtils.isPlaceable(add, 0, true);
                        if(util != null) {
                            util.doPlace(false);
                            mc.player.inventory.currentItem = currentItem;
                            return;
                        }
                        mc.player.inventory.currentItem = currentItem;
                    }
                    break;

                case "Sponge":
                    int sponge = InventoryUtils.pickItem(19);
                    if(sponge != -1) {
                        int item = mc.player.inventory.currentItem;
                        mc.player.inventory.currentItem = sponge;
                        CopyOnWriteArrayList<BlockPos> water = new CopyOnWriteArrayList<>();

                        int range = 3;
                        BlockPos pos = new BlockPos(mc.player);
                        int x = pos.getX();
                        int y = pos.getY();
                        int z = pos.getZ();
                        for(int dx = -range; dx <= range; dx++) {
                            for(int dy = -range; dy <= range; dy++) {
                                for(int dz = -range; dz <= range; dz++) {
                                    pos = new BlockPos(x+dx, y+dy, z+dz);
                                    if (mc.player.getDistanceSq(pos) > Math.pow(mc.playerController.getBlockReachDistance()-1, 2))
                                        continue;
                                    Block block = mc.world.getBlockState(pos).getBlock();
                                    if (block instanceof BlockLiquid) {
                                        BlockLiquid liquid = (BlockLiquid)block;
                                        if (mc.world.getBlockState(pos).getMaterial() == Material.WATER) {
                                            water.add(pos);
                                        }
                                    }
                                }
                            }
                        }

                        if (!water.isEmpty()) {
                            Collections.sort(water, new BlockPosComparatorWithDistance());
                            Collections.sort(water, new BlockPosComparatorWithY());


                            Block block = mc.world.getBlockState(pos.offset(EnumFacing.UP)).getBlock();
                            if (block instanceof BlockLiquid) {
                                mc.playerController.processRightClickBlock(mc.player, mc.world, water.get(0).offset(EnumFacing.UP), EnumFacing.DOWN, new Vec3d(0, 1, 0), EnumHand.MAIN_HAND);
                            }else {
                                mc.playerController.processRightClickBlock(mc.player, mc.world, water.get(0).offset(EnumFacing.DOWN), EnumFacing.UP, new Vec3d(0, 1, 0), EnumHand.MAIN_HAND);
                            }
                            mc.player.inventory.currentItem = item;
                            return;
                        }

                        mc.player.inventory.currentItem = item;
                    }
                    break;

            }
        }
        super.onEvent(e);
    }

    @Override
    public void onEnable() {
        enablePos = mc.player.getPositionVector();
        super.onEnable();
    }


    public static class BlockPosComparatorWithDistance implements Comparator<BlockPos> {
        protected Minecraft mc = Satellite.mc;

        @Override
        public int compare(BlockPos o1, BlockPos o2) {
            if(mc.player.getDistanceSq(o1) > mc.player.getDistanceSq(o2))
                return -1;
            if(mc.player.getDistanceSq(o1) < mc.player.getDistanceSq(o2))
                return 1;
            return 0;
        }
    }
    public static class BlockPosComparatorWithY implements Comparator<BlockPos> {
        protected Minecraft mc = Satellite.mc;

        @Override
        public int compare(BlockPos o1, BlockPos o2) {
            if(o1.getY() > o2.getY())
                return 1;
            if(o1.getY() < o2.getY())
                return -1;
            return 0;
        }
    }
}