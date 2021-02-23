package com.minty.leemonmc.core.cache;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.minty.leemonmc.basics.core.cache.Account;
import com.minty.leemonmc.core.CoreMain;
import com.minty.leemonmc.core.util.redis.AbstractJSON;

import redis.clients.jedis.Jedis;

public class AccountManager extends AbstractJSON<Account>{

	private final static String KEY = "ACCOUNT:";
	
	public AccountManager() {
		super(Account.class, KEY, CoreMain.getInstance().getRedisConnector().getRedisResource());
	}

	public Account getAccount(String UUID) {
		return this.getObject(UUID);
	}

	public void removeAccount(String UUID)
	{
		this.removeObject(UUID);
	}
	
	public String userNameToUUID(String username)
	{
		for(Account account : getAccounts()) {
			if(account.getLastUsername().equalsIgnoreCase(username)) {
				return account.getUUID();
			}
		}
		return "ERROR";
	}
	
	public String UUIDtoUsername(String UUID)
	{
		for(Account account : getAccounts()) {
			if(account.getUUID().equalsIgnoreCase(UUID)) {
				return account.getLastUsername();
			}
		}
		
		return "ERROR";
	}
	
	public Account createAccount(String UUID, Account account) {
		Jedis jedis = CoreMain.getInstance().getRedisConnector().getRedisResource();
		jedis.set(KEY + account.getUUID(), serialize(account));
		jedis.close();
		getAccounts().add(account);
		getAccountsNames().add(UUID);
		return account;
	}
	
	public void saveAccount(Account account)
	{
		this.updateObject(account.getUUID(), account);
	}
	
	public List<Account> getAccounts(){
		List<Account> accounts = new ArrayList<>();
		Jedis jedis = CoreMain.getInstance().getRedisConnector().getRedisResource();
		for(String key : jedis.keys(KEY + "*"))
		{
			accounts.add(deserialize(jedis.get(key)));
		}
		return accounts;
	}
	
	@Deprecated
	public Account getAccountByName(String name) {
		for (String server : getAccountsNames()) {
			if (server.contains(name)) 
				return getAccount(name);
		}
		return null;
	}
	
	@Deprecated
	public List<String> getAccountsNames(){
		List<String> accounts = new ArrayList<>();
		for (String names : CoreMain.getInstance().getRedisConnector().getRedisResource().keys(KEY + "*"))
			accounts.add(names);
		return accounts;
	}

}
