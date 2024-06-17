package com.sample.loginform.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sample.loginform.Entity.Product;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long>{

}
