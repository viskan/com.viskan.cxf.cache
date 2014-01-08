package com.viskan.cxf.cache.interceptor;

import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.jaxrs.model.OperationResourceInfo;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;

import com.viskan.cxf.cache.CacheResponse;
import com.viskan.cxf.cache.ICacheKeyGenerator;
import com.viskan.cxf.cache.ICacheNameResolver;
import com.viskan.cxf.cache.ICacheProvider;
import com.viskan.cxf.cache.annotation.Cachable;

/**
 * Out interceptor that puts request in cache
 * 
 * @author vimarhen
 */
public class CachingOutInterceptor extends ACachingInterceptor
{
	public CachingOutInterceptor(ICacheProvider cachingProvider, ICacheKeyGenerator cacheKeyGenerator, ICacheNameResolver cacheNameResolver)
	{
		super(cachingProvider, cacheKeyGenerator, cacheNameResolver, Phase.MARSHAL);
	}
	
	@Override
	public void handleMessage(Message message) throws Fault
	{
		if (shouldCache(message))
		{
			String cacheKey = (String) message.getExchange().get(CachingInInterceptor.CACHE_KEY);
			
			if (cacheKey != null)
			{
				String cacheName = null;
				if (cacheNameResolver != null)
				{
					cacheName = cacheNameResolver.resolveCacheName(message);
				}
				
				OutputStream content = message.getContent(OutputStream.class);
				
				CacheAndWriteOutputStream cwos = new CacheAndWriteOutputStream (content);
				message.setContent (OutputStream.class, cwos);
		        cwos.registerCallback (new CachingOutCallback (cachingProvider, cacheName, cacheKey, message));
			}
		}
	}

	/**
	 * Checks if method invoked was tagged with {@link Cachable} annotation
	 */
	private boolean shouldCache(Message message)
	{
		OperationResourceInfo ori = message.getExchange().get(OperationResourceInfo.class);
		if (ori != null)
		{
			Method method = ori.getMethodToInvoke();
			if (method != null)
			{
				return method.getAnnotation(Cachable.class) != null;
			}
		}
		
		return false;
	}
	
	private class CachingOutCallback implements CachedOutputStreamCallback
	{
		private final ICacheProvider cacheProvider;
		private String cacheName;
		private final Serializable key;
		private Message message;

		public CachingOutCallback(ICacheProvider cacheProvider, String cacheName, Serializable key, Message message)
		{
			this.cacheProvider = cacheProvider;
			this.cacheName = cacheName;
			this.key = key;
			this.message = message;
		}
		
		/**
		 * @see org.apache.cxf.io.CachedOutputStreamCallback#onClose(org.apache.cxf.io.CachedOutputStream)
		 */
		@Override
		public void onClose(CachedOutputStream os)
		{
			try
	        {
	            if ( os != null )
	            {
	            	String body = IOUtils.toString (os.getInputStream ( ));
	            	if (StringUtils.isNotBlank(body))
	            	{
	            		@SuppressWarnings("unchecked")
	        			Map<String, List<String>> headers = (Map<String, List<String>>)  message.get(Message.PROTOCOL_HEADERS);
	        			
	            		CacheResponse cacheResponse = new CacheResponse();
	            		cacheResponse.setBody(body);
	            		cacheResponse.setHeaders(headers);
	            		cacheProvider.put(cacheName, key, cacheResponse);
	            	}
	            }

	        }
	        catch ( Exception e )
	        {
	            e.printStackTrace();
	        }  
		}

		/**
		 * @see org.apache.cxf.io.CachedOutputStreamCallback#onFlush(org.apache.cxf.io.CachedOutputStream)
		 */
		@Override
		public void onFlush(CachedOutputStream os)
		{
		}
	}
}
