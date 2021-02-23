package com.minty.leemonmc.core.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface GuiBuilder
{
	
	public String name();
	public int getSize();
	public void contents(Player player, Inventory inv);
	public void onClick(Player player, Inventory inv, ItemStack current, int slot);
	public void onRightClick(Player player, Inventory inv, ItemStack current, int slot);
}