package simpleads.io.bittiger.ut;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import simpleads.io.bittiger.ads.QueryParser;

public class QueryUnderstandUT {

	@Test
	public void testQueryUnderstand() {
		String query = "looking for the new nike shoe";
		List<String> queryTerms = QueryParser.getInstance().QueryUnderstand(query);
		for(String term : queryTerms)
		{
			System.out.println("term="+ term);
		}
		assertEquals(queryTerms.size(),4);
	}

}
