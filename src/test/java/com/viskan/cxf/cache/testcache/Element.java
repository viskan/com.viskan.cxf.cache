package com.viskan.cxf.cache.testcache;

import java.util.Date;

import com.viskan.cxf.cache.CacheResponse;

/**
 * Class holding cached data used by @{link CacheProvider}
 * @author vimarhen
 *
 */
public class Element
{
	private CacheResponse cacheResponse;
	private Date date = new Date();
	
	public CacheResponse getCacheResponse()
	{
		return cacheResponse;
	}
	
	public void setCacheResponse(CacheResponse cacheResponse)
	{
		this.cacheResponse = cacheResponse;
	}
	
	public Date getDate()
	{
		return date;
	}
	
	public void setDate(Date date)
	{
		this.date = date;
	}
}

