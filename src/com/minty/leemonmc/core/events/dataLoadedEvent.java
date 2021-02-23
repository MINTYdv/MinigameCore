package com.minty.leemonmc.core.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.minty.leemonmc.basics.core.cache.Account;

public class dataLoadedEvent extends Event {

	 private static final HandlerList handlers = new HandlerList();
	 
	 private Player player;
	 private Account account;
	 private String uuid;
	 
	 public dataLoadedEvent(Player p, Account _account)
	 {
		 this.player = p;
		 this.uuid = p.getUniqueId().toString();
		 this.account = _account;
	 }
	 
	 public String getUuid() {
		return uuid;
	}
	 
	 public HandlerList getHandlers()
	 {
	    return handlers; 
	 } 
	 
	 public static HandlerList getHandlerList()
	 { 
		return handlers;
	 }
	
	 public Player getPlayer()
	 {
		 return player;
	 }
	 
	 public Account getData()
	 {
		 return account;
	 }
	 
}
