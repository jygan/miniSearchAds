package simpleads.io.bittiger.ads;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.*;

public class AdsEngine {
	private String mAdsDataFilePath;
	private String mBudgetFilePath;
	private IndexBuilder indexBuilder;
	private String mMemcachedServer;
	private int mMemcachedPortal;
	
	public AdsEngine(String adsDataFilePath, String budgetDataFilePath,String memcachedServer,int memcachedPortal)
	{
		mAdsDataFilePath = adsDataFilePath;
		mBudgetFilePath = budgetDataFilePath;
		mMemcachedServer = memcachedServer;
		mMemcachedPortal = memcachedPortal;
		indexBuilder = new IndexBuilder(memcachedServer,memcachedPortal);
	}
	
	public Boolean init()
	{
		try {
			//load ads data
			byte[] adsData;
			adsData = Files.readAllBytes(Paths.get(mAdsDataFilePath));
			String adsContent = new String(adsData, StandardCharsets.UTF_8);
			JSONObject adsJsonObj = new JSONObject(adsContent);
			JSONArray adsList = adsJsonObj.getJSONArray("ads");
			for(int i = 0;i < adsList.length();i++)
			{
				Ad ad = new Ad();
				JSONObject adJson = adsList.getJSONObject(i);				
				ad.adId = adJson.getLong("adId");
				ad.campaignId = adJson.getLong("campaignId");
				ad.bidPrice = adJson.getDouble("bidPrice");
				ad.pClick = adJson.getDouble("pClick");
				ad.keyWords = new ArrayList<String>();
				JSONArray keyWords = adJson.getJSONArray("keyWords");
				for(int j = 0; j < keyWords.length();j++)
				{
					ad.keyWords.add(keyWords.getString(j));
				}
				
				if(!indexBuilder.buildInvertIndex(ad) || !indexBuilder.buildForwardIndex(ad))
				{
					//log				
				}				
			}
			
			//load budget data
			byte[] budgetData;
			budgetData = Files.readAllBytes(Paths.get(mBudgetFilePath));
			String budgetContent = new String(budgetData, StandardCharsets.UTF_8);
			JSONObject budgetJsonObj = new JSONObject(budgetContent);
			JSONArray campaignList = budgetJsonObj.getJSONArray("campaigns");
			for(int i = 0 ; i < campaignList.length();i++)
			{
				JSONObject campaignJson = campaignList.getJSONObject(i);
				Long campaignId = campaignJson.getLong("campaignId");
				double budget = campaignJson.getDouble("budget");
				if(!indexBuilder.updateBudget(campaignId, budget))
				{
					//log
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	public List<Ad> selectAds(String query)
	{
		//query understanding
		List<String> queryTerms = QueryParser.getInstance().QueryUnderstand(query);
		//select ads candidates
		List<Ad> adsCandidates = AdsSelector.getInstance(mMemcachedServer, mMemcachedPortal).selectAds(queryTerms);		
		//L0 filter by pClick, relevance score
		List<Ad> L0unfilteredAds = AdsFilter.getInstance().LevelZeroFilterAds(adsCandidates);
		//rank 
		List<Ad> rankedAds = AdsRanker.getInstance().rankAds(L0unfilteredAds);
		//L1 filter by relevance score : select top K ads
		int k = 20;
		List<Ad> unfilteredAds = AdsFilter.getInstance().LevelOneFilterAds(rankedAds,k);
		//Dedupe ads per campaign
		List<Ad> dedupedAds = AdsCampaignManager.getInstance(mMemcachedServer, mMemcachedPortal).DedupeByCampaignId(unfilteredAds);
		//pricing： next rank score/current score * current bid price
		AdPricing.getInstance().setCostPerClick(dedupedAds);
		//filter last one , ad without budget , ads with CPC < minReservePrice
		List<Ad> ads = AdsCampaignManager.getInstance(mMemcachedServer, mMemcachedPortal).ApplyBudget(dedupedAds);
		//allocation
		AdsAllocation.getInstance().AllocateAds(ads);
		return ads;
	}
}
