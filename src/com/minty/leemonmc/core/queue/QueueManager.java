package com.minty.leemonmc.core.queue;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.minty.leemonmc.basics.core.GameState;
import com.minty.leemonmc.basics.core.Server;
import com.minty.leemonmc.basics.core.ServerGroup;
import com.minty.leemonmc.basics.core.ServerType;
import com.minty.leemonmc.basics.core.cache.Account;
import com.minty.leemonmc.basics.core.queue.QueuePriority;
import com.minty.leemonmc.core.CoreMain;
import com.minty.leemonmc.core.util.Title;
import com.minty.leemonmc.social.core.PartyHandler;

public class QueueManager implements Listener {

	private CoreMain main;
	private Map<ServerGroup, List<Player>> serversQueues = new HashMap<>();
	
	private Map<Player, Long> playersCooldowns = new HashMap<>();
	
	public QueueManager(CoreMain _main)
	{
		main = _main;
	}
	
	public void setup()
	{
		for(ServerGroup group : ServerGroup.values())
		{
			List<Player> value = new ArrayList<>();
			
			System.out.println("Adding " + group.toString());
			getServersQueues().put(group, value);
		}
		
		/* 
		 * Ultra high
		 * */
        new BukkitRunnable() {
        	@Override
        	public void run()
        	{
        		tick(QueuePriority.ULTRA_HIGH);
        	}
        }.runTaskTimer(main, 0, 20 * QueuePriority.ULTRA_HIGH.getWaitTime());
        
		/* 
		 * high
		 * */
        new BukkitRunnable() {
        	@Override
        	public void run()
        	{
        		tick(QueuePriority.HIGH);
        	}
        }.runTaskTimer(main, 0, 20 * QueuePriority.HIGH.getWaitTime());
        
		/* 
		 * Elevee
		 * */
        new BukkitRunnable() {
        	@Override
        	public void run()
        	{
        		tick(QueuePriority.ELEVEE);
        	}
        }.runTaskTimer(main, 0, 20 * QueuePriority.ELEVEE.getWaitTime());
        
		/* 
		 * Normal
		 * */
        new BukkitRunnable() {
        	@Override
        	public void run()
        	{
        		tick(QueuePriority.NORMAL);
        	}
        }.runTaskTimer(main, 0, 20 * QueuePriority.NORMAL.getWaitTime());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e)
	{
		Player player = e.getPlayer();
		if(isPlayerInQueue(player)) {
			removePlayerFromQueue(player);
		}
	}
	
	public void queue(Player player, ServerGroup group)
	{
		String UUID = player.getUniqueId().toString();
		Account account = main.getAccountManager().getAccount(UUID);
		
		if(!getPlayersCooldowns().containsKey(player))
		{
			getPlayersCooldowns().put(player, System.currentTimeMillis() - 3500);
		}
		
		long elapsed = System.currentTimeMillis() - getPlayersCooldowns().get(player);
		if(elapsed < 3500)
		{
			player.sendMessage("§6§lLeemonMC §f» §cPatientez avant de rejoindre une autre file d'attente !");
			return;
		}
		
		if(isPlayerInQueue(player))
		{
			if(getPlayerQueuingFor(player) == group)
			{
				player.sendMessage("§6§lLeemonMC §f» §7Vous êtes déjà en file d'attente pour ce mode de jeu !");
				return;
			}
			
			removePlayerFromQueue(player);
		}
		
		new BukkitRunnable() {
			
			@Override
			public void run()
			{
				if(account.getSetting("global_gender").equalsIgnoreCase("FEMALE"))
				{
					player.sendMessage("§6§lLeemonMC §f» §7Vous avez été ajoutée à la file d'attente de §e" + group.toString() + "§7...");
				} else
				{
					player.sendMessage("§6§lLeemonMC §f» §7Vous avez été ajouté à la file d'attente de §e" + group.toString() + "§7...");
				}
				
				int estimated = (getServersQueues().get(group).size() * account.getRank().getPriority().getWaitTime()) + 1;
				player.sendMessage("§6§lLeemonMC §f» §7Vous passerez en priorité §e" + account.getRank().getPriority().getName() + " §7 ! (Temps d'attente estimé de §6" + estimated + " secondes§7)");
				
				System.out.println("Player " + player.getName() + " is now queing for " + group.toString());
				getServersQueues().get(group).add(player);
				
				getPlayersCooldowns().remove(player);
				getPlayersCooldowns().put(player, System.currentTimeMillis());
			}
		}.runTaskLater(main, 10);
		
	}
	
	public void connect(Player player, Server server)
	{
		player.sendMessage("§6§lLeemonMC §f» §7Connexion à §e" + server.getName() + " §7en cours...");
		
		if(main.getServerManager().getServer().getName() == server.getName())
		{
			player.sendMessage("§6§lLeemonMC §f» §cVous êtes déjà connecté sur ce serveur !");
			return;
		}
		
		removePlayerFromQueue(player);
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		
		try {
			out.writeUTF("Connect");
			out.writeUTF(server.getName());
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		player.sendPluginMessage(main, "BungeeCord", b.toByteArray());
	}
	
	private void tick(QueuePriority priority)
	{
		for(Entry<ServerGroup, List<Player>> entry : getServersQueues().entrySet())
		{
			ServerGroup group = entry.getKey();
			List<Player> queudedPlayers = entry.getValue();
			
			for(int i = 0; i < queudedPlayers.size(); i++)
			{
				Player pl = queudedPlayers.get(i);
				
				Title title = new Title();
				title.sendActionBar(pl, "§eFile d'attente: §6" + group.toString() + " §7- §ePosition: §6" + (i + 1) + "/" + queudedPlayers.size());
			}
			
			if(queudedPlayers.size() < 1) continue;
			
			Player player = queudedPlayers.get(0);
			
			String UUID = player.getUniqueId().toString();
			Account account = main.getAccountManager().getAccount(UUID);
			if(account.getRank().getPriority() != priority)
			{
				continue;
			}
			
			if(getAvailableServers(player, group) == null || getAvailableServers(player, group).size() == 0)
			{
				continue;
			}
			
			if(isPlayerInQueue(player))
			{
				Server target = getAvailableServers(player, group).get(0);
				connect(player, target);
				removePlayerFromQueue(player);
			}
		}
	}

	public List<Server> getAvailableServers(Player player, ServerGroup group)
	{
		List<Server> available = new ArrayList<>();	
		for(Server server : main.getServerManager().getServersOfGroup(group))
		{
			if(server.getGameState() == GameState.WAITING)
			{
				int playersAmount = 1;
				if(PartyHandler.ownsParty(player.getUniqueId().toString()))
				{
					playersAmount = PartyHandler.getParty(player.getUniqueId().toString()).getPlayers().size();
				}
				if(server.getPlayingPlayers() < server.getMaxPlayers())
				{
					if(!main.getServerManager().getServer().getName().equalsIgnoreCase(server.getName()))
					{
						available.add(server);
					}
				}
			}
		}
		return available;
	}
	
	public void removePlayerFromQueue(Player player)
	{
		for(Entry<ServerGroup, List<Player>> entry : getServersQueues().entrySet())
		{
			List<Player> queudedPlayers = entry.getValue();
			
			if(queudedPlayers.contains(player))
			{
				getServersQueues().get(entry.getKey()).remove(player);
			}
		}
	}
	
	public ServerGroup getPlayerQueuingFor(Player player)
	{
		for(Entry<ServerGroup, List<Player>> entry : getServersQueues().entrySet())
		{
			ServerGroup group = entry.getKey();
			List<Player> queudedPlayers = entry.getValue();
			
			if(queudedPlayers.contains(player))
			{
				return group;
			}
		}
		return null;
	}
	
	public boolean isPlayerInQueue(Player player)
	{
		for(Entry<ServerGroup, List<Player>> entry : getServersQueues().entrySet())
		{
			List<Player> queudedPlayers = entry.getValue();
			
			if(queudedPlayers.contains(player))
			{
				return true;
			}
		}
		return false;
	}
	
	public Map<ServerGroup, List<Player>> getServersQueues() {
		return serversQueues;
	}

	public Map<Player, Long> getPlayersCooldowns() {
		return playersCooldowns;
	}
	
}
