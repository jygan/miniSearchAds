package simpleads.io.bittiger.ads;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.spy.memcached.MemcachedClient;

public class AdsCampaignManager {
	private static AdsCampaignManager instance = null;
	private int EXP = 7200;
	private String mMemcachedServer;
	private int mMemcachedPortal;
	private static double minPriceThreshold = 1.5;
	protected AdsCampaignManager(String memcachedServer,int memcachedPortal)
	{
		mMemcachedServer = memcachedServer;
		mMemcachedPortal = memcachedPortal;	
	}
	public static AdsCampaignManager getInstance(String memcachedServer,int memcachedPortal) {
	      if(instance == null) {
	         instance = new AdsCampaignManager(memcachedServer,memcachedPortal);
	      }
	      return instance;
	}
	public  List<Ad> DedupeByCampaignId(List<Ad> adsCandidates)
	{
		List<Ad> dedupedAds = new ArrayList<Ad>();
		HashSet<Long> campaignIdSet = new HashSet<Long>();
		for(Ad ad : adsCandidates)
		{
			if(!campaignIdSet.contains(ad.campaignId))
			{
				dedupedAds.add(ad);
				campaignIdSet.add(ad.campaignId);
			}
		}
		return dedupedAds;
	}
	public List<Ad> ApplyBudget(List<Ad> adsCandidates)
	{
		List<Ad> ads = new ArrayList<Ad>();
		try
		{
			MemcachedClient cache = new MemcachedClient(new InetSocketAddress(mMemcachedServer,mMemcachedPortal));
			for(int i = 0; i < adsCandidates.size()  - 1;i++)
			{
				Ad ad = adsCandidates.get(i);
				Long campaignId = ad.campaignId;
				double budget = (double)cache.get(campaignId.toString());
				if(ad.costPerClick <= budget && ad.costPerClick >= minPriceThreshold)
				{
					ads.add(ad);
					budget = budget - ad.costPerClick;
					cache.set(campaignId.toString(), EXP, budget);
				}
			}
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ads;
	}
	
}
