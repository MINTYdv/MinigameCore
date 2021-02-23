package com.minty.leemonmc.core;

import com.minty.leemonmc.basics.core.Rank;
import com.minty.leemonmc.basics.core.cache.Account;
import com.minty.leemonmc.core.events.dataLoadedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SqlConnection {

	private Connection connection;
	private String host,database,user,pass;
	
	public SqlConnection(String host, String database, String user, String pass, CoreMain main)
	{
		this.host = host;
		this.database = database;
		this.user = user;
		this.pass = pass;
	}

	public void connection()
	{
		if(!isConnected()) {	
			try {
				connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database + "?autoReconnect=true", user, pass);
				
				System.out.println("Successfully connected to the database !");
			} catch (SQLException e) {
				e.printStackTrace();
				Bukkit.getServer().reload();
			}
		}
	}
	
	public void disconnect()
	{
		if(isConnected()) {
			try {
				connection.close();
				System.out.println("Successfully disconnected from the database !");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void loadData(Player player)
	{
		if(!hasDatabaseAccount(player))
		{
			createDatabaseAccount(player);
		}
		
		convertToRedis(player);
	}

	public void createDatabaseAccount(Player player)
	{
		/* 
		 * A NE PAS APPELER SANS VERIFICATION AVANT
		 * */
		
		String uuid = player.getUniqueId().toString();
		
		try {
			PreparedStatement q = connection.prepareStatement("INSERT INTO data_core(uuid,pulpe,lemons,rank,modenabled,username,firstlog,nick_rank,nick_name) VALUES (?,?,?,?,?,?,?,?,?)");
			
			q.setString(1, player.getUniqueId().toString());
			q.setInt(2, 0);
			q.setInt(3, 0);
			q.setInt(4, Rank.PLAYER.getPower());
			q.setBoolean(5, false);
			q.setString(6, player.getName());
			Date now = new Date();
			String pattern = "dd-MM-yyyy";
			SimpleDateFormat formatter = new SimpleDateFormat(pattern);
			q.setString(7, formatter.format(now));
			q.setInt(8, Rank.PLAYER.getPower());
			q.setString(9, player.getName());
			
			q.execute();
			q.close();
			
			// INSERT SETTINGS
			PreparedStatement qq = connection.prepareStatement("INSERT INTO data_settings(uuid,hub_movement,group_requests,friends_requests,msg_allow,join_notifs,host_notifs,vip_symbol,vip_prefix,global_gender,join_effect,join_message) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
			
			qq.setString(1, uuid);
			qq.setString(2, "WALK");
			qq.setString(3, "ENABLED");
			qq.setString(4, "ENABLED");
			qq.setString(5, "ENABLED");
			qq.setString(6, "NULL");
			qq.setString(7, "NULL");
			qq.setString(8, "NULL");
			qq.setString(9, "&dCustom");
			qq.setString(10, "MALE");
			qq.setString(11, "NOTHING");
			qq.setString(12, "&6&oa rejoint le hub !");
			
			qq.execute();
			qq.close();
			
			getNewDefaultRedisAccount(player);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean hasDatabaseAccount(Player player)
	{
		try {
			PreparedStatement q = connection.prepareStatement("SELECT uuid FROM data_core WHERE uuid = ?");
			q.setString(1, player.getUniqueId().toString());
			ResultSet result = q.executeQuery();
			
			boolean hasAccount = result.next();
			q.close();
			
			return hasAccount;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	@SuppressWarnings("deprecation")
	public Account getOfflineAccount(String name)
	{
		try {

			PreparedStatement rs = connection.prepareStatement("SELECT * FROM data_core");
			PreparedStatement rss = connection.prepareStatement("SELECT * FROM data_settings");
			
			ResultSet results = rs.executeQuery();
			ResultSet resultsSettings = rss.executeQuery();
			
			int pulpe = 0;
			int lemons = 0;
			
			Map<String, String> map = new HashMap<>();
			
			Rank rank = Rank.PLAYER;
			boolean modEnabled = false;
			String username = "";
			String firstLog = "ERROR";
			String uuid = "";
			String nickName = "ERROR";
			Rank nickRank = Rank.PLAYER;
			
			while(results.next()) {
				if(results.getString("username").equalsIgnoreCase(name))
				{
					uuid = results.getString("uuid");
					pulpe = results.getInt("pulpe");
					lemons = results.getInt("lemons");	
					rank = Rank.powerToRank(results.getInt("rank"));
					modEnabled = results.getBoolean("modenabled");
					username = results.getString("username");
					firstLog = results.getString("firstlog");
					nickName = results.getString("nick_name");
					nickRank = Rank.powerToRank(results.getInt("nick_rank"));
				}
			}
			
			if(username.isEmpty() || username == "") {
				return null;
			}
			
			Account account = new Account(uuid);
			
			while(resultsSettings.next())
			{
				map.put("hub_movement", resultsSettings.getString("hub_movement"));
				map.put("group_requests", resultsSettings.getString("group_requests"));
				map.put("friends_requests", resultsSettings.getString("friends_requests"));
				map.put("msg_allow", resultsSettings.getString("msg_allow"));
				map.put("join_notifs", resultsSettings.getString("join_notifs"));
				map.put("host_notifs", resultsSettings.getString("host_notifs"));
				map.put("vip_symbol", resultsSettings.getString("vip_symbol"));
				map.put("vip_prefix", resultsSettings.getString("vip_prefix"));
				map.put("global_gender", resultsSettings.getString("global_gender"));
				map.put("join_effect", resultsSettings.getString("join_effect"));
				map.put("join_message", resultsSettings.getString("join_message"));
			}
			
			account.setPulpes(pulpe, true);
			account.setLemons(lemons, true);
			account.setModEnabled(modEnabled, true);
			account.setRank(rank.getPower(), true);
			account.setLastUsername(username, true);
			account.setFirstConnection(firstLog, true);
			account.setNickName(nickName, true);
			account.setNickRank(nickRank, true);
			
			for(String s : map.keySet())
			{
				account.setSetting(s, map.get(s));
			}
			
			return CoreMain.getInstance().getAccountManager().createAccount(uuid, account);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public Account convertToRedis(Player player)
	{
		final String uuid = player.getUniqueId().toString();
		Account account = new Account(uuid);
		
		if(CoreMain.getInstance().getAccountManager().getAccountsNames().contains(uuid))
		{
			CoreMain.getInstance().getAccountManager().removeAccount(uuid);
		}
		
		if(!CoreMain.getInstance().getAccountManager().getAccountsNames().contains(uuid))
		{
			try {
				PreparedStatement rs = connection.prepareStatement("SELECT * FROM data_core WHERE uuid = ?");
				PreparedStatement rss = connection.prepareStatement("SELECT * FROM data_settings WHERE uuid = ?");
				rs.setString(1, player.getUniqueId().toString());
				rss.setString(1, player.getUniqueId().toString());
				
				ResultSet results = rs.executeQuery();
				ResultSet resultsSettings = rss.executeQuery();
				
				int pulpe = 0;
				int lemons = 0;
				
				Map<String, String> map = new HashMap<>();
				
				Rank rank = Rank.PLAYER;
				boolean modEnabled = false;
				String username = "";
				String firstLog = "ERROR";
				String nickName = "ERROR";
				Rank nickRank = Rank.PLAYER;
				
				while(results.next()) {
					pulpe = results.getInt("pulpe");
					lemons = results.getInt("lemons");	
					rank = Rank.powerToRank(results.getInt("rank"));
					modEnabled = results.getBoolean("modenabled");
					username = results.getString("username");
					firstLog = results.getString("firstlog");
					nickName = results.getString("nick_name");
					nickRank = Rank.powerToRank(results.getInt("nick_rank"));
				}
				
				while(resultsSettings.next())
				{
					map.put("hub_movement", resultsSettings.getString("hub_movement"));
					map.put("group_requests", resultsSettings.getString("group_requests"));
					map.put("friends_requests", resultsSettings.getString("friends_requests"));
					map.put("msg_allow", resultsSettings.getString("msg_allow"));
					map.put("join_notifs", resultsSettings.getString("join_notifs"));
					map.put("host_notifs", resultsSettings.getString("host_notifs"));
					map.put("vip_symbol", resultsSettings.getString("vip_symbol"));
					map.put("vip_prefix", resultsSettings.getString("vip_prefix"));
					map.put("global_gender", resultsSettings.getString("global_gender"));
					map.put("join_effect", resultsSettings.getString("join_effect"));
					map.put("join_message", resultsSettings.getString("join_message"));
				}
				
				account.setPulpes(pulpe, true);
				account.setLemons(lemons, true);
				account.setModEnabled(modEnabled, true);
				account.setRank(rank.getPower(), true);
				account.setLastUsername(username, true);
				account.setFirstConnection(firstLog, true);
				account.setNickName(nickName, true);
				account.setNickRank(nickRank, true);
				
				for(String s : map.keySet())
				{
					account.setSetting(s, map.get(s));
				}
				
				Bukkit.getPluginManager().callEvent(new dataLoadedEvent(player, account));
				
				return CoreMain.getInstance().getAccountManager().createAccount(uuid, account);
			} catch (SQLException e) {
				System.out.println("ERREUR : Lors de la sauvegarde des données du joueur sur Redis");
			}
		}
		
		Bukkit.getPluginManager().callEvent(new dataLoadedEvent(player, account));
		
		return CoreMain.getInstance().getAccountManager().createAccount(uuid, account);

	}
	
	public Account getNewDefaultRedisAccount(Player player)
	{
		String uuid = player.getUniqueId().toString();
		Account account = new Account(uuid);
		account.setRank(Rank.PLAYER);
		account.setSetting("hub_movement", "WALK");
		account.setSetting("group_requests", "ENABLED");
		account.setSetting("friends_requests", "ENABLED");
		account.setSetting("msg_allow", "ENABLED");
		account.setSetting("join_notifs", "NULL");
		account.setSetting("host_notifs", "NULL");
		account.setSetting("vip_symbol", "NULL");
		account.setSetting("vip_prefix", "&dCustom");
		account.setSetting("global_gender", "MALE");
		account.setSetting("join_effect", "NOTHING");
		account.setSetting("join_message", "&6&oa rejoint le hub !");
		account.setLemons(0, true);
		account.setPulpes(0, true);
		account.setNickRank(Rank.PLAYER);
		account.setNickName(player.getName(), true);
		Date now = new Date();
		String pattern = "dd-MM-yyyy";
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		account.setFirstConnection(formatter.format(now));
		return account;
	}
	
	public void saveData(Player player)
	{
		String uuid = player.getUniqueId().toString();
		if(CoreMain.getInstance().getAccountManager().getAccount(uuid) != null)
		{
			Account account = CoreMain.getInstance().getAccountManager().getAccount(uuid);
			int pulpe = account.getPulpes();
			int lemons = account.getLemons();
			int power = account.getRank().getPower();
			boolean modEnabled = account.isModEnabled();
			String firstLog = account.getFirstConnection();
			int nickRank = account.getNickedRank().getPower();
			String nickName = account.getNickedName();
			
			if(account.getFirstConnection() == null || account.getFirstConnection().isEmpty()) return;
			
			PreparedStatement rs;
			try {
				rs = connection.prepareStatement("UPDATE data_core SET pulpe = ?, lemons = ?, rank = ?, modenabled = ?, username = ?, firstlog = ?, nick_rank = ?, nick_name = ? WHERE uuid = ?");
				
				rs.setInt(1, pulpe);
				rs.setInt(2, lemons);
				rs.setInt(3, power);
				rs.setBoolean(4, modEnabled);
				rs.setString(5, player.getDisplayName());
				rs.setString(6, firstLog);
				rs.setInt(7, nickRank);
				rs.setString(8, nickName);
				
				rs.setString(9, uuid);
				
				rs.executeUpdate();
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			PreparedStatement rss;
			try {
				rss = connection.prepareStatement("UPDATE data_settings SET hub_movement = ?, group_requests = ?, friends_requests = ?, msg_allow = ?, join_notifs = ?, host_notifs = ?, vip_symbol = ?, vip_prefix = ?, global_gender = ?, join_effect = ?, join_message = ? WHERE uuid = ?");
				
				rss.setString(1, account.getSetting("hub_movement"));
				rss.setString(2, account.getSetting("group_requests"));
				rss.setString(3, account.getSetting("friends_requests"));
				rss.setString(4, account.getSetting("msg_allow"));
				rss.setString(5, account.getSetting("join_notifs"));
				rss.setString(6, account.getSetting("host_notifs"));
				rss.setString(7, account.getSetting("vip_symbol"));
				rss.setString(8, account.getSetting("vip_prefix"));
				rss.setString(9, account.getSetting("global_gender"));
				rss.setString(10, account.getSetting("join_effect"));
				rss.setString(11, account.getSetting("join_message"));
				
				rss.setString(12, uuid);
				
				rss.executeUpdate();
				rss.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public boolean isConnected() {
		return connection != null;
	}
	
}
