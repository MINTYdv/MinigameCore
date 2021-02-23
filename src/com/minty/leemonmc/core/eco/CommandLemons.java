package com.minty.leemonmc.core.eco;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.minty.leemonmc.basics.core.Rank;
import com.minty.leemonmc.basics.core.cache.Account;
import com.minty.leemonmc.core.CoreMain;
import com.minty.leemonmc.core.SqlConnection;
import com.minty.leemonmc.core.cache.AccountManager;

public class CommandLemons implements CommandExecutor {
	private CoreMain main;
	
	public CommandLemons(CoreMain _main) {
		this.main = _main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		
		if(sender instanceof Player)
		{
			Player p = (Player)sender;
			String UUID = p.getUniqueId().toString();
			
			if(args.length == 0) {
				int balancePulpe = CoreMain.getInstance().getAccountManager().getAccount(UUID).getPulpes();
				int balanceLemons = CoreMain.getInstance().getAccountManager().getAccount(UUID).getLemons();
				p.sendMessage("§7Vous avez actuellement §e" + main.getLeemonUtils().formatNumber(balancePulpe) + " pulpe §7et §6" + main.getLeemonUtils().formatNumber(balanceLemons) + " citrons§7.");
			}
			
			String utilisation = "§cUtilisation: /money <pulpe/lemons> <add/remove> <montant> <joueur>";
			
			if(args.length >= 1) {
				
				// PULPE
				
				if(CoreMain.getInstance().getAccountManager().getAccount(UUID).getRank().getPower() < Rank.DEVELOPER.getPower()) {
					p.sendMessage("§cErreur: Vous n'avez pas les permissions requises pour exécuter cette commande ! (>=DEVELOPER)");
					return false;
				}
				
				if(args.length != 4) {
					p.sendMessage(utilisation);
				} else {
					if(args[0].equalsIgnoreCase("pulpe"))
					{
						if(args[1].equalsIgnoreCase("add"))
						{
							if(args.length != 4) {
								p.sendMessage(utilisation);
							} else {
								Player target = Bukkit.getPlayer(args[3]);
								String targetUUID = target.getUniqueId().toString();
								Account targetAccount = CoreMain.getInstance().getAccountManager().getAccount(targetUUID);
								
								if(target != null) {
									int montant = 0;
									
									try {
										montant = Integer.valueOf(args[2]);
									} catch(NumberFormatException e) {
										p.sendMessage("§cErreur: Merci de préciser un nombre correct !");
										return false;
									}
									
									targetAccount.addPulpe(montant);
									p.sendMessage("§7Vous avez ajouté §e" + main.getLeemonUtils().formatNumber(montant) + " pulpe §7à " + target.getDisplayName() + " !");
								}
							}
						}
						
						if(args[1].equalsIgnoreCase("remove"))
						{
							if(args.length != 4) {
								p.sendMessage(utilisation);
								return false;
							} else {
								
								Player target = Bukkit.getPlayer(args[3]);
								String targetUUID = target.getUniqueId().toString();
								Account targetAccount = CoreMain.getInstance().getAccountManager().getAccount(targetUUID);
								
								if(target != null) {
									
									int montant = Integer.valueOf(args[2]);
									targetAccount.removePulpe(montant);
									p.sendMessage("§7Vous avez retiré §e" + main.getLeemonUtils().formatNumber(montant) + " pulpe §7à " + target.getDisplayName() + " !");
								}
								
							}
						}
					}
					
					// LEMONS
					
					if(args[0].equalsIgnoreCase("lemons"))
					{
						// /money lemons add 50 minty
						
						if(args[1].equalsIgnoreCase("add"))
						{
							if(args.length != 4) {
								p.sendMessage(utilisation);
								return false;
							} else {
								
								Player target = Bukkit.getPlayer(args[3]);

								if(target != null) {
									
									String targetUUID = target.getUniqueId().toString();
									Account targetAccount = CoreMain.getInstance().getAccountManager().getAccount(targetUUID);

									int montant = Integer.valueOf(args[2]);
									targetAccount.addLemons(montant);
									p.sendMessage("§7Vous avez ajouté §6" + main.getLeemonUtils().formatNumber(montant) + " citrons §7à " + target.getDisplayName() + " !");
								}
							}
						}
						
						if(args[1].equalsIgnoreCase("remove"))
						{
							if(args.length != 4) {
								p.sendMessage(utilisation);
								return false;
							} else {
								
								Player target = Bukkit.getPlayer(args[3]);
								String targetUUID = target.getUniqueId().toString();
								Account targetAccount = CoreMain.getInstance().getAccountManager().getAccount(targetUUID);
								
								if(target != null) {
									
									int montant = Integer.valueOf(args[2]);
									targetAccount.removeLemons(montant);
									p.sendMessage("§7Vous avez retiré §6" + main.getLeemonUtils().formatNumber(montant) + " citrons §7à " + target.getDisplayName() + " !");
								}
								
							}
						}
					}
				}
				
				
			}
		}
		
		return false;
	}

}
