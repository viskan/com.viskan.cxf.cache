package com.viskan.cxf.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.cxf.binding.BindingFactoryManager;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.jayway.restassured.http.ContentType;
import com.viskan.cxf.cache.testcache.CacheProvider;

import static com.jayway.restassured.RestAssured.when;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.apache.http.HttpStatus.SC_OK;

/**
 * Test of @{link CachingFeature}
 * @author vimarhen
 *
 */
public class CachingFeatureTest extends Assert
{
	private static Server server;
	private static String URI = "http://localhost:6666/cxf";
	private static TestServiceBean bean = new TestServiceBean();
	private static CacheProvider cachingProvider = new CacheProvider();
	
	@BeforeClass
	public static void setUp()
	{
		JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();

		BindingFactoryManager manager = sf.getBus().getExtension(BindingFactoryManager.class);
		JAXRSBindingFactory factory = new JAXRSBindingFactory();
		factory.setBus(sf.getBus());

		CachingFeature cachingFeature = new CachingFeature();
		cachingFeature.setCachingProvider(cachingProvider);
		
		sf.setFeatures(Arrays.asList((cachingFeature)));
		
		manager.registerBindingFactory(JAXRSBindingFactory.JAXRS_BINDING_ID, factory);

		List<Object> providers = new ArrayList<>();
		
		providers.add(new JacksonJaxbJsonProvider());
		
		sf.setProviders(providers);
		
		sf.setServiceBean(bean);

		sf.setAddress(URI);
		server = sf.create();
	}
	
	@AfterClass
	public static void tearDown()
	{
		server.stop();
		server.destroy();
	}
	
	@Before
	public void before()
	{
		cachingProvider.clear();
		// Reset invocation count
		bean.GET_INVOCATION_COUNT = 0;
		bean.POST_INVOCATION_COUNT = 0;
	}
	
	@Test
	public void test_cache()
	{
		when()
			.get(URI + "/get")
		.then()
			.body(equalTo(TestServiceBean.TEST_STR))
			.statusCode(SC_OK);
		
		assertEquals("Method should be invoked once", 1, bean.GET_INVOCATION_COUNT);
		
		when()
			.get(URI + "/get")
		.then()
			.body(equalTo(TestServiceBean.TEST_STR))
			.statusCode(SC_OK);
		
		assertEquals("Method should still be invoked once", 1, bean.GET_INVOCATION_COUNT);
	}
	
	@Test
	public void test_cache_multiple_content_type_and_accept_is_preserved()
	{
		assertGet("/get");
		
		assertEquals("Method should be invoked once", 1, bean.GET_INVOCATION_COUNT);
		
		assertGet("/get");
		
		assertEquals("Method should still be invoked once", 1, bean.GET_INVOCATION_COUNT);
	}
	
	@Test
	public void test_cache_with_post_body()
	{
		TestServiceBean.Pojo pojo = new TestServiceBean.Pojo();
		
		assertPost(pojo);
		
		assertEquals("Method should be invoked once", 1, bean.POST_INVOCATION_COUNT);
		
		assertPost(pojo);
		
		assertEquals("Method should still be invoked once", 1, bean.POST_INVOCATION_COUNT);

		// Alter post body and test again
		
		pojo.setName("test");
		
		assertPost(pojo);
		
		assertEquals("Method should be called again due to new body", 2, bean.POST_INVOCATION_COUNT);
		
		assertPost(pojo);
		
		assertEquals("Method should NOT be called again due to same body", 2, bean.POST_INVOCATION_COUNT);
	}
	
	@Test
	public void test_no_cache_with_no_cachable_annotation()
	{
		assertGet("/getNoCache");
		
		assertEquals("Method should be invoked once", 1, bean.GET_NOCACHE_INVOCATION_COUNT);
		
		assertGet("/getNoCache");
		
		assertEquals("Method should be invoked twice", 2, bean.GET_NOCACHE_INVOCATION_COUNT);
	}
	
	private void assertGet(String path)
	{
		given()
			.header("Accept", "application/json,application/xml")
		.when()
			.get(URI + path)
			.then()
			.contentType(ContentType.JSON)
			.body(equalTo(TestServiceBean.TEST_STR))
			.statusCode(SC_OK);
	}
	
	private void assertPost(Object postdata)
	{
		given()
			.header("Accept", "application/json,application/xml")
			.contentType(ContentType.JSON)
			.body(postdata)
		.when()
			.post(URI + "/post")
		.then()
			.contentType(ContentType.JSON)
			.body(equalTo(TestServiceBean.TEST_STR))
			.statusCode(SC_OK);
	}
}
