package com.minty.leemonmc.core.ranks;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.minty.leemonmc.basics.core.Rank;
import com.minty.leemonmc.core.CoreMain;

public class CommmandRank implements CommandExecutor {

//	public CommmandRank(SqlConnection sql) {
//		this.sql = sql;
//	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		
		if(!(sender instanceof Player))
		{
			return false;
		}
		Player p = (Player) sender;
		String UUID = p.getUniqueId().toString();
		
		if(CoreMain.getInstance().getAccountManager().getAccount(UUID).getRank().getPower() >= Rank.DEVELOPER.getPower())
		{
			// Has permissions
			
			if(args.length == 0) {
				p.sendMessage("§cUtilisation: /rank <show/set/info> <joueur> [rank]");
				return false;
			}
			
			if(args.length >= 1)
			{
				System.out.println(args[0]);
				if(args[0].equalsIgnoreCase("info"))
				{
					String content = "";
					content += "§7Liste des powers des grades : \n ";
					for(Rank r : Rank.values())
					{
						content += "§8• " + r.getdisplayChatMen() + " §e: §6" + r.getPower() + "\n";
					}
					p.sendMessage(content);
				}
				if(args[0].equalsIgnoreCase("show"))
				{
					if(args.length == 1) {
						p.sendMessage("§7• Votre grade est : " + CoreMain.getInstance().getAccountManager().getAccount(UUID).getRank().getdisplayChatMen() + " §7!");
					} else {
						if(Bukkit.getPlayer(args[1]) != null)
						{
							p.sendMessage("§7• Le grade de §e" + args[1] + " §7est " + CoreMain.getInstance().getAccountManager().getAccount(UUID).getRank().getdisplayChatMen() + " §7!");
						} else
						{
							p.sendMessage("§cErreur: Ce joueur n'a pas pu être trouvé !");
						}
					}
				}
				if(args[0].equalsIgnoreCase("set")) {
					if(args.length == 1) {
						p.sendMessage("§cErreur: Merci de préciser un joueur !");
						return false;
					}
					
					if(args.length == 2) {
						p.sendMessage("§cErreur: Merci de préciser un grade à donner ! (power)");
						return false;
					}
					
					Player target = Bukkit.getPlayer(args[1]);
					String targetUUID = target.getUniqueId().toString();
					
					int powerArg = 404;
					
				    try {
				    	powerArg = Integer.parseInt(args[2]);
				    } catch (NumberFormatException e) {
				        p.sendMessage("§cErreur: Ceci n'est pas un nombre correct !");
				        return false;
				    }
					
					Rank rank = Rank.powerToRank(powerArg);
					
					if(rank == null) {
						p.sendMessage("§cErreur: Impossible de trouver un grade avec un power de " + args[2] + "§c !");
						return false;
					}
					
					if(CoreMain.getInstance().getAccountManager().getAccount(UUID).getRank().getPower() < CoreMain.getInstance().getAccountManager().getAccount(UUID).getRank().getPower()) {
						p.sendMessage("§cErreur: Vous ne pouvez pas modifier le grade de ce joueur !");
						return false;
					}
					
					if(rank.getPower() == CoreMain.getInstance().getAccountManager().getAccount(targetUUID).getRank().getPower()) {
						p.sendMessage("§cErreur: Le grade de " + target.getName() + " est déjà défini sur " + rank.getdisplayChatMen() + " §c!");
						return false;
					}
					
					CoreMain.getInstance().getAccountManager().getAccount(targetUUID).setRank(rank);
					p.sendMessage("§aVous avez bien défini le grade de " + target.getName() + " sur §f" + rank.getdisplayChatMen() + " §a!");
					return false;
					
				}
			}
			
		} else {
			p.sendMessage("§cErreur: Vous n'avez pas les permissions requises pour exécuter cette commande ! (>=DEVELOPER)");
		}
		
		return false;
	}

}
