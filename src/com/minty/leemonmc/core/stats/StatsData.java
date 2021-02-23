package com.minty.leemonmc.core.stats;

import java.util.HashMap;
import java.util.Map;

public class StatsData {

	private Map<String, Integer> playerStats = new HashMap<>();
	private String owner;
	
	public StatsData(String _owner) {
		owner = _owner;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public void setStat(String name, int value)
	{
		if(getPlayerStats().containsKey(name)) {
			getPlayerStats().remove(name);
		}
		
		getPlayerStats().put(name, value);
	}
	
	public void removeStat(String name, int value)
	{
		if(!getPlayerStats().containsKey(name)) {
			getPlayerStats().put(name, 0);
		}
		
		int initial = getPlayerStats().get(name);
		initial -= value;
		if(initial < 0) {
			initial = 0;
		}
		getPlayerStats().remove(name);
		getPlayerStats().put(name, initial);
	}
	
	public void addStat(String name, int value)
	{
		if(!getPlayerStats().containsKey(name)) {
			getPlayerStats().put(name, 0);
		}
		
		int initial = getPlayerStats().get(name);
		initial += value;
		getPlayerStats().remove(name);
		getPlayerStats().put(name, initial);
	}
	
	public Integer getStat(String name)
	{
		if(!getPlayerStats().containsKey(name)) {
			getPlayerStats().put(name, 0);
		}
		return getPlayerStats().get(name);
	}
	
	public void setPlayerStats(Map<String, Integer> playerStats) {
		this.playerStats = playerStats;
	}
	
	public Map<String, Integer> getPlayerStats() {
		return playerStats;
	}
	
}
