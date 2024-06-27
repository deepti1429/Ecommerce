package com.sample.loginform.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="cartDetails")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Cart {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private Long productId;
	    private String productName;
	    private String description;
	    private double price;
	    private String productImagePath;

	    @ManyToOne(fetch=FetchType.LAZY)
	    @JoinColumn(name = "user_id") // Corrected to reference user_id in User entity
	    private User user;

	    @ManyToOne(fetch=FetchType.LAZY)
	    @JoinColumn(name = "order_id")
	    private Order order;

		

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getProductId() {
			return productId;
		}

		public void setProductId(Long productId) {
			this.productId = productId;
		}

		public String getProductName() {
			return productName;
		}

		public void setProductName(String productName) {
			this.productName = productName;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public double getPrice() {
			return price;
		}

		public void setPrice(double price) {
			this.price = price;
		}

		public String getProductImagePath() {
			return productImagePath;
		}

		public void setProductImagePath(String productImagePath) {
			this.productImagePath = productImagePath;
		}

		public User getUser() {
			return user;
		}

		public void setUser(User loggedInUser) {
			this.user = loggedInUser;
		}

		public Order getOrder() {
			return order;
		}

		public void setOrder(Order order) {
			this.order = order;
		}

		public void setProduct(Product product) {
			// TODO Auto-generated method stub
			
		}
	    
}