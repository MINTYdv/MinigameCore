package com.minty.leemonmc.core.servers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;

import com.google.gson.Gson;
import com.minty.leemonmc.basics.core.Server;
import com.minty.leemonmc.basics.core.ServerGroup;
import com.minty.leemonmc.basics.core.ServerType;
import com.minty.leemonmc.core.CoreMain;

import redis.clients.jedis.Jedis;

public class ServerManager {

	private CoreMain main;
	private ServerType serverType;
	
	private final static Gson gson = new Gson();
	private static String KEY = "SERVER:";
	private Jedis jedis = CoreMain.getInstance().getRedisConnector().getRedisResource();
	
	public ServerManager(CoreMain _main)
	{
		this.main = _main;
	}
	
	public void init(Server server)
	{

	}
	

	public ServerType getServerType() {
		return serverType;
	}
	
	public void setServerType(ServerType serverType) {
		this.serverType = serverType;
	}
	
	/*
	 * REDIS STUFF / GETTERS AND SETTERS
	 * */
	
	public void removeServer()
	{
		Server server = getServer();
		if(jedis.exists(KEY + server.getName()))
		{
			jedis.del(KEY + server.getName());
		}
	}
	
	public Server getServer() {
		String name = Bukkit.getServerName();
		Server deserialize = deserialize(jedis.get(KEY + name));
		jedis.close();
		return deserialize;
	}
	
	public int getPlayingPlayers(ServerGroup group) {
		List<Server> servers = getServersOfGroup(group);
		int amount = 0;
		for(Server server : servers) {
			amount += server.getPlayingPlayers();
		}
		return amount;
	}
	
	public void saveServer(Server server)
	{
		jedis.set(KEY + server.getName(), serialize(server));
		jedis.close();
	}
	
	public Server getRandomLobby()
	{
		return randomServer(getServersOfGroup(ServerGroup.lobby));
	}
	
	public Server randomServer(List<Server> givenList) {
	    Random rand = new Random();
	    Server randomElement = givenList.get(rand.nextInt(givenList.size()));
	    return randomElement;
	}
	
	public List<Server> getServersOfGroup(ServerGroup group)
	{
		List<Server> servers = new ArrayList<>();
		for(String names : jedis.keys(KEY + group.toString() + "*"))
		{
			servers.add(deserialize(jedis.get(names)));
		}
		return servers;
	}
	
	public List<String> getServerNames(){
		List<String> servers = new ArrayList<>();
		for (String names : jedis.keys(KEY + "*"))
			servers.add(names);
		return servers;
	}
	
	private static String serialize(Server server) { 
		return gson.toJson(server); 
	}

	private static Server deserialize(String serverData) { 
		return (Server)gson.fromJson(serverData, Server.class);
	}
}
