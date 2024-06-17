package com.sample.loginform.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sample.loginform.Entity.Cart;
import com.sample.loginform.Entity.Order;
import com.sample.loginform.Entity.Product;
import com.sample.loginform.Repository.CartRepo;
import com.sample.loginform.Repository.OrderRepo;
import com.sample.loginform.Repository.ProductRepo;
import com.sample.loginform.Service.ProductService;

@Controller
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepo productRepository;

    @Autowired
    private ProductService serv;

    @Autowired
    private CartRepo cartRepository;
    
    private int cartItemsCount=0;

    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam("productId") Long productId, RedirectAttributes redirectAttributes) {
        Product product = productRepository.findById(productId).orElse(null);

        if (product != null) {
            Cart cartItem = new Cart();
            cartItem.setProductId(product.getProductId());
            cartItem.setProductName(product.getProductName());
            cartItem.setDescription(product.getDescription());
            cartItem.setPrice(product.getPrice());
            cartItem.setProductImagePath(product.getProductImagePath());

            cartRepository.save(cartItem);

            redirectAttributes.addFlashAttribute("success", "Product added to cart successfully!");
            cartItemsCount++;
            return "redirect:/api/products";
        } else {
            redirectAttributes.addFlashAttribute("error", "Product not found!");
            return "redirect:/api/products";
        }
    }

    @GetMapping("/Cart")
    public String viewCart(Model model) {
        List<Cart> cartItems = cartRepository.findAll(); 
        model.addAttribute("cartItems", cartItems); 
        double totalAmount = cartItems.stream()
                .collect(Collectors.summingDouble(Cart::getPrice));
model.addAttribute("totalAmount", totalAmount);
        return "Cart";  
    }

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads";

    @PostMapping("/create")
    public String createProduct(
            @RequestParam("productId") Long productId,
            @RequestParam("productName") String productName,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("productImage") MultipartFile productImage
    ) {
        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String fileName = StringUtils.cleanPath(productImage.getOriginalFilename());
            Path path = Paths.get(UPLOAD_DIR, fileName);
            Files.copy(productImage.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            
            
            Product product = new Product();
            product.setProductId(productId);
            product.setProductName(productName);
            product.setDescription(description);
            product.setPrice(price);
            product.setProductImagePath("/uploads/" + fileName);

            productRepository.save(product);

            return "redirect:/api/products"; 
        } catch (IOException e) {
            throw new RuntimeException("Could not save file: " + productImage.getOriginalFilename(), e);
        }
    }
    @PostMapping("/remove-from-cart")
    public String removeFromCart(@RequestParam("cartItemId") Long cartItemId, RedirectAttributes redirectAttributes) {
        cartRepository.deleteById(cartItemId);
        redirectAttributes.addFlashAttribute("success", "Product removed from cart successfully!");
        cartItemsCount--;
        return "redirect:/api/products/Cart";
    }
    
    public int getCartItemsCount() {
		return cartItemsCount;
	}

    @ModelAttribute("cartItemCount")
    public int getCartItemCount() {
        return cartItemsCount;
    }

	@GetMapping
    public String getAllProducts(Model model) {
        model.addAttribute("products", serv.getAllProducts());
        return "home"; 
    }
	 @GetMapping("/login")
	    public String showLoginPage() {
	        return "login";
	    }
	 
	 
	 @Autowired
	    private OrderRepo orderRepository;

	    @PostMapping("/buy")
	    public String buyNow(Model model) {
	        model.addAttribute("order", new Order());
	        return "orderForm";
	    }

	    @PostMapping("/confirm-order")
	    public String confirmOrder(@ModelAttribute Order order, RedirectAttributes redirectAttributes) {
	       
	        List<Cart> cartItems = cartRepository.findAll();


	        for (Cart cartItem : cartItems) {
	            
	            order.setProductId(cartItem.getProductId());
	            order.setProductName(cartItem.getProductName());
	            order.setProductImagePath(cartItem.getProductImagePath());
	            order.setPrice(cartItem.getPrice());
	            order.setDescription(cartItem.getDescription());

	           
	            cartItem.setOrder(order);
	        }

	        Order savedOrder = orderRepository.save(order);

	        cartRepository.saveAll(cartItems);

	        cartRepository.deleteAll();
	        cartItemsCount = 0;

	        redirectAttributes.addFlashAttribute("success", "Order placed successfully!");
	        return "order-sucess";
	    }

}
