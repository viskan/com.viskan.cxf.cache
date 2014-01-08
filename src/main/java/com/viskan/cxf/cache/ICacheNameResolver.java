package com.viskan.cxf.cache;

import org.apache.cxf.message.Message;

/**
 * Interface defining a resolver for cache names used
 * when caching objects
 * 
 * @author vimarhen
 *
 */
public interface ICacheNameResolver
{
	/**
	 * Resolve cache name based on provided @{link Message}
	 */
	String resolveCacheName(Message message);
}
