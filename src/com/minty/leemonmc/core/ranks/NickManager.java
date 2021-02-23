package com.minty.leemonmc.core.ranks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.minty.leemonmc.basics.core.Rank;
import com.minty.leemonmc.basics.core.cache.Account;
import com.minty.leemonmc.core.CoreMain;
import com.minty.leemonmc.core.events.dataLoadedEvent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UUIDTypeAdapter;

import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_9_R2.PacketPlayOutRespawn;

public class NickManager implements Listener {

	private CoreMain main = CoreMain.getInstance();
	private List<String> usernames = new ArrayList<>();
	
	public void setup()
	{
		usernames = main.getConfig().getStringList("nicknames");
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onDataLoaded(dataLoadedEvent e)
	{
		Player player = e.getPlayer();
		String UUID = player.getUniqueId().toString();
		Account account = main.getAccountManager().getAccount(UUID);
		
		if(account.getNickedRank() != account.getRank() && !account.isNicked())
		{
			account.setNickRank(account.getRank());
		}

		if(account.getNickedName() == null || account.getNickedName().isEmpty())
		{
			account.setNickName(player.getName());
		}
		
		if(account.isNicked())
		{
			player.setPlayerListName(account.getPrefixAccordingToSettings() + " " + account.getNickedName());
			player.setCustomName(account.getNickedName());
			
			for(Player pls : Bukkit.getOnlinePlayers())
			{
				pls.hidePlayer(player);
				pls.showPlayer(player);
			}
		}

	}
	
	public String getUUIDofUnknownPlayer(String name)
	{
        String url = "https://api.mojang.com/users/profiles/minecraft/"+name;
        try {
            String UUIDJson = IOUtils.toString(new URL(url));           
            if(UUIDJson.isEmpty()) return "invalid name";                       
            JSONObject UUIDObject = (JSONObject) JSONValue.parseWithException(UUIDJson);
            String uuid = UUIDObject.get("id").toString();
            Bukkit.broadcastMessage("UUID of " + name + " is " + uuid);
            return uuid;
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        return "error";
	}
	
	public static boolean setSkin(GameProfile profile, UUID uuid) {
	    try {
	        HttpsURLConnection connection = (HttpsURLConnection) new URL(String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false", UUIDTypeAdapter.fromUUID(uuid))).openConnection();
	        if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
	            String reply = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
	            String skin = reply.split("\"value\":\"")[0].split("\"")[0];
	            String signature = reply.split("\"signature\":\"")[0].split("\"")[0];
	            profile.getProperties().put("textures", new Property("textures", skin, signature));
	            return true;
	        } else {
	            System.out.println("Connection could not be opened (Response code " + connection.getResponseCode() + ", " + connection.getResponseMessage() + ")");
	            return false;
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public void unNick(Player player)
	{
		String UUID = player.getUniqueId().toString();
		Account account = main.getAccountManager().getAccount(UUID);
		if(!account.isNicked()) return;
		
		account.setNickName(account.getLastUsername());
		account.setNickRank(account.getRank());
	}
	
	public List<String> getAllUsedNicknames()
	{
		List<String> allNames = new ArrayList<>();
		try
		{
			Connection connection = main.getSql().getConnection();
			PreparedStatement rs = connection.prepareStatement("SELECT * FROM data_core");
			
			ResultSet results = rs.executeQuery();
			
			String nickName = "ERROR";
			
			while(results.next()) {
				nickName = results.getString("nick_name");
				
				if(nickName != null && !nickName.isEmpty()) {
					allNames.add(nickName);
				}
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return allNames;
	}
	
	private String getRandomAvailableNickName()
	{
        Random rand = new Random();
        String rndName = usernames.get(rand.nextInt(usernames.size()));
        if(getAllUsedNicknames().contains(rndName)) {
        	return getRandomAvailableNickName();
        }
        return rndName; 
	}
	
	public void nick(Player player, Rank rank)
	{
		String UUID = player.getUniqueId().toString();
		Account account = main.getAccountManager().getAccount(UUID);
		if(account.isNicked()) return;
		
		account.setNickName(getRandomAvailableNickName());
		account.setNickRank(rank);
		player.sendMessage("§6§lLeemonMC §f» §7Votre nouveau pseudo est : " + account.getPrefixAccordingToSettings() + " " + account.getNickedName());
	}
	
}
