package com.viskan.cxf.cache;

import org.apache.cxf.Bus;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.InterceptorProvider;

import com.viskan.cxf.cache.interceptor.CachingInInterceptor;
import com.viskan.cxf.cache.interceptor.CachingOutInterceptor;

/**
 * Feature that adds caching to Cxf
 * 
 * @author vimarhen
 *
 */
public class CachingFeature extends AbstractFeature
{
	private ICacheProvider cachingProvider;
	private ICacheKeyGenerator cacheKeyGenerator;
	private ICacheNameResolver cacheNameResolver;
	
	@Override
	protected void initializeProvider(InterceptorProvider provider, Bus bus)
	{
		if (cachingProvider == null)
		{
			throw new IllegalArgumentException("cachingProvider is null");
		}
		
		if (cacheKeyGenerator == null)
		{
			cacheKeyGenerator = new MessageKeyGenerator();
		}

		provider.getInInterceptors().add(new CachingInInterceptor(cachingProvider, cacheKeyGenerator, cacheNameResolver));
		provider.getOutInterceptors().add(new CachingOutInterceptor(cachingProvider, cacheKeyGenerator, cacheNameResolver));
				
		super.initializeProvider(provider, bus);
	}

	public ICacheProvider getCachingProvider()
	{
		return cachingProvider;
	}

	public void setCachingProvider(ICacheProvider cachingProvider)
	{
		this.cachingProvider = cachingProvider;
	}

	public ICacheKeyGenerator getCacheKeyGenerator()
	{
		return cacheKeyGenerator;
	}

	public void setCacheKeyGenerator(ICacheKeyGenerator cacheKeyGenerator)
	{
		this.cacheKeyGenerator = cacheKeyGenerator;
	}

	public ICacheNameResolver getCacheNameResolver()
	{
		return cacheNameResolver;
	}

	public void setCacheNameResolver(ICacheNameResolver cacheNameResolver)
	{
		this.cacheNameResolver = cacheNameResolver;
	}
}
