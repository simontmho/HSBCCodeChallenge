package com.hsbc.codechallenge;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class encapsulate Order definition
 * @author simon
 *
 */
public class Order {
	
	private static AtomicInteger orderIdSource = new AtomicInteger(0);
	
	private int orderId;
	
	private TradeSide tradeSide;
	
	private Float price;
	
	private int quantity;
	
	private int tradedQuantity=0;
	
	private OrderList orderList = null;
	
	private Lock orderLock = new ReentrantLock();
	
	public void lockOrder()
	{
		orderLock.lock();
	}
	
	public void unlockOrder()
	{
		orderLock.unlock();
	}
	
	public OrderList getOrderList() {
		return orderList;
	}

	public void setOrderList(OrderList orderList) {
		this.orderList = orderList;
	}

	public void addTradeQuantity(int tradeQty)
	{
		tradedQuantity += tradeQty;
	}
	
	
	public TradeSide getTradeSide() {
		return tradeSide;
	}
	
	public Float getPrice() {
		return price;
	}

	public int getQuantity() {
		return quantity-tradedQuantity;
	}

	public int getOrderId() {
		return orderId;
	}

	public Order(TradeSide tradeSide, Float price, int quantity) {
		this.orderId = orderIdSource.getAndIncrement();		// assign order ID sequence
		this.tradeSide = tradeSide;
		this.price = price;
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "Order [orderId=" + orderId + ", tradeSide=" + tradeSide + ", price=" + price + ", quantity=" + getQuantity()
				+ "]";
	}
	
	
	
}
