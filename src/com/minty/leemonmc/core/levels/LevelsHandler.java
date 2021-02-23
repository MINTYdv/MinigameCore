package com.minty.leemonmc.core.levels;

import com.minty.leemonmc.core.CoreMain;

public class LevelsHandler {

	private static CoreMain main = CoreMain.getInstance();
	private final static int maxExpPerGame = 100;
	
	public static int getGlobalLevel(String UUID)
	{
		int level = 0;
		level += getSpecificLevel(UUID, "skyrush");
		level += getSpecificLevel(UUID, "underground");
		level += getSpecificLevel(UUID, "ffaskywars");
		level += getSpecificLevel(UUID, "gemsdefender");
		return level;
	}
	
	public static String getFormatedLevelIcon(int level)
	{
		//⋆ ★ ✯ ✹ ✲ ❇ ✥ ❉ 
		
		level /= 100;
		
		// CHANGEMENT D'ETOILE
		
		if(level <= 0)
		{
			return "§8⋆";
		}
		if(level < 10 && level >= 5)
		{
			return "§7⋆";
		}
		
		// CHANGEMENT D'ETOILE
		
		if(level < 15 && level >= 10)
		{
			return "§7★";
		}
		if(level < 20 && level >= 15)
		{
			return "§7★";
		}
		
		// CHANGEMENT D'ETOILE
		
		
		if(level < 25 && level >= 20)
		{
			return "§7✯";
		}
		if(level < 30 && level >= 25)
		{
			return "§7✯";
		}
		if(level < 35 && level >= 30)
		{
			return "§7✯";
		}
		if(level < 40 && level >= 35)
		{
			return "§7✯";
		}
		
		// CHANGEMENT D'ETOILE
		
		if(level < 45 && level >= 40)
		{
			return "§7✹";
		}
		if(level < 50 && level >= 45)
		{
			return "§7✹";
		}
		
		// CHANGEMENT D'ETOILE
		
		if(level < 55 && level >= 50)
		{
			return "§7✲";
		}
		if(level < 60 && level >= 55)
		{
			return "§7✲";
		}
		
		// CHANGEMENT D'ETOILE
		
		if(level < 65 && level >= 60)
		{
			return "§7❇";
		}
		if(level < 70 && level >= 65)
		{
			return "§7❇";
		}
		if(level < 75 && level >= 70)
		{
			return "§7❇";
		}
		if(level < 80 && level >= 75)
		{
			return "§7❇";
		}
		
		// CHANGEMENT D'ETOILE
		
		if(level < 85 && level >= 80)
		{
			return "§7✥";
		}
		
		if(level < 90 && level >= 85)
		{
			return "§c✥";
		}
		if(level < 95 && level >= 90)
		{
			return "§c§l✥";
		}
		
		
		// CHANGEMENT D'ETOILE
		
		
		return "§4§l❉";

		
	}
	
	public static int getSpecificLevel(String UUID, String minigame)
	{
		return main.getStatsHandler().getStat(UUID, minigame, "level");
	}
	
	public static int getSpecificExp(String UUID, String minigame)
	{
		return main.getStatsHandler().getStat(UUID, minigame, "exp");
	}
	
	public static int getMaxexppergame() {
		return maxExpPerGame;
	}
	
}
