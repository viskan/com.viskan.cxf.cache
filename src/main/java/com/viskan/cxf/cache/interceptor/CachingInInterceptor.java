package com.viskan.cxf.cache.interceptor;

import java.io.Serializable;
import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;

import com.viskan.cxf.cache.CacheResponse;
import com.viskan.cxf.cache.ICacheKeyGenerator;
import com.viskan.cxf.cache.ICacheNameResolver;
import com.viskan.cxf.cache.ICacheProvider;

/**
 * In interceptor that checks if incoming request is cached and returns cached value if keys are matched
 * 
 * @author vimarhen
 */
public class CachingInInterceptor extends ACachingInterceptor
{
	static final String CACHE_KEY = "CACHE_KEY";
	
	public CachingInInterceptor(ICacheProvider cachingProvider, ICacheKeyGenerator cacheKeyGenerator, ICacheNameResolver cacheNameResolver)
	{
		super(cachingProvider, cacheKeyGenerator, cacheNameResolver, Phase.RECEIVE);
	}

	@Override
	public void handleMessage(Message message) throws Fault
	{
		Object client = message.get(Message.REQUESTOR_ROLE);
		if (client instanceof Boolean && (Boolean)client)
		{
			return;
		}
		
		Serializable key = cacheKeyGenerator.generateKey(message);
		String cacheName = null;
		if (cacheNameResolver != null)
		{
			cacheName = cacheNameResolver.resolveCacheName(message);
		}
		
		CacheResponse cacheResponse = cachingProvider.get(cacheName, key);
		
		// If we have a cached response, build an actual repsonse and return
		if (cacheResponse != null)
		{
			ResponseBuilder response = Response.ok(cacheResponse.getBody());
			
			if (cacheResponse.getHeaders() != null)
			{
				for(Entry<String, List<String>> e : cacheResponse.getHeaders().entrySet())
				{
					response.header(e.getKey(), StringUtils.join(e.getValue(), ","));
				}
			}
			
//			response.header("X-Cached-Data", true);
			
			message.getExchange().put(Response.class, response.build());
		}
		else
		{
			// Put cache key in message. Out interceptor will pick it up.
			message.getExchange().put(CACHE_KEY, key);
		}
	}
}
