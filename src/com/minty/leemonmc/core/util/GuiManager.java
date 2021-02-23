package com.minty.leemonmc.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.minty.leemonmc.core.CoreMain;

public class GuiManager implements Listener {

	private Map<Player, Map<Class<? extends GuiBuilder>, GuiBuilder>> registeredMenus = new HashMap<>();
	private Map<Class<? extends GuiBuilder>, GuiBuilder> registeredMenusPublic = new HashMap<>();
	private CoreMain main;

	public GuiManager(CoreMain _main) {
		main = _main;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event){
	
		Player player = (Player) event.getWhoClicked();
		Inventory inv = event.getInventory();
		ItemStack current = event.getCurrentItem();
		
		if(event.getCurrentItem() == null) return;

		try
		{
			boolean isPublic = true;
			GuiBuilder currentMenu = null;
			for(Entry<Class<? extends GuiBuilder>, GuiBuilder> entry : getRegisteredMenusPublic().entrySet())
			{
				if(entry.getValue().name().equalsIgnoreCase(event.getInventory().getName())) {
					currentMenu = entry.getValue();
					isPublic = true;
					break;
				} else {
					isPublic = false;
				}
			}
			
			if(getRegisteredMenus().get(player).containsKey(currentMenu) || getRegisteredMenus().get(player).values().contains(currentMenu))
			{
				isPublic = false;
			}
			
			if(isPublic == false)
			{
				
				if(event.getAction() == InventoryAction.PICKUP_HALF)
				{
					getRegisteredMenus().get(player).values().stream()
					.filter(menu -> inv.getName().equalsIgnoreCase(menu.name()))
					.forEach(menu -> {
						menu.onRightClick(player, inv, current, event.getSlot());
						event.setCancelled(true);
					});
				} else
				{
					getRegisteredMenus().get(player).values().stream()
					.filter(menu -> inv.getName().equalsIgnoreCase(menu.name()))
					.forEach(menu -> {
						menu.onClick(player, inv, current, event.getSlot());
						event.setCancelled(true);
					});
				}
			} else
			{

				if(event.getAction() == InventoryAction.PICKUP_HALF)
				{
					getRegisteredMenusPublic().values().stream()
					.filter(menu -> inv.getName().equalsIgnoreCase(menu.name()))
					.forEach(menu -> {
						menu.onRightClick(player, inv, current, event.getSlot());
						event.setCancelled(true);
					});
				} else
				{
					getRegisteredMenusPublic().values().stream()
					.filter(menu -> inv.getName().equalsIgnoreCase(menu.name()))
					.forEach(menu -> {
						menu.onClick(player, inv, current, event.getSlot());
						event.setCancelled(true);
					});
				}
			}
		} catch(Exception e)
		{
			// Ignore it <3
		}


	}

	public void addMenu(GuiBuilder m, Player p)
	{
		if(!getRegisteredMenus().containsKey(p)) {
			getRegisteredMenus().put(p, new HashMap<>());
		}
		getRegisteredMenus().get(p).put(m.getClass(), m);
	}
	
	public void addMenu(GuiBuilder m)
	{
		getRegisteredMenusPublic().put(m.getClass(), m);
	}

	public void open(Player player, Class<? extends GuiBuilder> gClass){
		
		if(!getRegisteredMenus().containsKey(player))
		{
			getRegisteredMenus().put(player, new HashMap<>());
		}
		
		if(!getRegisteredMenus().get(player).containsKey(gClass) && !getRegisteredMenusPublic().containsKey(gClass)) return;

		boolean isPublic = false;
		if(getRegisteredMenusPublic().containsKey(gClass))
		{
			isPublic = true;
		}
		
		if(isPublic == false)
		{
			GuiBuilder menu = getRegisteredMenus().get(player).get(gClass);
			Inventory inv = Bukkit.createInventory(null, menu.getSize(), menu.name());
			menu.contents(player, inv);
			
			new BukkitRunnable() {
				
				@Override
				public void run() {
					player.openInventory(inv);
				}
				
			}.runTaskLater(CoreMain.getInstance(), 1);
		} else
		{
			GuiBuilder menu = getRegisteredMenusPublic().get(gClass);
			Inventory inv = Bukkit.createInventory(null, menu.getSize(), menu.name());
			menu.contents(player, inv);
			
			new BukkitRunnable() {
				
				@Override
				public void run() {
					player.openInventory(inv);
				}
				
			}.runTaskLater(CoreMain.getInstance(), 1);
		}
		
	}

	public Map<Class<? extends GuiBuilder>, GuiBuilder> getRegisteredMenusPublic() {
		return registeredMenusPublic;
	}
	
	public Map<Player, Map<Class<? extends GuiBuilder>, GuiBuilder>> getRegisteredMenus() {
		return registeredMenus;
	}
	
}