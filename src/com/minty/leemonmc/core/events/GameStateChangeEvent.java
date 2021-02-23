package com.minty.leemonmc.core.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.minty.leemonmc.basics.core.GameState;

public class GameStateChangeEvent extends Event {

	 private static final HandlerList handlers = new HandlerList();
	 
	 private GameState newState;
	 private GameState oldState;
	 
	 public GameStateChangeEvent(GameState newState, GameState oldState)
	 {
		 this.newState = newState;
		 this.oldState = oldState;
	 }
	 
	 public HandlerList getHandlers()
	 {
	    return handlers; 
	 } 
	 
	 public static HandlerList getHandlerList()
	 { 
		return handlers;
	 }
	
	 public GameState getNewState() {
		 return newState;
	 }
	 
	 public GameState getOldState() {
		return oldState;
	}
	
}
