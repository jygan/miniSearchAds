package simpleads.io.bittiger.ut;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import simpleads.io.bittiger.ads.Ad;
import simpleads.io.bittiger.ads.AdsRanker;

public class AdsRankerUT {

	@Test
	public void testRankAds() {
		Ad ad1 = new Ad();
		ad1.adId = 1111L;
		ad1.bidPrice = 3.5;
		ad1.pClick = 0.3;
		ad1.relevanceScore = 0.6;
		
		Ad ad2 = new Ad();
		ad2.adId = 1112L;
		ad2.bidPrice = 3.8;
		ad2.pClick = 0.25;
		ad2.relevanceScore = 0.8;
		
		Ad ad3 = new Ad();
		ad3.adId = 1113L;
		ad3.bidPrice = 3.5;
		ad3.pClick = 0.4;
		ad3.relevanceScore = 0.3;
		
		Ad ad4 = new Ad();
		ad4.adId = 1114L;
		ad4.bidPrice = 5.5;
		ad4.pClick = 0.2;
		ad4.relevanceScore = 0.5;
		List<Ad> adsCandidates = new ArrayList<Ad>();
		adsCandidates.add(ad1);
		adsCandidates.add(ad2);
		adsCandidates.add(ad3);
		adsCandidates.add(ad4);
		List<Ad> rankedAds = AdsRanker.getInstance().rankAds(adsCandidates);
		for(Ad ad : rankedAds)
		{
			System.out.println("ad id :"+ad.adId+",ad rank score:"+ad.rankScore);
		}
		assertEquals((int)(rankedAds.get(0).rankScore * 100),76);
		assertEquals((int)(rankedAds.get(1).rankScore * 100),63);
		assertEquals((int)(rankedAds.get(2).rankScore * 100),55);
		assertEquals((int)(rankedAds.get(3).rankScore * 100),42);
	}

}
