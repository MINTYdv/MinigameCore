package com.minty.leemonmc.core.stats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.minty.leemonmc.core.CoreMain;

public class StatsHandler implements Listener {

	private String tableName;
	private Connection connection = CoreMain.getInstance().getSql().getConnection();
	private List<String> stats = new ArrayList<>();
	
	public void init(String _name, List<String> _stats)
	{
		tableName = "data_" + _name;
		stats = _stats;
		
		createTable();
	}
	
	public boolean hasStatsAccount(String UUID)
	{
		try {
			PreparedStatement q = connection.prepareStatement("SELECT uuid FROM " + tableName + " WHERE uuid = ?");
			q.setString(1, UUID);
			ResultSet result = q.executeQuery();
			
			boolean hasAccount = result.next();
			q.close();
			
			return hasAccount;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	public void playerJoined(Player player)
	{

		if(tableName == null || tableName.isEmpty()) return;
		
		String UUID = player.getUniqueId().toString();
		StatsData data = StatsDataHandler.getPlayerStats(player);
		
		if(!hasStatsAccount(UUID))
		{
			createDatabaseAccount(player);
		}
		
		try {
			PreparedStatement rs = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE uuid = ?");
			rs.setString(1, UUID);
			
			ResultSet results = rs.executeQuery();

			Map<String, Integer> playersStats = new HashMap<>();
			
			while(results.next()) {
				for(String stat : stats)
				{
					playersStats.put(stat, results.getInt(stat));
				}
			}
			
			for(Entry<String, Integer> entry : playersStats.entrySet())
			{
				data.setStat(entry.getKey(), entry.getValue());
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void playerQuitted(Player player)
	{
		if(tableName == null || tableName.isEmpty()) return;
		
		String UUID = player.getUniqueId().toString();
		StatsData data = StatsDataHandler.getPlayerStats(player);
		
		try
		{
			String request = "UPDATE " + tableName + " SET ";
			
			for(int i = 0; i < stats.size(); i++)
			{
				String stat = stats.get(i);
				int value = data.getStat(stat);
				
				if(i == stats.size() -1) {
					request += stat + " = ? ";
				} else {
					request += stat + " = ?, ";
				}
			}
			
			request += "WHERE uuid = ?";
			
			PreparedStatement q = connection.prepareStatement(request);
			
			for(int i = 0; i < stats.size(); i++)
			{
				String stat = stats.get(i);
				q.setInt(i + 1, data.getStat(stat));
			}
			
			q.setString(stats.size() + 1, UUID);
			
			q.execute();
			q.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void createDatabaseAccount(Player player)
	{
		if(tableName == null || tableName.isEmpty()) return;
		
		String UUID = player.getUniqueId().toString();
		StatsData data = StatsDataHandler.getPlayerStats(player);
		
		try
		{
			
			String request = "INSERT INTO " + tableName + " (";
			
			for(int i = 0; i < stats.size(); i++)
			{
				String stat = stats.get(i);
				
				if(i == stats.size() - 1)
				{
					request += stat + ")";
				} else if(i == 0){
					request += "uuid," + stat + ",";
				} else {
					request += stat + ",";
				}
			}
			
			request += " VALUES (";
			
			for(int i = 0; i < stats.size(); i++)
			{
				if(i == stats.size() - 1)
				{
					request += "?)";
				} else if(i == 0)
				{
					request += "?,?,";
				} else
				{
					request += "?,";
				}
			}

			PreparedStatement q = connection.prepareStatement(request);
			
			q.setString(1, UUID);
			
			for(int i = 0; i < stats.size(); i++)
			{
				String stat = stats.get(i);
				q.setInt(i + 2, data.getStat(stat));
			}
			
			q.execute();
			q.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void createTable()
	{
		
		if(tableName == null || tableName.isEmpty()) return;
		
		try {
			String request = "CREATE TABLE IF NOT EXISTS " + tableName + " (";
			
			for(int i = 0; i < stats.size(); i++)
			{
				String stat = stats.get(i);
				
				if(i == stats.size() - 1)
				{
					request += stat + " INTEGER(55))";
				} else if(i == 0)
				{
					request += "uuid VARCHAR(55) UNIQUE, " + stat + " INTEGER(55),";
				} else {
					request += stat + " INTEGER(55),";
				}
			}
			
			PreparedStatement rs = connection.prepareStatement(request);
			rs.execute();
		} catch (SQLException e) {
		}
	}
	
	public List<String> getAllRegisteredUUIDs(String table)
	{
		String tableName = "data_" + table;
		
		List<String> result = new ArrayList<>();
		
		try {
			PreparedStatement rs = connection.prepareStatement("SELECT * FROM " + tableName);
			
			ResultSet results = rs.executeQuery();

			while(results.next())
			{
				result.add(results.getString("uuid"));
			}
		} catch (Exception e)
		{
			// Ignore it <3
		}
		return result;
	}
	
	public int getStat(String UUID, String table, String name)
	{
		String tableName = "data_" + table;
		
		try {
			PreparedStatement rs = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE uuid = ?");
			rs.setString(1, UUID);
			
			ResultSet results = rs.executeQuery();

			while(results.next()) {
				return results.getInt(name);
			}
		} catch (Exception e)
		{
			return 0;
		}
		return 0;
	}
	
}
