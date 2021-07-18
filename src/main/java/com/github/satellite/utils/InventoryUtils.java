package com.github.satellite.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class InventoryUtils {

	protected static Minecraft mc = Minecraft.getMinecraft();

	public static short actionNumber = 0;

	private static int currentItem;

	public static int getSlot() {
		return mc.player.inventory.currentItem;
	}

	public static void setSlot(int slot) {
		if (slot > 8 || slot < 0) return;
		mc.player.inventory.currentItem = slot;
	}

	public static int getPlaceableItem() {
		ArrayList<ItemStack> item = new ArrayList<ItemStack>();

		for (int i1 = 0; i1 < 9; i1++) {
			if (mc.player.inventory.mainInventory.get(i1).getItem() instanceof ItemBlock)
				item.add(mc.player.inventory.mainInventory.get(i1));
		}

		item.sort((a, b) -> b.getCount() - a.getCount());
		if (!(item.size() < 1))
			return mc.player.inventory.mainInventory.indexOf(item.get(0));
		return -1;
	}

	public static int pickItem(int item) {

		ArrayList<ItemStack> filter = new ArrayList<ItemStack>();
		for (int i1 = 0; i1 < 9; i1++) {
			if (Item.getIdFromItem(mc.player.inventory.mainInventory.get(i1).getItem()) == item) {
				filter.add(mc.player.inventory.mainInventory.get(i1));
			}
		}

		//filter.sort((a, b) -> b.func_190916_E() - a.func_190916_E());
		if (!(filter.size() < 1))
			return mc.player.inventory.mainInventory.indexOf(filter.get(0));
		return -1;
	}

	public static int pickItem(int item, boolean allowInventory) {

		ArrayList<ItemStack> filter = new ArrayList<ItemStack>();
		for (int i1 = 0; i1 < (allowInventory?mc.player.inventory.mainInventory.size():9); i1++) {
			if (Item.getIdFromItem(mc.player.inventory.mainInventory.get(i1).getItem()) == item) {
				filter.add(mc.player.inventory.mainInventory.get(i1));
			}
		}

		//filter.sort((a, b) -> b.func_190916_E() - a.func_190916_E());
		if (!(filter.size() < 1))
			return mc.player.inventory.mainInventory.indexOf(filter.get(0));
		return -1;
	}

	public static void moveItem(int before, int after) {
		mc.playerController.windowClick(mc.player.inventoryContainer.windowId, before, 0, ClickType.PICKUP, mc.player);
		mc.playerController.windowClick(mc.player.inventoryContainer.windowId, after, 0, ClickType.PICKUP, mc.player);
		mc.playerController.windowClick(mc.player.inventoryContainer.windowId, before, 0, ClickType.PICKUP, mc.player);
	}

	public static void clickWindow(int slotIn, ClickType type) {
		//mc.player.inventoryContainer.getNextTransactionID(mc.player.inventory);
	}

	public static void push() {
		currentItem = mc.player.inventory.currentItem;
	}

	public static void pop() {
		mc.player.inventory.currentItem = currentItem;
	}
}
