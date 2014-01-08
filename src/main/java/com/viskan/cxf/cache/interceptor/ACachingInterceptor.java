package com.viskan.cxf.cache.interceptor;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;

import com.viskan.cxf.cache.ICacheKeyGenerator;
import com.viskan.cxf.cache.ICacheNameResolver;
import com.viskan.cxf.cache.ICacheProvider;

/**
 * Base class for caching interceptors
 * 
 * @author vimarhen
 */
public abstract class ACachingInterceptor extends AbstractPhaseInterceptor<Message>
{
	protected ICacheProvider cachingProvider;
	protected ICacheKeyGenerator cacheKeyGenerator;
	protected ICacheNameResolver cacheNameResolver;
	
	public ACachingInterceptor(ICacheProvider cachingProvider, ICacheKeyGenerator cacheKeyGenerator, ICacheNameResolver cacheNameResolver, String phase)
	{
		super(phase);
		this.cachingProvider = cachingProvider;
		this.cacheKeyGenerator = cacheKeyGenerator;
		this.cacheNameResolver = cacheNameResolver;
	}
}
