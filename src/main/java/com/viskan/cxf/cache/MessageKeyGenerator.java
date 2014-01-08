package com.viskan.cxf.cache;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.jaxrs.impl.UriInfoImpl;
import org.apache.cxf.message.Message;

/**
 * Default key generator that builds a key from headers and body
 * 
 * @author vimarhen
 *
 */
public class MessageKeyGenerator implements ICacheKeyGenerator
{
	/**
	 * @see ICacheKeyGenerator#generateKey(Message)
	 */
	@Override
	public Serializable generateKey(Message m)
	{
		/*
		 * Key parts:
		 * - headers
		 * - request uri
		 * - request parameters
		 * - body
		 */
		StringBuilder sb = new StringBuilder();
		buildHeaderString(m, sb);
		
		UriInfo uriInfo = new UriInfoImpl(m); 
		
		String path = uriInfo.getPath();
		
		if (StringUtils.isNotBlank(path))
		{
			sb.append(",Query[");
			sb.append(uriInfo.getPath());
			sb.append("]");
		}
		
		sb.append(",Parameters[");
		sb.append(uriInfo.getQueryParameters());
		sb.append("]");
		
		InputStream content = m.getContent(InputStream.class);
		if (content != null)
		{
			sb.append(", Body[");
			CachedOutputStream cos = new CachedOutputStream();
			
			try
			{
				IOUtils.copy(content, cos);
				cos.flush();
				content.close();
				m.setContent(InputStream.class, cos.getInputStream());
				cos.close();
				String body = new String(cos.getBytes(), "utf-8");
				sb.append(body);
				sb.append("]");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * Builds headers part of key
	 */
	private void buildHeaderString(Message m, StringBuilder sb)
	{
		@SuppressWarnings("unchecked")
		Map<String, List<String>> headers = (Map<String, List<String>>)m.get(Message.PROTOCOL_HEADERS);
		
		sb.append("Headers[");
		
		for(Entry<String, List<String>> e : headers.entrySet())
		{
			sb.append(e.getKey());
			sb.append(";");
			sb.append(e.getValue());
		}

		sb.append("]");
	}
}
