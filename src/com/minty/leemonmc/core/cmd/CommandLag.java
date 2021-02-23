package com.minty.leemonmc.core.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_9_R2.MinecraftServer;

public class CommandLag implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args)
	{
		if(!(sender instanceof Player)) {
			sender.sendMessage("§cErreur: Cette commande peut seulement être exécutée par un joueur !");
			return false;
		}
		
		Player player = (Player) sender;
		
		player.sendMessage("§6§m------§e§m------§r §6§lPerformances§r §e§m------§6§m------");
		player.sendMessage("");
		player.sendMessage("§6➜ §eVotre ping : §6" + ((CraftPlayer) player).getHandle().ping + "ms §7§oTemps de réponse client<->serveur");
		double tps = Math.round(MinecraftServer.getServer().recentTps[0] * 100.0) / 100.0;
		player.sendMessage("§6➜ §eTPS : §6" + tps + " §7§oTicks par seconde du serveur");
		player.sendMessage("");
		player.sendMessage("§6§m------§e§m------§6§m------§e§m------§6§m------§e§m------");
		return false;
	}

}
