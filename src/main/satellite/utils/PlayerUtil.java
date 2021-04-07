package satellite.utils;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec3d;

public class PlayerUtil {
	protected static Minecraft mc = Minecraft.getMinecraft();
	
	public EntityPlayerSP player;
	
	public PlayerUtil(EntityPlayerSP e) {
		this.player = e;
	}
	
	public void vClip(double d) {
		mc.player.setPosition(mc.player.posX, mc.player.posY + d, mc.player.posZ);
	}

	public void vClip2(double d) {
		mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY+d, mc.player.posZ, false));
	}
	
	public int InputY() {
		return (MovementInput.jumpKeyDown() ? 1 : 0) + (MovementInput.sneakKeyDown() ? -1 : 0);
	}
	
	public double getSpeed()
	{
		return Math.sqrt(Math.pow(player.motionX, 2)+Math.pow(player.motionZ, 2));
	}
	
	public double toRadian(double d)
	{
		return d * Math.PI / 180;
	}
	
	public void Strafe(double d)
	{
		float Forward = (player.movementInput.forwardKeyDown?1:0)-(player.movementInput.backKeyDown?1:0);
		float Strafing = (player.movementInput.rightKeyDown?1:0)-(player.movementInput.leftKeyDown?1:0);

		double r = Math.atan2(Forward, Strafing)-1.57079633-toRadian(player.rotationYaw);
		
		if(Forward==0&&Strafing==0) {d=0;}

		player.motionX=0;
		player.motionZ=0;

		player.motionX=Math.sin(r)*d;
		player.motionZ=Math.cos(r)*d;
	}
	
	public void Move()
	{
		player.moveEntity(MoverType.SELF, player.motionX, player.motionY, player.motionZ);
	}
	
	public void freeze()
	{
		player.motionX=0;
		player.motionY=0;
		player.motionZ=0;
	}
	
	static class MovementInput {
		
		public static boolean forwardKeyDown() {
			return Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode());
		}
		public static boolean backKeyDown() {
			return Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode());
		}
		public static boolean leftKeyDown() {
			return Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode());
		}
		public static boolean rightKeyDown() {
			return Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode());
		}
		public static boolean jumpKeyDown() {
			return Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
		}
		public static boolean sneakKeyDown() {
			return Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode());
		}
	}
}