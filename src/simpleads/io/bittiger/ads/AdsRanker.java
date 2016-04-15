package simpleads.io.bittiger.ads;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdsRanker {
	private static AdsRanker instance = null;
	protected AdsRanker()
	{

	}
	public static AdsRanker getInstance() {
	      if(instance == null) {
	         instance = new AdsRanker();
	      }
	      return instance;
	}
	public List<Ad> rankAds(List<Ad> adsCandidates)
	{
		for(Ad ad : adsCandidates)
		{
			ad.qualityScore = 0.75 * ad.pClick  +  0.25 * ad.relevanceScore;
			ad.rankScore = ad.qualityScore * ad.bidPrice;			
		}
		//sort by rank score
		Collections.sort(adsCandidates, new Comparator<Ad>() {
	        @Override
	        public int compare(Ad ad2, Ad ad1)
	        {
	        	if (ad1.rankScore < ad2.rankScore)
	        		return -1;
	        	else if(ad1.rankScore > ad2.rankScore)
	        		return 1;
	        	else
	        		return 0;
	        }
	    });
		return adsCandidates;
	}
}
