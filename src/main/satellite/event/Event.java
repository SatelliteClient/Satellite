package satellite.event;

import satellite.Client;
import satellite.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class Event<T> {
	
	public boolean cancelled;
	public EventType type;
	public EventDirection direction;
	
	public boolean isCancelled() {
		return cancelled;
	}
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	public EventType getType() {
		return type;
	}
	public void setType(EventType type) {
		this.type = type;
	}
	public EventDirection getDirection() {
		return direction;
	}
	public void setDirection(EventDirection direction) {
		this.direction = direction;
	}
	
	public boolean isPre() {
		if(type == null)
			return false;
		
		return type == EventType.PRE;
	}
	
	public boolean isPost() {
		if(type == null)
			return false;
		
		return type == EventType.POST;
	}
	
	public boolean isIncoming() {
		if(direction == null)
			return false;
		
		return direction == EventDirection.INCOMING;
	}
	
	public boolean isOutgoing() {
		if(direction == null)
			return false;
		
		return direction == EventDirection.OUTGOING;
	}
	
}