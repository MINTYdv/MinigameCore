package com.minty.leemonmc.core.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.minty.leemonmc.basics.core.Rank;
import com.minty.leemonmc.basics.core.cache.Account;
import com.minty.leemonmc.core.CoreMain;
import com.minty.leemonmc.core.events.GuisLoadingEvent;
import com.minty.leemonmc.core.events.dataLoadedEvent;
import com.minty.leemonmc.core.gui.core.NickConfirmMenu;
import com.minty.leemonmc.core.util.Title;

public class LeeCoreListeners implements Listener {

	private CoreMain main;
	@SuppressWarnings("unused")
	private Title title;
	
	public LeeCoreListeners(CoreMain _main) {
		main = _main;
	}
	
	@EventHandler
	public void onPreLogin(AsyncPlayerPreLoginEvent e) {
		
		if(!main.sql.isConnected())
		{
			main.sql.connection();
			e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cImpossible de se connecter à la base de données. Réessayez.");
		}
	}
	
	@EventHandler
	public void onGuis(GuisLoadingEvent e) {
		e.getGuiManager().addMenu(new NickConfirmMenu(main.getGuiUtils()));
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();
		
		if(main.getSql().isConnected())
		{
			main.getSql().loadData(p);
			
		} else {
			main.getSql().connection();
		}
		
		p.getActivePotionEffects().clear();
	}
	
	@EventHandler
	public void onDataLoaded(dataLoadedEvent e) {
		main.getAccountManager().getAccount(e.getPlayer().getUniqueId().toString()).setLastUsername(e.getPlayer().getDisplayName());
		main.getTabRanksHandler().playerJoined(e.getPlayer());
		
		main.getStatsHandler().playerJoined(e.getPlayer());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e)
	{
		main.getSql().saveData(e.getPlayer());
		main.getTabRanksHandler().playerQuitted(e.getPlayer());
		
		main.getStatsHandler().playerQuitted(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		String UUID = player.getUniqueId().toString();
		Account account = CoreMain.getInstance().getAccountManager().getAccount(UUID);
		
		Rank r = account.getRank();
		if(e.getMessage().startsWith("&") && r.getPower() >= Rank.STAFF.getPower()) {
			e.setCancelled(true);
		}
		
		e.setFormat(main.getPlayerDisplayNameChat(player) + " §8» " + account.getNickedRank().getchatColor() + "%2$s");
	}
	
}
