package com.minty.leemonmc.core.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.minty.leemonmc.core.CoreMain;
import com.minty.leemonmc.core.util.GuiManager;

public class GuisLoadingEvent extends Event {

	 private static final HandlerList handlers = new HandlerList();
	 
	 private CoreMain main;
	 private GuiManager manager;
	 
	 public GuisLoadingEvent(CoreMain _main, GuiManager _manager)
	 {
		 main = _main;
		 this.manager = _manager;
	 }
	 
	 public HandlerList getHandlers()
	 {
	    return handlers; 
	 } 
	 
	 public static HandlerList getHandlerList()
	 { 
		return handlers;
	 }
	
	public CoreMain getMain() {
		return main;
	}
	
	public GuiManager getGuiManager() {
		return manager;
	}
	
}
