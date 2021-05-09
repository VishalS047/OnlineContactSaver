package com.springboot.contactSaver.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.contactSaver.entities.OrderDetails;

public interface MyOrderRepo  extends JpaRepository<OrderDetails, Long>{
	
	public OrderDetails findByOrderId(String orderId);
}
