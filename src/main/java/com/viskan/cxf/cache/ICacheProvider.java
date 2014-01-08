package com.viskan.cxf.cache;

import java.io.Serializable;

/**
 * Interface defining a provider used by caching framework to put and get cache values
 * @author vimarhen
 *
 */
public interface ICacheProvider
{
	/**
	 * Gets value from cache with provided key
	 * 
	 * @return Cached value or <b>null</b> if not value was found
	 */
	CacheResponse get(String cacheName, Serializable key);
	
	/**
	 * Puts value to cache with provided key and value
	 */
	void put(String cacheName, Serializable key, CacheResponse value);
}
