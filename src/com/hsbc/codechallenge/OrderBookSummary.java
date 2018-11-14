package com.hsbc.codechallenge;

import org.apache.log4j.Logger;

/**
 * This encapsulate order book summarized report
 * @author simon
 *
 */
public class OrderBookSummary {
	
	private static Logger log = Logger.getLogger(OrderBookSummary.class);
	
	private int size;
	
	public OrderBookSummary(int size) {
		super();
		this.size 	= size;
		bidPrice 	= new float[size];
		bidQuantity = new int[size];
		askPrice 	= new float[size];
		askQuantity = new int[size];
	}
	
	private float bidPrice[];
	private int bidQuantity[];
	private float askPrice[];
	private int askQuantity[];
	
	/**
	 * Set summary report
	 * @param tradeSide
	 * @param level	Starting level as 1
	 * @param price
	 * @param quantity
	 */
	public void setSummary(TradeSide tradeSide, int level, Float price, int quantity)
	{
		if (tradeSide == TradeSide.BID)
		{
			bidPrice[level-1] = price;
			bidQuantity[level-1] = quantity;
		}
		else
		{
			askPrice[level-1] = price;
			askQuantity[level-1] = quantity;
		}
	}
	
	/**
	 * Get quantity for specified side/level
	 * @param tradeSide
	 * @param level
	 * @return
	 */
	public int getSummaryQty(TradeSide tradeSide, int level)
	{
		if (tradeSide == TradeSide.BID)
		{
			return bidQuantity[level-1];
		}
		else
		{
			return askQuantity[level-1];
		}
	}
	
	/**
	 * Get price for specified side/level
	 * @param tradeSide
	 * @param level
	 * @return
	 */
	public float getSummaryPrice(TradeSide tradeSide, int level)
	{
		if (tradeSide == TradeSide.BID)
		{
			return bidPrice[level-1];
		}
		else
		{
			return askPrice[level-1];
		}
	}

	@Override
	public String toString() {
		StringBuffer sBuf = new StringBuffer("\n**Order Book Summary**\n");
		sBuf.append( String.format("%s\t\t%s\t\t%s \n", "Bid", "Ask","Level" ));
		sBuf.append( String.format("%s\t%s\t%s\t%s \n", "Price", "Size",  "Price", "Size") );
		
		for (int i=0; i< size; i++)
		{
			if ( ( bidQuantity[i] > 0 ) || (askQuantity[i] > 0) )
				sBuf.append( String.format("%5.2f\t%5d\t%5.2f\t%5d\t%2d\n", bidPrice[i], bidQuantity[i], askPrice[i], askQuantity[i], i+1 ) );
		}
		sBuf.append("** END **");
		
		return sBuf.toString();
	}
	
	

}
