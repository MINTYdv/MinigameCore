package com.minty.leemonmc.core.util.redis;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.gson.Gson;

import redis.clients.jedis.Jedis;

/**
 *
 * Ceci a été fait pour le serveur nommé 'LentiaMC'
 * et vous n'avez pas le droit de le changer et le publier
 * sans aucune autorisation pour me contacter sur Discord
 * -> Enissay#9910
 *
 * @author Enissay
 */
public abstract class AbstractJSON<T> {

	@Nonnull
	protected String REDIS_KEY;
	@Nonnull
	protected Class<T> object;
	@Nonnull
	protected Jedis jedis;

	private final Gson gson = new Gson();

	/**
	 * Constructor
	 *
	 * @param object the class to register in json to redis
	 * @param REDIS_KEY Redis's key example: ACCOUNT:
	 */
	public AbstractJSON(Class<T> object, String REDIS_KEY, Jedis jedis) {
		this.REDIS_KEY = REDIS_KEY;
		this.object = object;
		this.jedis = jedis;
	}

	/**
	 * To get the object class
	 * with its variables
	 *
	 * @param arg0
	 * @return deserialize
	 */
	public T getObject(String arg0) {
		if (jedis.exists(REDIS_KEY + arg0)) {
			T deserialize = deserialize(jedis.get(REDIS_KEY + arg0));
			jedis.close();
			return deserialize;
		}else return null;
	}
	
	public T getObjectByName(String arg0) {
		if (getObjectsNames().contains(arg0))
			return getObject(arg0.contains(REDIS_KEY) ? arg0.replace(REDIS_KEY, "") : arg0);
		else return null;
	}
	
	public List<T> getObjects(){
		List<T> list = new ArrayList<>();
		for (String objectsName : getObjectsNames()) {
			list.add(getObject(objectsName));
		}
		return list;
	}
	
	/**
	 * 
	 * To get all the objects
	 * names
	 * 
	 * @return list
	 */
	public List<String> getObjectsNames() {
		if (jedis.exists(REDIS_KEY + "*")) {
			List<String> list = new ArrayList<>();
			for (String names : jedis.keys(REDIS_KEY + "*")) 
				list.add(names);
			return list;
		}else return null;
	}

	/**
	 *
	 * Update the class registered in redis
	 *
	 * @param arg0 username, uuid etc...
	 */
	public void updateObject(String arg0, T object) {
		jedis.set(REDIS_KEY + arg0, serialize(object));
		jedis.close();
	}

	/**
	 *
	 * Remove the registered class from Redis
	 *
	 * @param arg0 username, uuid etc...
	 */
	public void removeObject(String arg0) {
		jedis.del(REDIS_KEY + arg0);
		jedis.close();
	}

	/**
	 *
	 * To serialize the class in JSON
	 *
	 * @return
	 */
	protected String serialize(Object object) {
		return gson.toJson(object);
	}

	/**
	 *
	 * To deserialize a class from a JSON serializer
	 *
	 * @param serializer
	 * @return
	 */
	protected T deserialize(String serializer) {
		return (T)gson.fromJson(serializer, object);
	}

}