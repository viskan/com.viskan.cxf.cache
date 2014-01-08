package com.viskan.cxf.cache;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.viskan.cxf.cache.annotation.Cachable;

/**
 * Test bean
 * 
 * @author vimarhen
 *
 */
public class TestServiceBean
{
	public static String TEST_STR = "Test string";
	public int GET_INVOCATION_COUNT = 0;
	public int GET_NOCACHE_INVOCATION_COUNT = 0;
	public int POST_INVOCATION_COUNT = 0;
	
	@GET
	@Path("/get")
	@Cachable
	public String get()
	{
		GET_INVOCATION_COUNT++;
		return TEST_STR;
	}
	
	@POST
	@Path("/post")
	@Cachable
	public String post()
	{
		POST_INVOCATION_COUNT++;
		return TEST_STR;
	}
	
	@GET
	@Path("/getNoCache")
	public String getNoCache()
	{
		GET_NOCACHE_INVOCATION_COUNT++;
		return TEST_STR;
	}
	
	public static class Pojo
	{
		private String name;

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}
	}
}
