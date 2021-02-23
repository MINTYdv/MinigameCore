package com.minty.leemonmc.core.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.minty.leemonmc.core.CoreMain;

public class CoreInitEvent extends Event {

	 private static final HandlerList handlers = new HandlerList();
	 
	 private CoreMain main;
	 
	 public CoreInitEvent(CoreMain main)
	 {
		 this.main = main;
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
	 
}
