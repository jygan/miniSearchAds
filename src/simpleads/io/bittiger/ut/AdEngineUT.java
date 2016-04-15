package simpleads.io.bittiger.ut;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import simpleads.io.bittiger.ads.Ad;
import simpleads.io.bittiger.ads.AdsEngine;

public class AdEngineUT {

	@Test
	public void testSelectAds() {
		String adsDataFilePath = "/Users/jiayangan/code/SimpleAds/AdsData.json";
		String budgetDataFilePath = "/Users/jiayangan/code/SimpleAds/BudgetData.json";
		String memcachedServer = "127.0.0.1";
		int memcachedPortal = 11211;
		AdsEngine adsEngine = new AdsEngine(adsDataFilePath,budgetDataFilePath,memcachedServer,memcachedPortal);
		if(adsEngine.init())
		{
			List<Ad> adsCandidates = adsEngine.selectAds("kobe basketball shoe");
			assertEquals(adsCandidates.size() , 6);
			for(Ad ad : adsCandidates)
			{
				System.out.println("selected ad id = " + ad.adId);
				System.out.println("selected ad relevance score = " + ad.relevanceScore);
			}
		}
	}

}
