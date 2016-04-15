package simpleads.io.bittiger.ads;

import java.io.Serializable;
import java.util.List;

public class Ad implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Long adId;
	public Long campaignId;
	public List<String> keyWords;
	public double relevanceScore;
	public double pClick;	
	public double bidPrice;
	public double rankScore;
	public double qualityScore;
	public double costPerClick;
	public int position;//1: main line, 2: side bar
}
