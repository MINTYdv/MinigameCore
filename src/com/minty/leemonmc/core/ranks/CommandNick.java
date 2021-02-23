package com.minty.leemonmc.core.ranks;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.minty.leemonmc.basics.core.Rank;
import com.minty.leemonmc.basics.core.ServerType;
import com.minty.leemonmc.basics.core.cache.Account;
import com.minty.leemonmc.core.CoreMain;
import com.minty.leemonmc.core.gui.core.NickConfirmMenu;

public class CommandNick implements CommandExecutor {

	CoreMain main = CoreMain.getInstance();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args)
	{
		if(!(sender instanceof Player)){
			sender.sendMessage("§cErreur: Cette commande peut uniquement être exécutée par un joueur !");
			return false;
		}
		
		Player player = (Player) sender;
		String UUID = player.getUniqueId().toString();
		Account account = main.getAccountManager().getAccount(UUID);
		
		if(account.getRank().getPower() < Rank.CUSTOM.getPower()) {
			player.sendMessage("§c§m---------------------------------------");
			player.sendMessage("");
			player.sendMessage("§f§l➜  §eTu n'as pas le grade nécessaire pour");
			player.sendMessage("      §emodifier cette option ! §7(Grade personnalisé)");
			player.sendMessage("");
			player.sendMessage("§f§l➜  §eUn meilleur grade ?");
			player.sendMessage("      §6https://store.leemonmc.fr");
			player.sendMessage("");
			player.sendMessage("§c§m---------------------------------------");
			return false;
		}
		
		if(main.getServerManager().getServer().getServerType() != ServerType.LOBBY) {
			player.sendMessage("§6§lLeemonMC §f» §cCette commande peut uniquement s'exécuter dans un lobby !");
			return false;
		}
		
		if(account.isNicked())
		{
			main.getNickManager().unNick(player);
			player.sendMessage("§6§lLeemonMC §f» §7Vous venez de §cdésactiver §7le mode §c/nick §7! Pour que votre pseudo normal vous soit rendu, vous devez §6changer de serveur §7! (lobby, mini-jeu,...");
			return false;
		}
		
		player.sendMessage("§6§lLeemonMC §f» §7Ouverture du menu de confirmation...");
		main.getGuiManager().open(player, NickConfirmMenu.class);
		
		return false;
	}

	
	
}
