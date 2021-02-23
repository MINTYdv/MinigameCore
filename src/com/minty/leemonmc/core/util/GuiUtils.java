package com.minty.leemonmc.core.util;

import java.util.ArrayList;
import java.util.List;

import com.minty.leemonmc.basics.core.cache.Account;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.minty.leemonmc.core.CoreMain;

public class GuiUtils {

	private GuiUtils instance;
	
	private final CoreMain main;
	private final String access = "§6» §eCliquez pour y accéder";
	private final String notImplementedLore = "§4» §c§oCette option n'est pas encore disponible !...";
	
	public GuiUtils(CoreMain _main) {
		instance = this;
		main = _main;
	}
	
	public GuiUtils getInstance() {
		return instance;
	}
	
	public ItemStack getSettingsItem()
	{
		List<String> optionsLore = new ArrayList<>();
		optionsLore.add("§7Accédez et §emodifiez §7les");
		optionsLore.add("§eoptions §7de votre §eprofil §7!");
		optionsLore.add("");
		optionsLore.add(getAccess());

		return createItem(Material.DIODE, "§6Options", (byte) 0, optionsLore);
	}
	
	public ItemStack getSoonItem() {
		return createItem(Material.BARRIER, "§c✖ Prochainement... ✖", (byte) 0);
	}
	
	public void notImplemented(Player player)
	{
		player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1f, 1f);
		player.sendMessage("§c§m---------------------------------------");
		player.sendMessage("");
		player.sendMessage("§4§l➜ §c§oCette fonctionnalité n'a pas encore été implémentée...");
		player.sendMessage("");
		player.sendMessage("§c§m---------------------------------------");
	}
	
	public ItemStack randomItem()
	{
		ItemStack it = main.getLeemonUtils().getSkull("https://textures.minecraft.net/texture/797955462e4e576664499ac4a1c572f6143f19ad2d6194776198f8d136fdb2");
		ItemMeta meta = it.getItemMeta();
		meta.setDisplayName("§cAléatoire");
		it.setItemMeta(meta);
		return it;
	}
	
	public ItemStack backItem()
	{
		List<String> backLore = new ArrayList<>();
		backLore.add("");
		backLore.add("§6» §eCliquez pour retourner en arrière");

		return createItem(Material.ARROW, "§6Retour", (byte) 0, backLore);
	}
	
	public ItemStack pane()
	{
		ItemStack it = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 4);
		ItemMeta meta = it.getItemMeta();
		
		meta.setDisplayName("§e(^ o ^)");
		it.setItemMeta(meta);
		return it;
	}
	
	public void fillWithItem(ItemStack item, int from, int to, Inventory inv)
	{
		for(int i = from; i < to; i++) {
			inv.setItem(i, item);
		}
	}
	
	public ItemStack createItem(Material mat, String name, byte data, List<String> lore)
	{
		ItemStack it = createItem(mat, name, data);
		ItemMeta meta = it.getItemMeta();
		meta.setLore(lore);
		it.setItemMeta(meta);
		return it;
	}
	
	public String getNotImplementedLore()
{
		return notImplementedLore;
	}

	public String getLol(){
		return "lol";
	}

	public ItemStack cancelItem()
	{
		ItemStack it = new ItemStack(Material.BARRIER, 1);
		ItemMeta meta = it.getItemMeta();
		
		meta.setDisplayName("§cFermer");
		List<String> lore = new ArrayList<>();
		
		lore.add("§6» §eCliquez ici pour fermer le menu.");
		
		meta.setLore(lore);
		
		it.setItemMeta(meta);
		return it;
	}
	
	public ItemStack getVipSettingsItem()
	{
		ItemStack it = new ItemStack(Material.BLAZE_POWDER, 1);
		ItemMeta meta = it.getItemMeta();
		
		meta.setDisplayName("§6Options VIP");
		
		List<String> lore = new ArrayList<>();
		lore.add("§7Clique ici pour accéder aux options de");
		lore.add("§7ton grade §eVIP §7(ou +) si tu en possèdes un !");
		lore.add("");
		lore.add(getAccess());
		
		meta.setLore(lore);
		
		it.setItemMeta(meta);
		return it;
	}
	
	public ItemStack getProfileItem(String UUID)
	{
		ItemStack it = getHead(CoreMain.getInstance().getAccountManager().UUIDtoUsername(UUID));
		String username = CoreMain.getInstance().getAccountManager().UUIDtoUsername(UUID);
		ItemMeta meta = it.getItemMeta();
		
		meta.setDisplayName("§6§n§l" + username);
		List<String> lore = new ArrayList<>();
		
		CoreMain main = CoreMain.getInstance();
		Account account = main.getAccountManager().getAccount(UUID);
		
		lore.add("§8» §7Rang : §e" + account.getPrefixAccordingToSettings());
		lore.add("§8» §7Compte : §e" + username);
		lore.add("");
		lore.add("§8» §7Pulpe : §e" + main.getLeemonUtils().formatNumber(account.getPulpes()));
		lore.add("§8» §7Citrons : §e" + main.getLeemonUtils().formatNumber(account.getLemons()));
		lore.add("");
		lore.add("§8» §7Première connexion : §e" + account.getFirstConnection());
		lore.add("");
		lore.add("§6» §eClique pour accéder à ton profil");
		
		meta.setLore(lore);
		
		it.setItemMeta(meta);
		return it;
	}
	
	public static ItemStack getHead(String owner) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setOwner(owner);
        item.setItemMeta(skull);
        return item;
    }
	
	@Deprecated
	public static ItemStack getHead(Player player) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setDisplayName(player.getName());
        new ArrayList<String>();
        skull.setOwner(player.getName());
        item.setItemMeta(skull);
        return item;
    }
	
	public ItemStack createItem(Material mat, String name, byte data)
	{
		ItemStack it = new ItemStack(mat, 1, data);
		ItemMeta meta = it.getItemMeta();

		meta.setDisplayName(name);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		
		it.setItemMeta(meta);
		return it;
	}
	
	public String getAccess() {
		return access;
	}
}
