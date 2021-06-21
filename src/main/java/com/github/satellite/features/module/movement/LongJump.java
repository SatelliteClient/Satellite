package com.github.satellite.features.module.movement;

import com.github.satellite.event.Event;
import com.github.satellite.event.listeners.EventMotion;
import com.github.satellite.event.listeners.EventPlayerInput;
import com.github.satellite.event.listeners.EventUpdate;
import com.github.satellite.features.module.Module;
import com.github.satellite.mixin.client.AccessorEntityPlayer;
import com.github.satellite.setting.BooleanSetting;
import com.github.satellite.setting.ModeSetting;
import com.github.satellite.utils.ClientUtils;
import com.github.satellite.utils.MovementUtils;
import net.minecraft.potion.Potion;
import org.lwjgl.input.Keyboard;

public class LongJump extends Module {

	public LongJump() {
		super("LongJump", Keyboard.KEY_NUMPAD0, Category.MOVEMENT);
	}

	ModeSetting mode;

	BooleanSetting useTimer;

	boolean inTimer;
	int state;

	@Override
	public void init() {
		this.mode = new ModeSetting("Mode", "Vanilla", new String[] {"Vanilla", "NCPLow", "NCPHigh", "AAC"});
		this.useTimer = new BooleanSetting("UseTimer", true);
		addSetting(mode, useTimer);
		super.init();
	}

	int progress = 0;
	boolean jumpFlag;

	@Override
	public void onEvent(Event<?> e) {

		switch(mode.getMode()) {
			case "Vanilla":
				if(e instanceof EventUpdate && e.isPre()) {
					MovementUtils.Strafe(1);
				}
				break;

			case "NCPLow":
			{
				if(e instanceof EventMotion && e.isPre()) {
					if(mc.player.posY == mc.player.lastTickPosY && mc.player.collidedVertically && mc.player.onGround && MovementUtils.isMoving()) {
						MovementUtils.Strafe(0.1D);
						mc.player.setPosition(mc.player.lastTickPosX+mc.player.motionX, mc.player.lastTickPosY, mc.player.lastTickPosZ+mc.player.motionZ);
						if(progress>10) {
							MovementUtils.Strafe(.29D);
							mc.player.jump();
							progress=0;
						}
						progress++;
					}
					if(mc.player.motionY == .33319999363422365 && MovementUtils.isMoving()) {
						if (mc.player.isPotionActive(Potion.getPotionById(1))) {
							MovementUtils.Strafe(1.34D);
						} else {
							MovementUtils.Strafe(1.261D);
						}
					}
					MovementUtils.Strafe(MovementUtils.getSpeed());
				}
				break;
			}
			case "NCPHigh":
			{
				if (e instanceof EventPlayerInput) {
					EventPlayerInput event = (EventPlayerInput)e;
					jumpFlag = false;
					if (event.isJump()) {
						jumpFlag = true;
					}
					event.setJump(false);
				}
				if (e instanceof EventUpdate && e.isPre()) {
					if (state > 0) {
						state--;
					} else if (inTimer) {
						inTimer = false;
						ClientUtils.setTimer(1);
					}
					//double[] ncp = new double[] {0.425D, 0.396D, -0.122D, -0.1D, 0.423D, 0.35D, 0.28D, 0.217D, 0.15D, -0.08D};
					double[] ncp = new double[] {.599D, .423D, .35D, .28D, .217D, .15D, .025D, -.00625D, -.038D, -.0693D, -.102D, -.13D, -.18D, -.1D, -.117D, -.14532D, -.1334D, -.1581D, -.183141D, -.170695D, -.195653D, -.221D, -.209D, -.233D, -.25767D, -.314917D, -.371019D, -.426D, -.49588D, -.56436};
					double[] ncpp = new double[] {0.425D, 0.821D, 0.699D, 0.599D};
					//.599D,
					if(MovementUtils.isMoving() || jumpFlag || progress > 0) {
						if(mc.player.onGround || progress > 0) {
							if(progress<ncp.length) {
								mc.player.motionY = ncp[progress];
							}else {
								progress=-1;
							}

							if(progress == 0) {
								mc.player.setPosition(mc.player.lastTickPosX, mc.player.lastTickPosY, mc.player.lastTickPosZ);
								for (double d : ncpp) {
									MovementUtils.vClip2(d, false);
								}
								if (useTimer.isEnable()) {
									this.state = 3;
									this.inTimer = true;
									ClientUtils.setTimer(Math.min(1.0F, 1.0F / (1+ncpp.length/3)));
								}
							}

							((AccessorEntityPlayer)mc.player).speedInAir(.02F);

							if(progress == 0) MovementUtils.Strafe(.29D);
							if(progress == 1) {
								MovementUtils.Strafe(.7D);
							}
							if(progress == 7) {
								MovementUtils.Strafe(.26D);
							}

							progress++;
						}
					}
					MovementUtils.Strafe(MovementUtils.getSpeed());
				}
				break;
			}
			case "AAC":
			{
				if(e instanceof EventMotion && e.isPre()) {
					if(mc.player.onGround) {
						mc.player.motionY=0.42;
					}
					mc.player.motionX *= 1.08;
					mc.player.motionZ *= 1.08;
					mc.player.motionY += 0.05999D;
					mc.player.motionY += 0.01D;
				}
				break;
			}
			case "Mineplex":
				if(e instanceof EventMotion && e.isPre()) {
					if(mc.player.onGround) {
						mc.player.jump();
						MovementUtils.Strafe(.1);
						MovementUtils.vClip(.42 - .42);
					}
					((AccessorEntityPlayer)mc.player).speedInAir(0.03f);
					//mc.player.motionY += 0.05999D;
					MovementUtils.Strafe(MovementUtils.getSpeed());
					if(mc.player.motionY == .33319999363422365) {
						MovementUtils.Strafe(.5);
					}else if(mc.player.motionY < .33319999363422365 && mc.player.motionY > -.42) {
						mc.player.motionY += 0.03D;
					}
				}

		}
		super.onEvent(e);
	}

	@Override
	public void onDisable() {
		progress = 0;
		if(mode.is("NCPLow")) {
			progress=11;
		}
		((AccessorEntityPlayer)mc.player).speedInAir(0.02f);
		if (inTimer) ClientUtils.setTimer(1.0f);
		super.onDisable();
	}
}