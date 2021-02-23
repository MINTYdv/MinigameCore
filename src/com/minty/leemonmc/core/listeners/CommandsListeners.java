package com.minty.leemonmc.core.listeners;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.minty.leemonmc.core.CoreMain;

public class CommandsListeners implements Listener {

	private CoreMain main = CoreMain.getInstance();
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onCommand(PlayerCommandPreprocessEvent e)
	{
		Player player = e.getPlayer();
		if(player == null) return;
		String command = e.getMessage();
		
		List<String> bCmds = main.getConfig().getStringList("blocked-commands");

		if(command.equalsIgnoreCase("/tps"))
		{
			e.setCancelled(true);
			player.chat("/lag");
		}
		
		for(String blockedCommand : bCmds)
		{
			if(command.equalsIgnoreCase("/" + blockedCommand) || command.startsWith("/?"))
			{
				player.sendMessage("§cCette commande n'a pas pu être trouvée.");
				e.setCancelled(true);
				break;
			}
		}
	}
	
}
