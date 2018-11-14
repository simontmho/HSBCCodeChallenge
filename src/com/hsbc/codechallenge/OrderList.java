package com.hsbc.codechallenge;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

/**
 * List of order on specified side/price
 * @author simon
 *
 */
public class OrderList {
	
	private static Logger log = Logger.getLogger(OrderList.class);
	
	private Float price;
	private AtomicInteger quantity;
	private TradeSide tradeSide;
	private OrderBook orderBook;
	
	private static AtomicInteger matchCount = new AtomicInteger(0);
	
	public static int getMatchCount()
	{
		return matchCount.get();
	}
	
	public static void resetMatchCount()
	{
		matchCount.set(0);
	}
	
	private Queue<Order> orderQueue;
	
	public OrderList(OrderBook orderBook, TradeSide tradeSide, Float price)
	{
		this.orderBook = orderBook;
		this.tradeSide = tradeSide;
		this.price = price;
		this.quantity = new AtomicInteger(0);
		this.orderQueue = new ConcurrentLinkedQueue<>();
	}
	

	public void queueOrder(Order order )
	{		
//		log.info("OrderList - Queuing " + order);
		order.setOrderList(this);
		
		matchOrder(order);
		
		if (order.getQuantity() > 0)
		{
			addQuantity(order.getQuantity());
			orderQueue.add(order);
		}
			
	}
	
	public synchronized void addQuantity(int quantity)
	{
		this.quantity.addAndGet( quantity );
	}
	
	/**
	 * Get head order or return Null if not available.
	 * @return
	 */
	public Order getHeadOrder()
	{
		Order order = orderQueue.poll();
		
		if (order != null)
			addQuantity(-1 * order.getQuantity());
		
		return order;
	}

	/**
	 * Implement logic to match the order
	 */
	private void matchOrder(Order order) {
		TradeSide targetSide = getOppositeSide(tradeSide);
		OrderList topOrderList ;
		
		boolean isCompleted = false;
		
		int tradeQty;
		Order targetOrder=null;
		while (! isCompleted)
		{
			topOrderList = orderBook.getTopLevel(targetSide);
			
			if (topOrderList == null)
			{
				isCompleted = true;
				continue;
			}
			
			if  (
					(( order.getPrice() >=  topOrderList.getPrice()) && (order.getTradeSide() == TradeSide.BID)) ||
					(( order.getPrice() <=  topOrderList.getPrice()) && (order.getTradeSide() == TradeSide.ASK)) 
				)
			{
				// price cross the side
				targetOrder = topOrderList.getHeadOrder();
				
				if (targetOrder == null)
				{
					isCompleted = true;
					continue;
				}
				
				tradeQty = Math.min(order.getQuantity(), targetOrder.getQuantity());
				
//				log.info(",** MatchedQuantity: " + tradeQty + "," + "SrcOrder:, " + order.getTradeSide() + "," + order.getOrderId() + "," + order.getQuantity() + ",TargetOrder:," + targetOrder.getTradeSide() + "," + targetOrder.getOrderId() + "," + targetOrder.getQuantity());
				
				matchCount.addAndGet(1);
				
				targetOrder.addTradeQuantity( tradeQty);				
				order.addTradeQuantity( tradeQty);
				
				if (targetOrder.getQuantity() > 0)
				{
					// if target order still got residual qty, put it back
					topOrderList.queueOrder(targetOrder );
				}
								
				if (order.getQuantity() == 0)
				{
					isCompleted = true;
				}
			}
			else
			{
				isCompleted = true;		
			}			
		}
		
	}
	
	private TradeSide getOppositeSide(TradeSide tradeSide)
	{
		return (tradeSide == TradeSide.BID)? TradeSide.ASK : TradeSide.BID;					
	}

	@Override
	public String toString() {
		StringBuffer sBuf =  new StringBuffer( "OrderList [price=" + price + ", quantity=" + quantity + ", tradeSide=" + tradeSide + ",QueueSize=" + orderQueue.size() + "] \n");
			
		return sBuf.toString();
	}
	
	public String getBreakdown() {
		StringBuffer sBuf =  new StringBuffer( "OrderList [price=" + price + ", quantity=" + quantity + ", tradeSide=" + tradeSide + ",QueueSize=" + orderQueue.size() + "] \n");
		
		for (Order order : orderQueue)
		{
			sBuf.append("\t" + order + " \n ");
		}
		
		return sBuf.toString();
	}

	public Float getPrice() {
		return price;
	}

	public int getQuantity() {
		return quantity.get();
	}

	public TradeSide getTradeSide() {
		return tradeSide;
	}
	
}
