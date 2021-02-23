package com.minty.leemonmc.core;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.minty.leemonmc.basics.core.GameState;
import com.minty.leemonmc.basics.core.ServerType;
import com.minty.leemonmc.basics.core.cache.Account;
import com.minty.leemonmc.core.cache.AccountManager;
import com.minty.leemonmc.core.cache.RedisConnector;
import com.minty.leemonmc.core.cmd.CommandLag;
import com.minty.leemonmc.core.eco.CommandLemons;
import com.minty.leemonmc.core.events.CoreInitEvent;
import com.minty.leemonmc.core.events.GuisLoadingEvent;
import com.minty.leemonmc.core.listeners.CommandsListeners;
import com.minty.leemonmc.core.listeners.LeeCoreListeners;
import com.minty.leemonmc.core.queue.QueueManager;
import com.minty.leemonmc.core.ranks.CommandNick;
import com.minty.leemonmc.core.ranks.CommmandRank;
import com.minty.leemonmc.core.ranks.NickManager;
import com.minty.leemonmc.core.servers.ServerManager;
import com.minty.leemonmc.core.stats.StatsHandler;
import com.minty.leemonmc.core.tab.ScoreboardAnimator;
import com.minty.leemonmc.core.tab.TabRanksHandler;
import com.minty.leemonmc.core.util.Crypter;
import com.minty.leemonmc.core.util.GuiManager;
import com.minty.leemonmc.core.util.GuiUtils;
import com.minty.leemonmc.core.util.LeemonUtils;
import com.minty.leemonmc.games.LeemonGame;

import net.md_5.bungee.api.ChatColor;
import redis.clients.jedis.Jedis;

public class CoreMain extends JavaPlugin implements PluginMessageListener {

	/* 
	 * DATA RELATED
	 * */
	
	public SqlConnection sql;
	
	private String sqlUrl = "localhost";
	private String sqlDatabaseName = "leemonmc";
	private String sqlUser = "root";
	private String sqlPass;
	
	private GuiUtils gUtils;

	private GuiManager guiManager;
	private ServerManager serverManager;
	private QueueManager queueManager;
	private ScoreboardAnimator scoreboardAnimator;
	
	private AccountManager accountManager;
	private TabRanksHandler tabRanksHandler;
	private NickManager nickManager;
	private StatsHandler statsHandler;
	
	private LeemonUtils leemonUtils = new LeemonUtils(this);
	private LeemonGame leemonGame = (LeemonGame) Bukkit.getPluginManager().getPlugin("LeemonGame");
	private RedisConnector redisConnector;
	
	/* 
	 * OTHER/.. RELATED
	 * */
	
	public int bungeeOnlinePlayers = 0;
	private static CoreMain instance;
	
	@Override
	public void onEnable()
	{
		log("Plugin désormais actif !");
		
		instance = this;
		
		sqlDecrypter();
		registerReferences();
		registerListeners();
		registerCommands();
		
		registerBungeeRunnable();
        loadGuis();
        
		Bukkit.getPluginManager().callEvent(new CoreInitEvent(this));
		log("Sending core init event to all plugins...");
    }
	
	private void registerReferences()
	{
		leemonGame = (LeemonGame) Bukkit.getPluginManager().getPlugin("LeemonGame");
		sql = new SqlConnection(sqlUrl,sqlDatabaseName,sqlUser,sqlPass,this);
		getSql().connection();
		saveDefaultConfig();
	
		redisConnector = new RedisConnector();
		
		accountManager = new AccountManager();
		serverManager = new ServerManager(this);
		instance = this;
		tabRanksHandler = new TabRanksHandler();
		tabRanksHandler.setup();
		statsHandler = new StatsHandler();
		
		nickManager = new NickManager();
		getNickManager().setup();
		
		gUtils = new GuiUtils(this);
		leemonUtils = new LeemonUtils(this);
		serverManager.getServer();
		
		scoreboardAnimator = new ScoreboardAnimator();
		scoreboardAnimator.setup();
		
		queueManager = new QueueManager(this);
		queueManager.setup();
	}
	
	private void registerCommands()
	{
		log("Registering commands...");
		getCommand("money").setExecutor(new CommandLemons(this));
		getCommand("rank").setExecutor(new CommmandRank());
		getCommand("tps").setExecutor(new CommandLag());
		getCommand("lag").setExecutor(new CommandLag());
		getCommand("ping").setExecutor(new CommandLag());
		getCommand("nick").setExecutor(new CommandNick());
	}
	
	private void registerListeners()
	{
		log("Registering listeners...");
		getServer().getPluginManager().registerEvents(new LeeCoreListeners(this), this);
		getServer().getPluginManager().registerEvents(new CommandsListeners(), this);
		getServer().getPluginManager().registerEvents(getNickManager(), this);
		getServer().getPluginManager().registerEvents(getQueueManager(), this);
		
	    getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
	    getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
	}
	
	private void registerBungeeRunnable()
	{
        new BukkitRunnable() {
        	@Override
        	public void run()
        	{
        		for(@SuppressWarnings("unused") Player online : Bukkit.getOnlinePlayers())
        		{
            		getBungeeOnlinePlayers();
        		}
        	}
        }.runTaskTimer(this, 0, 20);
        
	}
	
	public void init(ServerType type, String minigameName)
	{
		System.out.println("Initiating : " + minigameName);
		getGameApi().getGameManager().setMinigameName(minigameName);
		init(type);
	}
	
	public void init(ServerType type)
	{
		getServerManager().setServerType(type);
		if(getServerManager().getServerType() == ServerType.MINIGAME) {
			getGameApi().getGameManager().setCurrentState(GameState.WAITING);
		}
	}
	
	private void sqlDecrypter()
	{
		Crypter c = new Crypter();
		char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
		String mdpkey = "MINTY1718POUCY6520";
		for (int i = 0; i < alphabet.length; i++) {
			mdpkey = mdpkey.replace(String.valueOf(alphabet[i]), String.valueOf(i));
		}
		int resultkey = 0;
		for (int i = 0; i < mdpkey.length(); i++) {
			char[] mdpkeychararray = mdpkey.toCharArray();
			resultkey = resultkey + Integer.valueOf(mdpkeychararray[i]);
		}
		this.sqlPass = c.decrypt("xcz|kdcixkl~", resultkey/resultkey*10);
	}
	
	public void sendPlayerToHub(Player player)
	{
		// Send target player to a lobby
		
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		
		try {
			out.writeUTF("Connect");
			out.writeUTF(getServerManager().getRandomLobby().getName());
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		player.sendPluginMessage(this, "BungeeCord", b.toByteArray());
	}
	
	private void loadGuis()
	{
		getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[LeemonMC] Chargement des menus en cours...");
		guiManager = new GuiManager(this);
		Bukkit.getPluginManager().registerEvents(guiManager, this);
		
		Bukkit.getPluginManager().callEvent(new GuisLoadingEvent(this, getGuiManager()));
	}

	public void log(String m)
	{
		getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[LeemonMC] " + m);
	}
	
	@Override
	public void onDisable() {
		for(Player p : Bukkit.getOnlinePlayers())
		{
			getStatsHandler().playerQuitted(p);
		}
		sql.disconnect();
		getServerManager().removeServer();
		System.out.println("[LeemonCore] Plugin inactif !");
	}

	  @Override
	  public void onPluginMessageReceived(String channel, Player player, byte[] message) {
	    if (!channel.equals("BungeeCord")) {
	      return;
	    }
	    
	    ByteArrayDataInput in = ByteStreams.newDataInput(message);
	    String subchannel = in.readUTF();
	    if (subchannel.equals("PlayerCount")) {
	    	@SuppressWarnings("unused")
			String server = in.readUTF();
	    	bungeeOnlinePlayers = in.readInt();
	    }
	    
	  }

	public String getPlayerDisplayNameChat(Player player)
	{
		String UUID = player.getUniqueId().toString();
		Account account = getAccountManager().getAccount(UUID);
		
		String name = account.getPrefixAccordingToSettings() + " " + account.getNickedName();

		return name;
	}
	
	public void getBungeeOnlinePlayers()
	{
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("PlayerCount");
		out.writeUTF("ALL");
		
		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
	
		player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
	}
	
	/* 
	 * Getters & Setters
	 * */
	
	public SqlConnection getSql() {
		return sql;
	}
	
	public GuiUtils getGuiUtils() {
		return gUtils;
	}

	public ServerManager getServerManager() {
		return serverManager;
	}
	
	public static CoreMain getInstance() {
		return instance;
	}
	
	public LeemonUtils getLeemonUtils() {
		return leemonUtils;
	}
	
	public GuiManager getGuiManager() {
		return guiManager;
	}
	
	public AccountManager getAccountManager() {
		return accountManager;
	}
	
	public StatsHandler getStatsHandler() {
		return statsHandler;
	}
	
	public QueueManager getQueueManager() {
		return queueManager;
	}
	
	public ScoreboardAnimator getScoreboardAnimator() {
		return scoreboardAnimator;
	}
	
	public TabRanksHandler getTabRanksHandler() {
		return tabRanksHandler;
	}
	
	public NickManager getNickManager() {
		return nickManager;
	}
	
	public RedisConnector getRedisConnector() {
		return redisConnector;
	}
	
	public LeemonGame getGameApi() {
		return leemonGame;
	}

}
