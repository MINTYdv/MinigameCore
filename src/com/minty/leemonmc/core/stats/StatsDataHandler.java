package com.minty.leemonmc.core.stats;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

public class StatsDataHandler {

	private static Map<Player, StatsData> playersStatsData = new HashMap<>();
	
	public static StatsData getPlayerStats(Player player)
	{
		if(!getPlayersStatsData().containsKey(player))
		{
			getPlayersStatsData().put(player, new StatsData(player.getUniqueId().toString()));
		}
		return getPlayersStatsData().get(player);
	}
	
	public static void createPlayerData(Player player, StatsData data)
	{
		if(getPlayersStatsData().containsKey(player)) {
			getPlayersStatsData().remove(player);
		}
		getPlayersStatsData().put(player, data);
	}
	
	public static Map<Player, StatsData> getPlayersStatsData() {
		return playersStatsData;
	}
	
}
