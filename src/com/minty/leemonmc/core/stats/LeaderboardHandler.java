package com.minty.leemonmc.core.stats;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.minty.leemonmc.core.CoreMain;

public class LeaderboardHandler {

	private static CoreMain main = CoreMain.getInstance();
	
	/** 
	 * Function used to get the leaderboard position of a specific statistic
	 * for a given player.
	 * 
	 * @param The UUID of the player
	 * @param The name of the minigame/table to look inside
	 * @param The name of the statistic to rank
	 * */
	public static int getLeaderboardPosition(String UUID, String minigame, String stat)
	{
		for(int i = 0; i < getLeaderboard(minigame, stat).entrySet().size(); i++)
		{
			@SuppressWarnings("unchecked")
			Entry<String, Integer> entry = (Entry<String, Integer>) getLeaderboard(minigame, stat).entrySet().toArray()[i];
			
			if(entry.getKey().equalsIgnoreCase(UUID))
			{
				return i + 1;
			}
		}
		
		return getLeaderboard(minigame, stat).size();
	}
	
	public static Map<String, Integer> getLeaderboard(String minigame, String stat)
	{
		Map<String, Integer> initial = new HashMap<>();
		Map<String, Integer> sorted = new HashMap<>();
		
		for(String UUID : main.getStatsHandler().getAllRegisteredUUIDs(minigame))
		{
			int statAmount = main.getStatsHandler().getStat(UUID, minigame, stat);
			initial.put(UUID, statAmount);
		}
		
		sorted = initial
	            .entrySet()
	            .stream()
	            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
	            .collect(
	            	java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
	                    LinkedHashMap::new));
		
		return sorted;
	}
	
}
