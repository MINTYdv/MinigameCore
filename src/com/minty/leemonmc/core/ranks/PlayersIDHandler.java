package com.minty.leemonmc.core.ranks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.entity.Player;

public class PlayersIDHandler {

	private static Map<Player, Long> playersIDs = new HashMap<>();
	
	public static long getPlayerID(Player player)
	{
		if(getPlayersIDs().containsKey(player)) {
			return getPlayersIDs().get(player);
		}
		
		Random random = new Random();
		long id = random.nextInt(1000000);
		while(getUsedIdentifiers().contains(id)) {
			id = random.nextInt(1000000);
		}
		getPlayersIDs().put(player, id);
		return id;
	}
	
	private static List<Long> getUsedIdentifiers()
	{
		List<Long> result = new ArrayList<>();
		for(long entry : getPlayersIDs().values())
		{
			result.add(entry);
		}
		return result;
	}
	
	public static Map<Player, Long> getPlayersIDs() {
		return playersIDs;
	}
	
}
