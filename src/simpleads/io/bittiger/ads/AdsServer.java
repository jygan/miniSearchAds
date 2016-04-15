package simpleads.io.bittiger.ads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/*
1. Rank : qualityScore * bid ,qualityScore = pClick * relevanceScore
2. Filter by rank score, pClick, relevance score
3. Dedupe ads per campaign
4. Pricing: (next quality score/current quality score) * next bid price = next rank score / current quality
5. Allocation: only ads with rankScore > mainlineReservePrice can be allocated on mainline. 

reference：
http://searchengineland.com/new-adwords-ad-ranking-formula-what-does-it-mean-174946
http://www.wordstream.com/blog/ws/2013/10/24/adwords-ad-rank-algorithm
*/ 
public class AdsServer {
	public static void main(String[] args) throws IOException {
		if(args.length < 4)
		{
			System.out.println("Usage: AdsServer <adsDataFilePath> <budgetDataFilePath> <memcachedServer> <memcachedPortal>");
			System.exit(0);
		}
		String adsDataFilePath = args[0];
		String budgetDataFilePath = args[1];
		String memcachedServer = args[2];
		int memcachedPortal = Integer.parseInt(args[3]);
		AdsEngine adsEngine = new AdsEngine(adsDataFilePath,budgetDataFilePath,memcachedServer,memcachedPortal);
		if(adsEngine.init())
		{
			System.out.println("Ready to take quey");
			try{
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));				
				String query;				
				while((query=br.readLine())!=null){
					//System.out.println(query);
					List<Ad> adsCandidates = adsEngine.selectAds(query);
					for(Ad ad : adsCandidates)
					{
						System.out.println("final selected ad id = " + ad.adId);
						System.out.println("final selected ad rank score = " + ad.rankScore);
					}
				}
					
			}catch(IOException io){
				io.printStackTrace();
			}	
		}
	}
}
