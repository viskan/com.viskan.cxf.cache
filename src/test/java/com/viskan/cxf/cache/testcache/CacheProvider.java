package com.viskan.cxf.cache.testcache;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.viskan.cxf.cache.CacheResponse;
import com.viskan.cxf.cache.ICacheProvider;

public class CacheProvider implements ICacheProvider
{
	private Map<Serializable, Element> cache = new ConcurrentHashMap<>();
	
	@Override
	public CacheResponse get(String cacheName, Serializable key)
	{
		Element element = cache.get(key);
		if (element != null)
		{
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(element.getDate());
			
			calendar.add(Calendar.SECOND, 15);
			
			if (new Date().compareTo(calendar.getTime()) < 0)
			{
				return element.getCacheResponse();
			}
			else
			{
				cache.remove(key);
			}
		}
		
		return null;
	}

	@Override
	public void put(String cacheName, Serializable key, CacheResponse value)
	{
		Element element = new Element();
		element.setCacheResponse(value);
		cache.put(key,  element);
	}
	
	/**
	 * Clear cache
	 */
	public void clear()
	{
		cache.clear();
	}
}