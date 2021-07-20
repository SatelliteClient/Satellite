package com.github.satellite.features.module.combat;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.Arrays;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

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
import com.github.satellite.utils.CrystalUtils;
import com.github.satellite.utils.InventoryUtils;
import com.github.satellite.utils.render.RenderUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;

public class CevBreaker extends Module {

    public CevBreaker() {
        super("CevBreaker", Keyboard.KEY_NONE, Category.COMBAT);
    }

    NumberSetting range;
    ModeSetting breakMode;
    BooleanSetting onClick;
    BooleanSetting autoDisable;
    BooleanSetting positiveCrystal;
    BooleanSetting resetBreakStat;
    BooleanSetting bypass;
    BooleanSetting changeTarget;
    BooleanSetting renderBlocks;

    @Override
    public void init() {
        super.init();
        this.breakMode = new ModeSetting("BreakMode", "Eco", "Check", "Eco", "Positive");
        this.autoDisable = new BooleanSetting("AutoDisable", true);
        this.onClick = new BooleanSetting("OnClick", false);
        this.positiveCrystal = new BooleanSetting("Positive", false);
        this.resetBreakStat = new BooleanSetting("ResetBreakStat", true);
        this.bypass = new BooleanSetting("Bypass", true);
        this.changeTarget = new BooleanSetting("ChangeTarget", false);
        this.renderBlocks = new BooleanSetting("RenderBlocks", true);
        this.range = new NumberSetting("Range", 5, 0, 10, 0.1D);
        addSetting(breakMode, onClick, autoDisable, positiveCrystal, resetBreakStat, bypass, changeTarget, renderBlocks, range);
    }

    int progress = 0;
    BlockPos obsiPos;
    BlockPos crysPos;
    boolean breakFlag;
    boolean flag;
    int civCounter;
    int sleep;
    Entity currentEntity;

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
            if (onClick.isEnable() && obsiPos == null) return;
            int slot = InventoryUtils.getSlot();
            if (changeTarget.isEnable()) {
                findTarget();
            }else if (currentEntity == null || currentEntity.getDistance(mc.player) > range.value || !currentEntity.isEntityAlive()) {
                findTarget();
            }

            if (currentEntity != null) {
                Entity entity = currentEntity;
                if (entity instanceof EntityPlayer) {
                    int obsidian = InventoryUtils.pickItem(49);
                    int redstone = InventoryUtils.pickItem(152);
                    int pickaxe = InventoryUtils.pickItem(278);
                    int crystal = InventoryUtils.pickItem(426);

                    if (obsidian == -1 || (crystal == -1 && !mc.player.inventory.offHandInventory.get(0).getItem().getClass().equals(Item.getItemById(426).getClass()))) {
                        ClientUtils.addChatMsg("\u00A77[Satellite] \u00A74No "+(obsidian == -1 ? "Obsidian" : "Crystal")+" Found ");
                        if (autoDisable.isEnable()) {
                            InventoryUtils.setSlot(slot);
                            toggle();
                            return;
                        }
                    }
                    if (redstone == -1 && bypass.isEnable()) {
                        ClientUtils.addChatMsg("\u00A77[Satellite] \u00A74No RedstoneBlock Found");
                        if (autoDisable.isEnable()) {
                            InventoryUtils.setSlot(slot);
                            toggle();
                            return;
                        }
                    }
                    if (sleep > 0) {
                        sleep --;
                    }else {
                        entity.move(MoverType.SELF, 0, -2, 0);
                        switch (progress) {
                            case 0:
                                // base 設置
                                //ClientUtils.addChatMsg("" + progress);
                                BlockPos pos = new BlockPos(entity);
                                if (onClick.isEnable()) {
                                    if (bypass.isEnable() && civCounter<1) {
                                        flag = true;
                                        InventoryUtils.setSlot(redstone);
                                    }
                                    else
                                        InventoryUtils.setSlot(obsidian);
                                    BlockUtils.doPlace(BlockUtils.isPlaceable(obsiPos, 0, true), false);
                                }else {
                                    for(BlockPos add : block) {
                                        if (Arrays.asList(block).indexOf(add) != -1 && bypass.isEnable() && civCounter<1) {
                                            flag = true;
                                            InventoryUtils.setSlot(redstone);
                                        }
                                        else
                                            InventoryUtils.setSlot(obsidian);
                                        BlockUtils util = BlockUtils.isPlaceable(pos.add(add), 0, false);
                                        if(util != null) {
                                            util.doPlace(true);
                                        }
                                    }
                                }
                                // クリスタル設置
                                //ClientUtils.addChatMsg("" + progress);
                                InventoryUtils.setSlot(crystal);
                                if (onClick.isEnable())
                                    CrystalUtils.placeCrystal(crysPos);
                                else
                                    CrystalUtils.placeCrystal(new BlockPos(entity.posX, entity.posY+3, entity.posZ));
                                progress++;
                                break;
                            case 1:
                                // 土台破壊
                                //ClientUtils.addChatMsg("" + progress);
                                InventoryUtils.setSlot(pickaxe);
                                if (onClick.isEnable()) {
                                    mc.playerController.onPlayerDamageBlock(obsiPos, EnumFacing.UP);
                                    mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, obsiPos, EnumFacing.UP));
                                    if (mc.world.isAirBlock(obsiPos)) {
                                        for (Entity target : mc.world.loadedEntityList) {
                                            if (entity.getDistance(target) > range.value)
                                                continue;
                                            if (target instanceof EntityEnderCrystal) {
                                                mc.playerController.attackEntity(mc.player, target);
                                            }
                                        }
                                        breakFlag = true;
                                    }
                                    if (bypass.isEnable() && civCounter<1) {
                                        mc.playerController.onPlayerDamageBlock(new BlockPos(entity).add(0, 2, 0), EnumFacing.UP);
                                        sleep += 30;
                                    }
                                }
                                else {
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
                                        breakFlag = true;
                                    }
                                    if (bypass.isEnable() && civCounter<1) {
                                        mc.playerController.onPlayerDamageBlock(new BlockPos(entity).add(0, 2, 0), EnumFacing.UP);
                                        sleep += 30;
                                    }
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
                InventoryUtils.setSlot(slot);
                return;
            }
            InventoryUtils.setSlot(slot);
        }
        if(e instanceof EventMotion) {
        }
        if(e instanceof EventRenderWorld) {
            if (renderBlocks.isEnable() && currentEntity != null) {
                if (!onClick.isEnable()) {
                    for(BlockPos tart : block) {
                        BlockPos pos = tart.add(currentEntity.getPositionVector().x, currentEntity.getPositionVector().y, currentEntity.getPositionVector().z);
                        Color color = mc.world.getBlockState(pos).isFullBlock()?new Color(237, 78, 66, 0x40):new Color(66, 237, 95, 0x40);
                        RenderUtils.drawBlockBox(pos, color);
                    }
                }
            }

            RayTraceResult over = mc.getRenderViewEntity().rayTrace(10, mc.getRenderPartialTicks());

            if (over != null && over.sideHit != null && over.getBlockPos() != null) {
                if (renderBlocks.isEnable() && obsiPos == null) {
                    RenderUtils.drawBlockBox(over.getBlockPos().offset(over.sideHit), new Color(0xff, 0xff, 0xff, 0x40));
                }
                if (obsiPos != null) {
                    if (renderBlocks.isEnable()) {
                        RenderUtils.drawBlockBox(obsiPos, new Color(0x42, 0x87, 0xf5, 0x40));
                        RenderUtils.drawBlockBox(crysPos, new Color(0xf5, 0x87, 0x42, 0x20));
                    }
                }else {
                    if (mc.currentScreen == null) {
                        if (Mouse.isButtonDown(1)) {
                            obsiPos = over.getBlockPos();
                            crysPos = over.getBlockPos().offset(over.sideHit);
                        }
                        if (Mouse.isButtonDown(0)) {
                            obsiPos = over.getBlockPos();
                            crysPos = over.getBlockPos().offset(EnumFacing.UP);
                        }
                    }
                }
            }
        }
        super.onEvent(e);
    }

    public void findTarget() {
        currentEntity = (Entity) mc.world.loadedEntityList.stream().filter((e) -> e != mc.player && e instanceof EntityLivingBase && e.getDistance(mc.player)<range.value).findFirst().orElse(null);
    }

    @Override
    public void onEnable() {
        findTarget();
        obsiPos = null;
        crysPos = null;
        progress = 0;
        if (resetBreakStat.isEnable()) {
            breakFlag = false;
        }
        flag = false;
        civCounter = 0;
        sleep = 0;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

}