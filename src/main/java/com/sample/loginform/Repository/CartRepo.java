package com.sample.loginform.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sample.loginform.Entity.Cart;

import jakarta.transaction.Transactional;

@Repository
public interface CartRepo extends JpaRepository<Cart, Long>{
	@Query("SELECT c FROM Cart c WHERE c.user.userId = :userId")
    List<Cart> findByUserId(@Param("userId") Long userId);
	@Modifying
    @Transactional
	 void deleteByUserUserId(Long userId);
}
