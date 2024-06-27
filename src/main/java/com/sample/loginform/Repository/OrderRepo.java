package com.sample.loginform.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sample.loginform.Entity.Order;

@Repository
public interface OrderRepo extends JpaRepository<Order,Long>{
	 

	List<Order> findByUser_UserId(Long userId);}
