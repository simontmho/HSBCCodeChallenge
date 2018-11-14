package com.hsbc.codechallenge;

import java.util.Comparator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

public class OrderBook {
	
	private static Logger log = Logger.getLogger(OrderBook.class);
	
	private ConcurrentSkipListMap<Float, OrderList> bidQueue;
	private ConcurrentSkipListMap<Float, OrderList> askQueue;
	
	private Lock queueLock = new ReentrantLock();
	
	public OrderBook()
	{
		Comparator<Float> descendingOrder = (o1, o2)-> -1 * Float.compare(o1 , o2) ;
		bidQueue = new ConcurrentSkipListMap<>(descendingOrder);
		
		askQueue = new ConcurrentSkipListMap<>();
	}
	
	/*
	public void tryPutQueue(TradeSide tradeSide, Float priceLevel, OrderList orderList)
	{
		if (tradeSide == TradeSide.BID)
		{
			bidQueue.putIfAbsent(priceLevel, orderList);
		}
		else
		{
			askQueue.putIfAbsent(priceLevel, orderList);

		}

	}
	*/
	
	/*
	public void removeQueue(TradeSide tradeSide, Float priceLevel)
	{
		if (tradeSide == TradeSide.BID)
		{
			bidQueue.remove(priceLevel);
		}
		else
		{
			askQueue.remove(priceLevel);	
		}
	}
	*/
	
	/**
	 * Get the top entry on either BID/ASK side
	 * @param tradeSide
	 * @return
	 */
	public OrderList getTopLevel(TradeSide tradeSide)
	{
		OrderList orderList = null;
			
		if (tradeSide == TradeSide.BID)
		{
			
			try
			{
				queueLock.lock();
				for (Entry<Float, OrderList> entry :  bidQueue.entrySet())
				{
					if (entry.getValue().getQuantity() > 0)
						return entry.getValue();
				}
			}
			finally
			{
				queueLock.unlock();
			}
			
		}
		else
		{
			try
			{
				queueLock.lock();
				for (Entry<Float, OrderList> entry :  askQueue.entrySet())
				{
					if (entry.getValue().getQuantity() > 0)
						return entry.getValue();
				}
			}
			finally
			{
				queueLock.unlock();
			}
			
		}
		
		return orderList;
	}
	
	/**
	 * Place an order to order book
	 * @param order
	 */
	public void placeOrder(Order order)
	{
		ConcurrentSkipListMap<Float, OrderList> queue = getQueue(order.getTradeSide());
		
		OrderList orderList = null;
		try
		{
			queueLock.lock();
			orderList = queue.get(order.getPrice());
			if (orderList == null)
			{
				orderList = new OrderList(this, order.getTradeSide(), order.getPrice());		
				queue.put(order.getPrice(), orderList);		
			}
		} 
		finally
		{
			queueLock.unlock();
		}
		
		orderList.queueOrder(order);		
	}
	
	private ConcurrentSkipListMap<Float, OrderList>  getQueue(TradeSide tradeSide)
	{
		if (tradeSide == TradeSide.BID)
			return bidQueue;
		else
			return askQueue;
	}
	
	
	/**
	 * This return price summary for ALL level on BID/ASK side
	 * @return all orders on order book
	 */
	public OrderBookSummary getAllPrice()
	{
		OrderBookSummary summary = getAllPrice(null);		
		return summary;		
	}
	
	public OrderBookSummary getAllPrice(TradeSide tradeSide)
	{
		return getAllPrice(tradeSide, null);
	}
	/**
	 * This return price summary for ALL level on specified BID/ASK side
	 * @param  look for price for specified tradeSide, or null if need both side
	 * @return	all orders on order book
	 */
	public OrderBookSummary getAllPrice(TradeSide tradeSide,  Integer level)
	{
		OrderBookSummary summary = null;
		try {
			queueLock.lock();
			
			// determine the size
			int maxSize = 0;			
			if (tradeSide == TradeSide.BID)
				maxSize = bidQueue.size();
			if (tradeSide == TradeSide.ASK)
				maxSize = askQueue.size();
			if (tradeSide == null)
				maxSize = Math.max( bidQueue.size(), askQueue.size() );
			
			summary = new OrderBookSummary(maxSize);
			
			int pos;
			
			if ( (tradeSide == TradeSide.BID) || (tradeSide == null) )
			{
				pos = 1;
				for (OrderList list : bidQueue.values())
				{
					if (list.getQuantity() > 0)
					{
						if (level == null)
						{
							summary.setSummary(TradeSide.BID, pos, list.getPrice(), list.getQuantity());
						}
						else
						{
							// level is specified
							if (level == pos)
							{
								summary.setSummary(TradeSide.BID, pos, list.getPrice(), list.getQuantity());
							}
						}
						pos++;
					}
				}
			}
			
			if ( (tradeSide == TradeSide.ASK) || (tradeSide == null) )
			{
				pos = 1;
				
				for (OrderList list : askQueue.values())
				{
					if (list.getQuantity() > 0)
					{
						if (level == null)
						{
							summary.setSummary(TradeSide.ASK, pos, list.getPrice(), list.getQuantity());
						}
						else
						{
							// level is specified
							if (level == pos)
							{
								summary.setSummary(TradeSide.ASK, pos, list.getPrice(), list.getQuantity());
							}
						}
						pos++;
					}
				}
			}
		}
		finally
		{
			queueLock.unlock();
		}
		
		return summary;
	}
	
	/**
	 * For debugging purpose only
	 * @return
	 */
	public OrderBookSummary getAllPriceWithBreakdown()
	{
		OrderBookSummary summary = null;
		try {
			queueLock.lock();
			int maxSize = Math.max( bidQueue.size(), askQueue.size() );
			summary = new OrderBookSummary(maxSize);
			
			int pos;
			pos = 1;
			for (OrderList list : bidQueue.values())
			{
				log.info(list.getBreakdown());
				if (list.getQuantity() > 0)
					summary.setSummary(TradeSide.BID, pos++, list.getPrice(), list.getQuantity());
			}
			
			pos = 1;
			for (OrderList list : askQueue.values())
			{
				log.info(list.getBreakdown());
				if (list.getQuantity() > 0)
					summary.setSummary(TradeSide.ASK, pos++, list.getPrice(), list.getQuantity());
			}
		}
		finally
		{
			queueLock.unlock();
		}
		
		return summary;
	}

}
