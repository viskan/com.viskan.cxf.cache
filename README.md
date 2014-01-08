com.viskan.cxf.cache
====================

Caching feature of Cxf endpoints


Background
==========

In one of our projects we needed some caching layer in Cxf that could take care of high load bursts.
When searching non were found, (Please send a note if there already exists functionallity like this) so I decided 
to write my own.

Flow of functionallity
======================

One in-interceptor that builds a key and checks if there is a cached response and returns the response if there is 
cached data.

One out-interceptor that checks if the called JAX-RS method has Cachable annotation and if that is caches the response.

Usage
=====

From Java:
    JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();

    ICachingProvider cachingProvider = ...
    
    CachingFeature cachingFeature = new CachingFeature();
		cachingFeature.setCachingProvider(cachingProvider);
		
		sf.setFeatures(Arrays.asList((cachingFeature)));
		
