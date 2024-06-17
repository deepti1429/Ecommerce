package com.sample.loginform.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sample.loginform.Entity.Cart;

@Repository
public interface CartRepo extends JpaRepository<Cart, Long>{


}
