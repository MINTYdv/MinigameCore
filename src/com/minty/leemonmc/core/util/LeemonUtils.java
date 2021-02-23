package com.minty.leemonmc.core.util;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

import com.minty.leemonmc.core.CoreMain;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.minecraft.server.v1_9_R2.NBTTagCompound;
import net.minecraft.server.v1_9_R2.NBTTagList;

public class LeemonUtils {

	@SuppressWarnings("unused")
	private CoreMain main;
	
	public LeemonUtils(CoreMain main) {
		this.main = main;
	}
	
	private char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
	
	@SuppressWarnings("serial")
	public static final Map<String, Color> COLOURS = new HashMap<String, Color>() {{
	    put("RED", Color.RED);
	    put("GREEN", Color.GREEN);
	    put("BLUE", Color.BLUE);
	    put("YELLOW", Color.YELLOW);
	    put("PINK", Color.FUCHSIA);
	    put("AQUA", Color.AQUA);
	    put("BLACK", Color.BLACK);
	    put("PURPLE", Color.PURPLE);
	    put("GRAY", Color.GRAY);
	    put("MAROON", Color.MAROON);
	    put("LIGHT_GREEN", Color.LIME);
	    put("ORANGE", Color.ORANGE);
	    put("SILVER", Color.SILVER);
	    put("WHITE", Color.WHITE);
	}};
	
    public String enchantmentName(Enchantment ench) {
        switch (ench.getName().toUpperCase()) {
	        case "ARROW_DAMAGE":
	            return "Power";
	        case "ARROW_FIRE":
	            return "Flame";
	        case "ARROW_INFINITE":
	            return "Infinity";
	        case "ARROW_KNOCKBACK":
	            return "Punch";
	        case "BINDING_CURSE":
	            return "Curse of Binding";
	        case "DAMAGE_ALL":
	            return "Tranchant";
	        case "DAMAGE_ARTHROPODS":
	            return "Bane of Arthropods";
	        case "DAMAGE_UNDEAD":
	            return "Smite";
	        case "DEPTH_STRIDER":
	            return "Depth Strider";
	        case "DIG_SPEED":
	            return "Efficacité";
	        case "DURABILITY":
	            return "Solidité";
	        case "FIRE_ASPECT":
	            return "Aura de feu";
	        case "FROST_WALKER":
	            return "Frost Walker";
	        case "KNOCKBACK":
	            return "Knockback";
	        case "LOOT_BONUS_BLOCKS":
	            return "Fortune";
	        case "LOOT_BONUS_MOBS":
	            return "Looting";
	        case "LUCK":
	            return "Luck of the Sea";
	        case "LURE":
	            return "Lure";
	        case "MENDING":
	            return "Mending";
	        case "OXYGEN":
	            return "Respiration";
	        case "PROTECTION_ENVIRONMENTAL":
	            return "Protection";
	        case "PROTECTION_EXPLOSIONS":
	            return "Blast Protection";
	        case "PROTECTION_FALL":
	            return "Feather Falling";
	        case "PROTECTION_FIRE":
	            return "Fire Protection";
	        case "PROTECTION_PROJECTILE":
	            return "Projectile Protection";
	        case "SILK_TOUCH":
	            return "Silk Touch";
	        case "SWEEPING_EDGE":
	            return "Sweeping Edge";
	        case "THORNS":
	            return "Thorns";
	        case "VANISHING_CURSE":
	            return "Cure of Vanishing";
	        case "WATER_WORKER":
	            return "Aqua Affinity";
	        default:
	            return "Unknown";
	        }
    }
	
	public ItemStack getLobbyItem()
	{
		ItemStack it = main.getGuiUtils().createItem(Material.BED, "§cRetour au lobby §7§o(Clic droit)", (byte) 0);
		return it;
	}
	
	public Color stringToColor(String string)
	{
		return COLOURS.get(string);
	}
	
	public String formatNumber(int n)
	{
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.FRANCE);
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

		symbols.setGroupingSeparator(' ');
		formatter.setDecimalFormatSymbols(symbols);
		String result = formatter.format(n);
		
		return result;
	}
	
	public char[] getAlphabet() {
		return alphabet;
	}
	
	public void removeAllEffects(Player player) {
		for(PotionEffect effect : player.getActivePotionEffects())
		{
			player.removePotionEffect(effect.getType());
		}
	}
	
	public ItemStack addGlow(ItemStack item){ 
		  net.minecraft.server.v1_9_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
		  NBTTagCompound tag = null;
		  if (!nmsStack.hasTag()) {
		      tag = new NBTTagCompound();
		      nmsStack.setTag(tag);
		  }
		  if (tag == null) tag = nmsStack.getTag();
		  NBTTagList ench = new NBTTagList();
		  tag.set("ench", ench);
		  nmsStack.setTag(tag);
		  return CraftItemStack.asCraftMirror(nmsStack);
		}
	
    public ItemStack getSkull(String url) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
        if(url.isEmpty())return head;
     
     
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try
        {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        }
        catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }
	
}
