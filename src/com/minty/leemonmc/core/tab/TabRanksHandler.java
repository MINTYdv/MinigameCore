package com.minty.leemonmc.core.tab;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import com.minty.leemonmc.basics.core.Rank;
import com.minty.leemonmc.basics.core.ServerType;
import com.minty.leemonmc.basics.core.cache.Account;
import com.minty.leemonmc.core.CoreMain;
import com.minty.leemonmc.core.ranks.PlayersIDHandler;

public class TabRanksHandler {

	private CoreMain main = CoreMain.getInstance();
	
	private Scoreboard board;
	
	public void setup()
	{
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		board = manager.getMainScoreboard();
	}
	
	public void playerJoined(Player joinedPlayer)
	{
		new BukkitRunnable() {
			
			@Override
			public void run()
			{
				for(Player players : Bukkit.getOnlinePlayers())
				{
					Scoreboard bScoreboard = players.getScoreboard();
					
					if(bScoreboard.getTeams().size() > 0) for(Team team : bScoreboard.getTeams())
					{
						team.unregister();
					}
					
					for(Player player : Bukkit.getOnlinePlayers())
					{

						String UUID = player.getUniqueId().toString();
						Account account = main.getAccountManager().getAccount(UUID);
						Rank rank = account.getNickedRank();
						
						String teamName = rankToChar(rank) + "" + PlayersIDHandler.getPlayerID(player);
						
						String prefix = account.getPrefixAccordingToSettings() + " ";
						
						Team playerTeam = bScoreboard.registerNewTeam(teamName);
						if(main.getServerManager().getServerType() != ServerType.MINIGAME) {
							playerTeam.setPrefix(prefix);
							playerTeam.setAllowFriendlyFire(false);
						}
						playerTeam.setOption(Option.COLLISION_RULE, OptionStatus.NEVER);
						
						playerTeam.addEntry(player.getName());

					}
					players.setScoreboard(bScoreboard);
				}
			}
		}.runTaskLater(main, 20L);
		
	}
	
	public void playerQuitted(Player player)
	{
		if(main.getServerManager().getServerType() == ServerType.MINIGAME) return;
		
		String UUID = player.getUniqueId().toString();
	}
	
	private char rankToChar(Rank target)
	{
		List<Rank> ranksArray = new ArrayList<>();
		
		for(Rank r : Rank.values()) {
			ranksArray.add(r);
		}
		
		for(int i = 0; i < ranksArray.size(); i++)
			{
				Rank r = ranksArray.get(i);

				char[] alpha = main.getLeemonUtils().getAlphabet();

				if(r == target) {
					return alpha[i];
				}

			}
		return 0;
	}

	
}
