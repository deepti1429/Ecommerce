package com.sample.loginform.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sample.loginform.Entity.Order;

@Repository
public interface OrderRepo extends JpaRepository<Order,Long>{

}
