package com.minty.leemonmc.core.cache;
 
import java.util.logging.Level;

import org.bukkit.scheduler.BukkitRunnable;

import com.minty.leemonmc.core.CoreMain;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
 
public class RedisConnector {
 
    private JedisPool cachePool;
 
    public RedisConnector()
    {
        initiateConnection();
    }
 
    public Jedis getRedisResource() {
        return cachePool.getResource();
    }
 
    public void killConnection() {
        cachePool.close();
        cachePool.destroy();
    }
 
    private void initiateConnection() {
        // Préparation de la connexion
        connect();
 
        // Init du thread
        new BukkitRunnable() {
			
			@Override
			public void run()
			{
	            try {
	                cachePool.getResource().close();
	            } catch (Exception e) {
	                e.printStackTrace();
	                CoreMain.getInstance().getLogger().log(Level.SEVERE, "Error redis connection, Try to reconnect!", e);
	                connect();
	            }
			}
		}.runTaskTimer(CoreMain.getInstance(), 0, 20 * 10);
    }
 
    private void connect() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(-1);
        config.setJmxEnabled(false);
        config.setMinIdle(2);
        
        try {
            this.cachePool = new JedisPool(config, "127.0.0.1", 6379, 0, null);
            this.cachePool.getResource().close();
 
            System.out.println("Connected to database.");
        } catch (Exception e) {
            System.out.println("Can't connect to the database!");
        }
    }
}