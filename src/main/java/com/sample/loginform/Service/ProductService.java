package com.sample.loginform.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sample.loginform.Entity.Cart;
import com.sample.loginform.Entity.Product;
import com.sample.loginform.Repository.CartRepo;
import com.sample.loginform.Repository.ProductRepo;

import jakarta.transaction.Transactional;

@Service
public class ProductService {
	 @Autowired
	    private ProductRepo productRepository;
	 
	 @Autowired
	 private CartRepo repo;

	    public Product saveProduct(Product product) {
	        return productRepository.save(product);
	    }
	    public List<Product> getAllProducts() {
	        return productRepository.findAll();
	    }
	    
}
