package com.minty.leemonmc.core.gui.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.minty.leemonmc.basics.core.Rank;
import com.minty.leemonmc.core.CoreMain;
import com.minty.leemonmc.core.util.GuiBuilder;
import com.minty.leemonmc.core.util.GuiUtils;
public class NickConfirmMenu implements GuiBuilder {

	private GuiUtils utils;
	private CoreMain main = CoreMain.getInstance();

	private Map<Integer, Rank> ranksSlots = new HashMap<>();
	
	public NickConfirmMenu(GuiUtils _utils)
	{
		this.utils = _utils;
	}
	
	@Override
	public void contents(Player player, Inventory inv) {

		/* 
		 * CONTENT
		 * */
		
		List<String> infoLore = new ArrayList<>();
		infoLore.add("");
		infoLore.add("§7Une fois la §econfirmation effectuée §7sur le");
		infoLore.add("§egrade de votre choix §7vous prendez l'apparence");
		infoLore.add("§7d'un joueur §ede ce grade §7et un §epseudo aléatoire");
		infoLore.add("§7vous §esera donné §7!");
		infoLore.add("");
		infoLore.add("§4⚠ Le règlement s'applique également en /nick !");
		infoLore.add("§4⚠ Tout abus sera sanctionné !");
		infoLore.add("");
		
		inv.setItem(3 * 9 - 2, utils.createItem(Material.BOOK, "§6Comment ça marche ?", (byte) 0, infoLore));
		inv.setItem(3 * 9 - 1, utils.cancelItem());
		
		/* 
		 * RANKS
		 * */
		
		List<String> confirmLore = new ArrayList<>();
		confirmLore.add("");
		confirmLore.add("§6» §eCliquez pour choisir ce grade");
		
		inv.setItem(11, utils.createItem(Material.IRON_INGOT, "§7Grade Joueur", (byte) 0, confirmLore));
		ranksSlots.put(11, Rank.PLAYER);
		inv.setItem(12, utils.createItem(Material.IRON_INGOT, "§7Grade §aVIP", (byte) 0, confirmLore));
		ranksSlots.put(12, Rank.VIP);
		inv.setItem(13, utils.createItem(Material.GOLD_INGOT, "§7Grade §bVIP+", (byte) 0, confirmLore));
		ranksSlots.put(13, Rank.VIP_PLUS);
		inv.setItem(14, utils.createItem(Material.DIAMOND, "§7Grade §eLemon", (byte) 0, confirmLore));
		ranksSlots.put(14, Rank.LEMON);
	}
	
	@Override
	public int getSize()
	{
		return 3 * 9;
	}

	@Override
	public String name()
	{
		return "§6Changement de grade";
	}

	@Override
	public void onClick(Player player, Inventory inv, ItemStack it, int slot)
	{
		if(it == null) return;
		
		switch(it.getType())
		{
			case BARRIER:
				player.closeInventory();
				break;
			default:
				break;
		}
		
		if(!it.hasItemMeta()) return;
		
		if(ranksSlots.containsKey(slot))
		{
			main.getNickManager().nick(player, ranksSlots.get(slot));
			player.closeInventory();
		}
	}

	@Override
	public void onRightClick(Player arg0, Inventory arg1, ItemStack arg2, int arg3) {
		
		
	}

}
