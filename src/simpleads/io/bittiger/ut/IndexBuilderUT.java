package simpleads.io.bittiger.ut;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Set;

import net.spy.memcached.MemcachedClient;

import org.junit.Test;

import simpleads.io.bittiger.ads.Ad;
import simpleads.io.bittiger.ads.IndexBuilder;

public class IndexBuilderUT {

	@Test
	public void testBuildInvertIndex() {
		IndexBuilder indexBuilder = new IndexBuilder("127.0.0.1",11211);
		Ad ad1 = new Ad();
		ad1.adId = 1121L;
		ad1.keyWords = new ArrayList<String>();
		ad1.keyWords.add("basketball");
		ad1.keyWords.add("kobe");
		ad1.keyWords.add("shoe");
		ad1.keyWords.add("nike");
		
		Ad ad2 = new Ad();
		ad2.adId = 1122L;
		ad2.keyWords = new ArrayList<String>();
		ad2.keyWords.add("basketball");
		ad2.keyWords.add("shoe");
		ad2.keyWords.add("adidas");
		indexBuilder.buildInvertIndex(ad1);
		indexBuilder.buildInvertIndex(ad2);
		
		try {
			MemcachedClient cache = new MemcachedClient(new InetSocketAddress("127.0.0.1",11211));
			@SuppressWarnings("unchecked")
			Set<Long>  adIdList = (Set<Long>)cache.get("basketball");
			System.out.println("ad id list size = " + adIdList.size());
			assertEquals(adIdList.size() , 2);
			for(Object id : adIdList)
			{
				System.out.println("ad id = " + (Long)(id));
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	@Test
	public void testBuildForwardIndex() {
		IndexBuilder indexBuilder = new IndexBuilder("127.0.0.1",11211);
		Ad ad1 = new Ad();
		ad1.adId = 1125L;
		ad1.keyWords = new ArrayList<String>();
		ad1.keyWords.add("running");
		ad1.keyWords.add("shoe");
		ad1.keyWords.add("rebook");
		ad1.bidPrice = 4.5;
		ad1.campaignId = 99L;
		ad1.pClick = 0.34;
		indexBuilder.buildForwardIndex(ad1);
		try {
			MemcachedClient cache = new MemcachedClient(new InetSocketAddress("127.0.0.1",11211));
			@SuppressWarnings("unchecked")
			Ad  ad = (Ad)cache.get("1125");
			System.out.println("bid="+ad.bidPrice);
			System.out.println("pClick="+ad.pClick);
			System.out.println("campaignId="+ad.campaignId);
			int bid = (int) (ad.bidPrice * 100);
			assertEquals(bid , 450);
			assertEquals((long)ad.campaignId,99);
			int pClick = (int) (ad.pClick*100);
			assertEquals(pClick, 34);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
