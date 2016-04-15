package simpleads.io.bittiger.ads;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

import net.spy.memcached.MemcachedClient;

public class IndexBuilder {
	private int EXP = 7200;
	private String mMemcachedServer;
	private int mMemcachedPortal;
	public IndexBuilder(String memcachedServer,int memcachedPortal)
	{
		mMemcachedServer = memcachedServer;
		mMemcachedPortal = memcachedPortal;
	}
	public Boolean buildInvertIndex(Ad ad)
	{
		try 
		{
			MemcachedClient cache = new MemcachedClient(new InetSocketAddress(mMemcachedServer, mMemcachedPortal));		
			for(int i = 0; i < ad.keyWords.size();i++)
			{
				String key = ad.keyWords.get(i);
				if(cache.get(key) instanceof Set)
				{
					@SuppressWarnings("unchecked")
					Set<Long>  adIdList = (Set<Long>)cache.get(key);
					adIdList.add(ad.adId);
				    cache.set(key, EXP, adIdList);
				}
				else
				{
					Set<Long>  adIdList = new HashSet<Long>();
					adIdList.add(ad.adId);
					cache.set(key, EXP, adIdList);
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public Boolean buildForwardIndex(Ad ad)
	{
		try 
		{
			MemcachedClient cache = new MemcachedClient(new InetSocketAddress(mMemcachedServer, mMemcachedPortal));
			String key = ad.adId.toString();
			cache.set(key, EXP, ad);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}		
		return true;
	}
	public Boolean updateBudget(Long campaignId,double budget)
	{
		try 
		{
			MemcachedClient cache = new MemcachedClient(new InetSocketAddress(mMemcachedServer, mMemcachedPortal));
			String key = campaignId.toString();
			cache.set(key, EXP, budget);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}		
		return true;
	}
}
