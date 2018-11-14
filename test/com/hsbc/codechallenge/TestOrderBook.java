package com.hsbc.codechallenge;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;


public class TestOrderBook  {
	
	private static Logger log = Logger.getLogger(TestOrderBook.class);
	
	private final double DELTA = 0.001;
	
	@Test
	public void testSequentialOrders()
	{
		OrderBook orderBook = new OrderBook();
		
		Order order = null;
		int size = 10;
		OrderBookSummary summary = null;
		
		float price = 100.23f;
		
		for (int i=0; i < size; i++)
		{
			order = new Order(TradeSide.ASK,  price, 110);
			orderBook.placeOrder(order);
			
			order = new Order(TradeSide.ASK, price, 110);
			orderBook.placeOrder(order);
			
			summary = orderBook.getAllPrice();
			log.info(summary);
			
			order = new Order(TradeSide.BID, price, 200);
			orderBook.placeOrder(order);
			
			summary = orderBook.getAllPrice();
			log.info(summary);
	
		}
		
		summary = orderBook.getAllPrice();
		log.info(summary);
		
		assertEquals(0, summary.getSummaryQty(TradeSide.BID, 1));
		assertEquals(0, summary.getSummaryPrice(TradeSide.BID, 1),DELTA);
		
		assertEquals(size * 20, summary.getSummaryQty(TradeSide.ASK, 1));
		assertEquals(price, summary.getSummaryPrice(TradeSide.ASK, 1), DELTA);
			
	}
	
	@Test
	public void testPlaceOrder()
	{
		OrderBook orderBook = new OrderBook();
		
		Order buyOrder = new Order(TradeSide.BID, 100.12f, 100);
		orderBook.placeOrder(buyOrder);
		
		Order buyOrder2 = new Order(TradeSide.BID, 100.12f, 300);
		orderBook.placeOrder(buyOrder2);
		
		Order buyOrder3 = new Order(TradeSide.BID, 101.12f, 300);
		orderBook.placeOrder(buyOrder3);
		
		Order sellOrder = new Order(TradeSide.ASK, 102f, 100);
		orderBook.placeOrder(sellOrder);
		
		Order sellOrder2 = new Order(TradeSide.ASK, 103.1f, 200);
		orderBook.placeOrder(sellOrder2);
		
		Order sellOrder3 = new Order(TradeSide.ASK, 103.1f, 300);
		orderBook.placeOrder(sellOrder3);
		
		log.info("Buy order " +  buyOrder);
		log.info("Buy order " +  buyOrder2);
		log.info("Buy order " +  buyOrder3);
		log.info("Sell order " +  sellOrder);
		log.info("Sell order " +  sellOrder2);
		log.info("Sell order " +  sellOrder3);
		
		OrderBookSummary summary = orderBook.getAllPrice();
		log.info(summary);
		
		assertEquals(300, summary.getSummaryQty(TradeSide.BID, 1));
		assertEquals(101.12, summary.getSummaryPrice(TradeSide.BID, 1),DELTA);
		
		assertEquals(100, summary.getSummaryQty(TradeSide.ASK, 1));
		assertEquals(102.00, summary.getSummaryPrice(TradeSide.ASK, 1), DELTA);
		
		
		assertEquals(400, summary.getSummaryQty(TradeSide.BID, 2));
		assertEquals(100.12, summary.getSummaryPrice(TradeSide.BID, 2),DELTA);
		
		assertEquals(500, summary.getSummaryQty(TradeSide.ASK, 2));
		assertEquals(103.10, summary.getSummaryPrice(TradeSide.ASK, 2), DELTA);
		
		// validate BID only
		summary = orderBook.getAllPrice(TradeSide.BID);
		
		assertEquals(300, summary.getSummaryQty(TradeSide.BID, 1));
		assertEquals(101.12, summary.getSummaryPrice(TradeSide.BID, 1),DELTA);
		assertEquals(400, summary.getSummaryQty(TradeSide.BID, 2));
		assertEquals(100.12, summary.getSummaryPrice(TradeSide.BID, 2),DELTA);
		
		assertEquals(0, summary.getSummaryQty(TradeSide.ASK, 1));
		assertEquals(0, summary.getSummaryPrice(TradeSide.ASK, 1), DELTA);
		assertEquals(0, summary.getSummaryQty(TradeSide.ASK, 2));
		assertEquals(0, summary.getSummaryPrice(TradeSide.ASK, 2), DELTA);
		
		// validate ASK only
		summary = orderBook.getAllPrice(TradeSide.ASK);
		
		assertEquals(0, summary.getSummaryQty(TradeSide.BID, 1));
		assertEquals(0, summary.getSummaryPrice(TradeSide.BID, 1),DELTA);
		assertEquals(0, summary.getSummaryQty(TradeSide.BID, 2));
		assertEquals(0, summary.getSummaryPrice(TradeSide.BID, 2),DELTA);
		
		assertEquals(500, summary.getSummaryQty(TradeSide.ASK, 2));
		assertEquals(103.10, summary.getSummaryPrice(TradeSide.ASK, 2), DELTA);
		assertEquals(100, summary.getSummaryQty(TradeSide.ASK, 1));
		assertEquals(102.00, summary.getSummaryPrice(TradeSide.ASK, 1), DELTA);
		
		summary = orderBook.getAllPrice(TradeSide.ASK, 2);
		log.info(summary);
		assertEquals(500, summary.getSummaryQty(TradeSide.ASK, 2));
		assertEquals(103.10, summary.getSummaryPrice(TradeSide.ASK, 2), DELTA);
	}

	
	@Test
	public void testLargeBIDPlaceOrder()
	{
		OrderBookSummary summary = runTestLargePlaceOrder(TradeSide.ASK, TradeSide.BID);
		
		assertEquals(100, summary.getSummaryQty(TradeSide.BID, 1));
		assertEquals(100.12, summary.getSummaryPrice(TradeSide.BID, 1),DELTA);
		
		assertEquals(0, summary.getSummaryQty(TradeSide.ASK, 1));
		assertEquals(0, summary.getSummaryPrice(TradeSide.ASK, 1), DELTA);
	}
	
	@Test
	public void testLargeASKPlaceOrder()
	{
		OrderBookSummary summary = runTestLargePlaceOrder(TradeSide.BID, TradeSide.ASK);
		
		assertEquals(0, summary.getSummaryQty(TradeSide.BID, 1));
		assertEquals(0, summary.getSummaryPrice(TradeSide.BID, 1), DELTA);
		
		assertEquals(100, summary.getSummaryQty(TradeSide.ASK, 1));
		assertEquals(100.12, summary.getSummaryPrice(TradeSide.ASK, 1), DELTA);
	}
	
	private OrderBookSummary runTestLargePlaceOrder(TradeSide tradeSide1, TradeSide tradeSide2)
	{
		OrderBook orderBook = new OrderBook();
		
		Order order1 = new Order(tradeSide1, 100.12f, 100);
		orderBook.placeOrder(order1);
		
		Order order2 = new Order(tradeSide1, 100.12f, 300);
		orderBook.placeOrder(order2);
		
		log.info(order1);
		log.info(order2);

		log.info(orderBook.getAllPrice());
		
		Order order3 = new Order(tradeSide2, 100.12f, 500);		// this take all ASK position, but got open BID position

		orderBook.placeOrder(order3);

		
		log.info("** All Completed ..... ");
		OrderBookSummary summary = orderBook.getAllPrice();
		log.info(summary);
		
		return summary;
	}
	
	private class OrderThread extends Thread
	{
		private TradeSide tradeSide;
		private  int count;
		private  float prices[];
		private    int quant[];
		private OrderBook orderBook;
				
		public  OrderThread(OrderBook orderBook, TradeSide tradeSide, int count, float prices[], int quant[])
		{
			this.orderBook = orderBook;
			this.tradeSide = tradeSide;
			this.count = count;
			this.prices = prices;
			this.quant = quant;				
		}

		@Override
		public void run() {
			int pos=0;
			Order order;
			int sampleSize = prices.length;
			for (int i=0; i < count; i++)
			{
				order = new Order(tradeSide, prices[pos], quant[pos]);
				orderBook.placeOrder(order);
				pos = (pos+1) % (sampleSize);
			}
			
			log.info("** OrderThread completed.. " + tradeSide);
		}
		
	}
	
	@Test
	public void testRepeatOrders() throws InterruptedException
	{
		float prices[] = {100.11f , 100.11f , 100.11f , 100.11f , 100.11f };
		int quant[] = {100, 150, 130, 230, 310 };
		
		OrderBook orderBook = new OrderBook();
		int tryCount=10;
		
		OrderThread bidThread = new OrderThread(orderBook, TradeSide.BID, tryCount, prices, quant);
		bidThread.start();
		
		OrderThread askThread = new OrderThread(orderBook, TradeSide.ASK, tryCount, prices, quant);
		askThread.start();
		
		bidThread.join();
		askThread.join();
		
		log.info("** ALL OrderThread completed.. " );
		
	}
	
	@Test
	public void testParallelBIDOrders() throws InterruptedException
	{
		float prices[] = {101.11f , 103.11f , 100.11f , 106.11f , 105.11f };
		float pricesSorted[] = {106.11f , 105.11f , 103.11f , 101.11f , 100.11f };
		int quant[] = {100, 100, 100, 100, 100 };
				
		OrderBook orderBook = new OrderBook();
		OrderList.resetMatchCount();
		int tryCount=1000;
		int threadCount=10;
		
		OrderThread bidThread[] = new OrderThread[threadCount];		
		for (int i=0; i< threadCount; i++)
		{
			bidThread[i] = new OrderThread(orderBook, TradeSide.BID, tryCount, prices, quant);
			bidThread[i].start();
		}
		

		for (int i=0; i< threadCount; i++)
			bidThread[i].join();
		
		log.info("*******************************" );
		log.info("** BID OrderThread completed.. " );
		log.info("Match count: " + OrderList.getMatchCount());
		log.info("*******************************" );
		log.info(orderBook.getAllPrice());
		log.info("*******************************" );
		
		OrderBookSummary summary = orderBook.getAllPrice();	
		
		assertEquals(tryCount * threadCount * quant[0] / 5 , summary.getSummaryQty(TradeSide.BID,1));
		assertEquals(pricesSorted[0] , summary.getSummaryPrice(TradeSide.BID, 1), DELTA);
		assertEquals(pricesSorted[1] , summary.getSummaryPrice(TradeSide.BID, 2), DELTA);
		assertEquals(pricesSorted[2] , summary.getSummaryPrice(TradeSide.BID, 3), DELTA);
		assertEquals(pricesSorted[3] , summary.getSummaryPrice(TradeSide.BID, 4), DELTA);
		assertEquals(pricesSorted[4] , summary.getSummaryPrice(TradeSide.BID, 5), DELTA);
		
		assertEquals(0, OrderList.getMatchCount());

	}
	
	@Test
	public void testParallelOrders() throws InterruptedException
	{
		float prices[] = {100.11f , 100.11f , 100.11f , 100.11f , 100.11f };
		int quant[] = {100, 100, 100, 100, 100 };
		
//		float prices[] = {102.11f , 105.11f , 103.11f , 100.11f , 108.11f };
//		int quant[] = {200, 80, 100, 160, 110};

		long startTime = System.currentTimeMillis();
		
		OrderList.resetMatchCount();
		
		OrderBook orderBook = new OrderBook();
		int tryCount=100000;
		int threadCount=10;

		OrderThread bidThread[] = new OrderThread[threadCount];		
		OrderThread askThread[] = new OrderThread[threadCount];	
		for (int i=0; i< threadCount; i++)
		{
			bidThread[i] = new OrderThread(orderBook, TradeSide.BID, tryCount, prices, quant);
			bidThread[i].start();
			
			askThread[i] = new OrderThread(orderBook, TradeSide.ASK, tryCount, prices, quant);
			askThread[i].start();
		}

		for (int i=0; i< threadCount; i++)
			bidThread[i].join();
				
		for (int i=0; i< threadCount; i++)
			askThread[i].join();
		
		log.info("*******************************" );
		log.info("** ALL OrderThread completed.. " );
		log.info("Match count: " + OrderList.getMatchCount());
		log.info("*******************************" );
		log.info(orderBook.getAllPriceWithBreakdown());
		log.info("*******************************" );
		
		long endTime = System.currentTimeMillis();
		
		log.info("Performance: ");
		log.info("Elapsed time: " + (endTime - startTime) + "ms");
		log.info("Order processed: " + (tryCount *  threadCount* 2) + " orders");
		log.info("Elapsed time: " + (float) (endTime - startTime) /(tryCount *  threadCount* 2) + "ms/order");
		

		
		OrderBookSummary summary = orderBook.getAllPrice();	
		
		assertEquals(0 , summary.getSummaryQty(TradeSide.BID,1));
		assertEquals(0 , summary.getSummaryQty(TradeSide.ASK,1));
		assertEquals(tryCount * threadCount , OrderList.getMatchCount());
		
		
	}
	
	
}
