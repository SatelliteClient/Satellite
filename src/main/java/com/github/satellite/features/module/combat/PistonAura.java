package com.github.satellite.features.module.combat;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.NumberSetting;
import com.github.satellite.utils.*;

import net.minecraft.block.BlockCompressedPowered;
import net.minecraft.block.BlockEmptyDrops;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;

public class PistonAura extends Module {

    public PistonAura() {
        super("PistonAura", Keyboard.KEY_NONE, Category.COMBAT);
    }

    NumberSetting range;
    BooleanSetting checkPiston;

    @Override
    public void init() {
        super.init();
        this.range = new NumberSetting("Range", 5.2, Integer.MIN_VALUE, 100, .1);
        this.checkPiston = new BooleanSetting("checkPiston", true);
        addSetting(range, checkPiston);
    }

    int progress = 0;

    EnumFacing facing;
    int sleep;

    @Override
    public void onEnable() {
        progress = 0;
        super.onEnable();
    }

    @Override
    public void onEvent(Event<?> e) {
        if (e instanceof EventMotion && e.isPost()) {
            InventoryUtils.push();
            TargetUtils.findTarget(range.value);
            if (TargetUtils.getDistance(null) > range.value) return;
            Entity entity = TargetUtils.currentTarget;

            if (entity instanceof EntityPlayer) {
                int obsidian = InventoryUtils.pickItem(49, false);
                int piston = InventoryUtils.pickItem(33, false);
                int power = InventoryUtils.pickItem(152, false);
                int crystal = InventoryUtils.pickItem(426, false);

                if (obsidian == -1 || piston == -1 || power == -1 || (crystal == -1 && !mc.player.inventory.offHandInventory.get(0).getItem().getClass().equals(Item.getItemById(426).getClass()))) {
                    ClientUtils.addChatMsg("\u00A77[Satellite] \u00A74Item Not Found ");
                    toggle();
                    return;
                }


                //ここいじれば高さ変えれる(後ろに".offset(EnumFacing.UP)"追加)
                BlockPos pos = new BlockPos(entity).offset(EnumFacing.UP);
                //

                ClientUtils.addChatMsg("" + progress);

                if (sleep>0) {
                    sleep--;
                }else {
                    switch (progress) {
                        case 0:

                            facing = getFacing(pos);

                            if (facing == null) return;

                            // base
                            BlockPos Pos = new BlockPos(pos).offset(facing).offset(EnumFacing.DOWN);

                            InventoryUtils.setSlot(obsidian);

                            BlockUtils event = BlockUtils.isPlaceable(Pos, 0, false);

                            BlockUtils.doPlace(event, true);

                            Pos = new BlockPos(pos).offset(facing).offset(facing).offset(EnumFacing.DOWN);

                            InventoryUtils.setSlot(obsidian);

                            event = BlockUtils.isPlaceable(Pos, 0, false);

                            BlockUtils.doPlace(event, true);

                            progress++;
                            break;
                        case 1:
                            // piston
                            Pos = new BlockPos(pos).offset(facing, 2);
                            InventoryUtils.setSlot(piston);
                            event = BlockUtils.isPlaceable(Pos, 0, false);
                            BlockUtils.doPlace(event, true);

                            //power
                            Pos = new BlockPos(pos).offset(facing, 3);
                            InventoryUtils.setSlot(power);
                            event = BlockUtils.isPlaceable(Pos, 0, false);
                            BlockUtils.doPlace(event, false);
                            for (EnumFacing f : EnumFacing.values()) {
                                if (f == facing.rotateY().rotateY()) continue;
                                if (!(mc.world.getBlockState(Pos).getBlock() instanceof BlockCompressedPowered)) {
                                    Pos = new BlockPos(pos).offset(facing, 2).offset(f);
                                    event = BlockUtils.isPlaceable(Pos, 0, false);
                                    BlockUtils.doPlace(event, false);
                                }
                            }

                            //crystal
                            Pos = new BlockPos(pos).offset(facing);
                            CrystalUtils.placeCrystal(Pos);

                            int i = 0;
                            for (Entity target : mc.world.loadedEntityList) {
                                if (entity.getDistance(target) > range.value)
                                    continue;
                                if (target instanceof EntityEnderCrystal) {
                                    mc.playerController.attackEntity(mc.player, target);
                                    i++;
                                }
                            }

                            if (i > 0) {
                                mc.world.setBlockToAir(pos.offset(facing, 1));
                                if (mc.world.getBlockState(pos.offset(facing, 2)).getBlock() instanceof BlockEmptyDrops)
                                    mc.world.setBlockToAir(pos.offset(facing, 1).offset(EnumFacing.UP));
                                else
                                    mc.world.setBlockToAir(pos.offset(facing, 2));
                            }

                            progress = 1;
                            sleep += mc.player.ticksExisted%2==0?1:0;
                            break;
                    }
                }
            }
            InventoryUtils.pop();
        }
        if (e instanceof EventMotion && e.isPre()) {
            EventMotion event = (EventMotion)e;

            if (progress > 0) {
                switch (facing) {

                    case NORTH:
                        event.yaw = 180;
                        break;
                    case SOUTH:
                        event.yaw = 0;
                        break;
                    case WEST:
                        event.yaw = 90;
                        break;
                    case EAST:
                        event.yaw = -90;
                        break;

                }
                event.pitch = 90;
            }
        }
        super.onEvent(e);
    }

    public EnumFacing getFacing(BlockPos position) {
        for (EnumFacing f : EnumFacing.values()) {
            if (f.getAxis().equals(Axis.Y)) continue;

            BlockPos pos = new BlockPos(position).offset(f);

            if (mc.world.isAirBlock(pos.offset(f, 0)) && mc.world.isAirBlock(pos.offset(f, 1)) && mc.world.checkNoEntityCollision(new AxisAlignedBB(pos, pos.offset(f, 2)))) {

                for (EnumFacing fa : EnumFacing.values()) {
                    if (fa == f.rotateY().rotateY()) continue;
                    if (mc.world.isAirBlock(pos.offset(f, 1).offset(fa))) {
                        return f;
                    }
                }
            }
        }
        return null;
    }
}