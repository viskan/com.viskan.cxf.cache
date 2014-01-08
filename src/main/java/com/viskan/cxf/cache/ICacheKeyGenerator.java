package com.viskan.cxf.cache;

import java.io.Serializable;

import org.apache.cxf.message.Message;

/**
 * Interface defining a cache key generator used to generate keys for cached items
 * @author vimarhen
 *
 */
public interface ICacheKeyGenerator
{
	/**
	 * Generates a key based on provided {@link Message}
	 */
	Serializable generateKey(Message message);
}
