package com.github.satellite.features.module.combat;

import java.awt.Color;
import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventRenderWorld;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.setting.NumberSetting;
import com.github.satellite.utils.BlockUtils;
import com.github.satellite.utils.ClientUtils;
import com.github.satellite.utils.InventoryUtils;
import com.github.satellite.utils.render.RenderUtils;

import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;

public class CevBreaker extends Module {

    public CevBreaker() {
        super("CevBreaker", Keyboard.KEY_NONE, Category.COMBAT);
    }

    NumberSetting range;
    ModeSetting breakMode;
    BooleanSetting positiveCrystal;
    BooleanSetting resetBreakStat;
    BooleanSetting bypass;

    @Override
    public void init() {
        super.init();
        this.breakMode = new ModeSetting("BreakMode", "Eco", "Check", "Eco", "Positive");
        this.positiveCrystal = new BooleanSetting("Positive", false);
        this.resetBreakStat = new BooleanSetting("ResetBreakStat", false);
        this.bypass = new BooleanSetting("Bypass", true);
        this.range = new NumberSetting("Range", 5.2, Integer.MIN_VALUE, 100, .1);
        addSetting(breakMode, positiveCrystal, resetBreakStat, bypass, range);
    }

    int progress = 0;
    BlockPos obsiPos;
    BlockPos crysPos;
    boolean breakFlag;
    boolean flag;
    int civCounter;
    int sleep;

    public void updateCev() {

    }

    @Override
    public void onEvent(Event<?> e) {
        BlockPos[] block = new BlockPos[] {};
        block = new BlockPos[] {
                new BlockPos(0, 0, 1),
                new BlockPos(0, 1, 1),
                new BlockPos(0, 2, 1),
                new BlockPos(0, 2, 0)
        };
        if (e instanceof EventUpdate) {
            int slot = InventoryUtils.getSlot();

            for(Entity entity : mc.world.loadedEntityList) {
                if (entity == mc.player) continue;
                if (mc.player.getDistance(entity) > 5.2) continue;
                if (entity instanceof EntityPlayer) {
                    int obsidian = InventoryUtils.pickItem(49);
                    int redstone = InventoryUtils.pickItem(152);
                    int pickaxe = InventoryUtils.pickItem(278);
                    int crystal = InventoryUtils.pickItem(426);
                    if (sleep > 0) {
                        sleep --;
                    }else {
                        switch (progress) {
                            case 0:
                                // base 設置
                                //ClientUtils.addChatMsg("" + progress);
                                BlockPos pos = new BlockPos(entity);
                                for(BlockPos add : block) {
                                    if (Arrays.asList(block).indexOf(add) != -1 && bypass.isEnable() && civCounter<1) {
                                        flag = true;
                                        InventoryUtils.setSlot(redstone);
                                    }
                                    else
                                        InventoryUtils.setSlot(obsidian);
                                    BlockUtils util = BlockUtils.isPlaceable(pos.add(add), 0, true);
                                    if(util != null) {
                                        util.doPlace(true);
                                    }
                                }
                                // クリスタル設置
                                //ClientUtils.addChatMsg("" + progress);
                                InventoryUtils.setSlot(crystal);
                                new BlockUtils(new BlockPos(entity.posX, entity.posY+3, entity.posZ), 0, EnumFacing.UP, 0).doPlace(false);
                                progress++;
                                break;
                            case 1:
                                // 土台破壊
                                //ClientUtils.addChatMsg("" + progress);
                                InventoryUtils.setSlot(pickaxe);
                                mc.playerController.onPlayerDamageBlock(new BlockPos(entity).add(0, 2, 0), EnumFacing.UP);
                                mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, new BlockPos(entity).add(0, 2, 0), EnumFacing.UP));
                                if (mc.world.isAirBlock(new BlockPos(entity).add(0, 2, 0))) {
                                    for (Entity target : mc.world.loadedEntityList) {
                                        if (entity.getDistance(target) > range.value)
                                            continue;
                                        if (target instanceof EntityEnderCrystal) {
                                            mc.playerController.attackEntity(mc.player, target);
                                        }
                                    }
                                    //if (breakMode.is("Check"))
                                    //	progress++;
                                    breakFlag = true;
                                }
                                if (bypass.isEnable() && civCounter<1) {
                                    mc.playerController.onPlayerDamageBlock(new BlockPos(entity).add(0, 2, 0), EnumFacing.UP);
                                    sleep += 30;
                                }

                                if (!breakMode.is("Check") && ( breakMode.is("Positive") || (breakMode.is("Eco") && breakFlag)))
                                    progress++;
                                break;
                            case 2:
                                // クリスタル爆破
                                //ClientUtils.addChatMsg("" + progress);
                                int i = 0;
                                for (Entity target : mc.world.loadedEntityList) {
                                    if (entity.getDistance(target) > range.value)
                                        continue;
                                    if (target instanceof EntityEnderCrystal) {
                                        mc.playerController.attackEntity(mc.player, target);
                                        i++;
                                    }
                                }

                                if (i == 0 || positiveCrystal.isEnable() || (bypass.isEnable() && flag))
                                    progress ++;
                                break;
                            case 3:
                                // 土台設置
                                //ClientUtils.addChatMsg("" + progress);
                                BlockUtils.doPlace(BlockUtils.isPlaceable(new BlockPos(entity.posX, entity.posY+2, entity.posZ), 0, true), true);
                                InventoryUtils.setSlot(obsidian);
                                progress=0;
                                civCounter ++;
                                break;
                        }
                    }
                }
            }
            InventoryUtils.setSlot(slot);
        }
        if(e instanceof EventMotion) {
        }
        if(e instanceof EventRenderWorld) {
            if (progress != 0) return;
            for(Entity entity : mc.world.loadedEntityList) {
                if (entity == mc.player) continue;
                if (mc.player.getDistance(entity) > 5.2) continue;
                if (entity instanceof EntityPlayer) {
                    for(BlockPos tart : block) {
                        BlockPos pos = tart.add(entity.getPositionVector().x, entity.getPositionVector().y, entity.getPositionVector().z);
                        Color color = mc.world.getBlockState(pos).isFullBlock()?new Color(237, 78, 66, 0x40):new Color(66, 237, 95, 0x40);
                        RenderUtils.drawBlockBox(pos, color);
                    }
                }
            }

        }
        super.onEvent(e);
    }

    @Override
    public void onEnable() {
        progress = 0;
        if (resetBreakStat.isEnable()) {
            breakFlag = false;
        }
        flag = false;
        civCounter = 0;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        ClientUtils.setTimer(1F);
        super.onDisable();
    }

}