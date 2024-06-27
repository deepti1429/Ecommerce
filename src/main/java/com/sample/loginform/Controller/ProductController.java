package com.sample.loginform.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

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
import com.sample.loginform.Entity.User;
import com.sample.loginform.Repository.CartRepo;
import com.sample.loginform.Repository.OrderRepo;
import com.sample.loginform.Repository.ProductRepo;
import com.sample.loginform.Service.ProductService;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepo productRepository;

    @Autowired
    private ProductService serv;

    @Autowired
    private CartRepo cartRepository;

    @Autowired
    private OrderRepo orderRepository;

    private int cartItemsCount = 0;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads";

    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam("productId") Long productId, HttpSession session, RedirectAttributes redirectAttributes) {
        Product product = productRepository.findById(productId).orElse(null);
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (product != null && loggedInUser != null) {
            Cart cartItem = new Cart();
            cartItem.setProduct(product);
            cartItem.setUser(loggedInUser);
            cartItem.setPrice(product.getPrice()); // Set price from Product entity
            cartItem.setDescription(product.getDescription()); // Set description from Product entity
            cartItem.setProductImagePath(product.getProductImagePath()); // Set image path from Product entity
            cartItem.setProductName(product.getProductName());
            cartItem.setProductId(product.getProductId());
            cartRepository.save(cartItem);

            redirectAttributes.addFlashAttribute("success", "Product added to cart successfully!");
            cartItemsCount++;
        } else {
            redirectAttributes.addFlashAttribute("error", "Product not found or user not logged in!");
        }
        
        return "redirect:/api/products";
    }

    @GetMapping("/Cart")
    public String viewCart(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser != null) {
            Long userId = loggedInUser.getUserId();
            List<Cart> cartItems = cartRepository.findByUserId(userId);
            model.addAttribute("cartItems", cartItems);
            double totalAmount = cartItems.stream().mapToDouble(Cart::getPrice).sum();
            model.addAttribute("totalAmount", totalAmount);
            return "Cart";
        } else {
            return "redirect:/api/products/login";
        }
    }

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
    public String removeFromCart(@RequestParam("cartItemId") Long cartItemId, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            cartRepository.deleteById(cartItemId);
            redirectAttributes.addFlashAttribute("success", "Product removed from cart successfully!");
            cartItemsCount--;
            return "redirect:/api/products/Cart";
        } else {
            return "redirect:/api/products/login";
        }
    }

    public int getCartItemsCount() {
        return cartItemsCount;
    }

    @ModelAttribute("cartItemCount")
    public int getCartItemCount() {
        return cartItemsCount;
    }

    @GetMapping
    public String getAllProducts(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            model.addAttribute("userName", loggedInUser.getUserName());
        }
        model.addAttribute("products", serv.getAllProducts());
        return "home"; 
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/buy")
    public String buyNow(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            Order order = new Order();
            order.setUser(loggedInUser);
            order.setName(loggedInUser.getUserName());
            order.setEmail(loggedInUser.getEmail());
            order.setAddress(loggedInUser.getAddress());
            order.setMobileNumber(loggedInUser.getMobilenumber());
            List<Cart> cartItems = cartRepository.findByUserId(loggedInUser.getUserId());
            for (Cart cartItem : cartItems) {
                cartItem.setOrder(order);
            }
            order.setCartItems(cartItems);
            order.setPaymentMethod("Credit Card");
            model.addAttribute("order", order);
        }
        return "orderForm";
    }

    @PostMapping("/confirm-order")
    public String confirmOrder(@ModelAttribute Order order, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        List<Cart> cartItems = cartRepository.findByUserId(loggedInUser.getUserId());

        for (Cart cartItem : cartItems) {
            Order newOrder = new Order();
            newOrder.setUser(loggedInUser);
            newOrder.setName(loggedInUser.getUserName());
            newOrder.setEmail(loggedInUser.getEmail());
            newOrder.setAddress(loggedInUser.getAddress());
            newOrder.setMobileNumber(loggedInUser.getMobilenumber());
            newOrder.setProductId(cartItem.getProductId());
            newOrder.setProductName(cartItem.getProductName());
            newOrder.setDescription(cartItem.getDescription());
            newOrder.setPrice(cartItem.getPrice());
            newOrder.setProductImagePath(cartItem.getProductImagePath());
            newOrder.setPaymentMethod(order.getPaymentMethod());
            newOrder.setUser(cartItem.getUser());
            orderRepository.save(newOrder);
        }

        cartRepository.deleteByUserUserId(loggedInUser.getUserId());
        cartItemsCount = 0;

        redirectAttributes.addFlashAttribute("success", "Order placed successfully!");
        return "order-sucess";
    }

    @PostMapping("/show-bill")
    public String showBill(@ModelAttribute Order order, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/api/products/login";
        }
        List<Cart> cartItems = cartRepository.findByUserId(loggedInUser.getUserId());
        double totalAmount = cartItems.stream().mapToDouble(Cart::getPrice).sum();
        
        for (Cart cartItem : cartItems) {
            Order billOrder = new Order();
            billOrder.setUser(cartItem.getUser());
            billOrder.setName(order.getName());
            billOrder.setEmail(order.getEmail());
            billOrder.setAddress(order.getAddress());
            billOrder.setMobileNumber(order.getMobileNumber());
            billOrder.setProductId(cartItem.getProductId());
            billOrder.setProductName(cartItem.getProductName());
            billOrder.setProductImagePath(cartItem.getProductImagePath());
            billOrder.setPrice(cartItem.getPrice());
            billOrder.setDescription(cartItem.getDescription());
            billOrder.setPaymentMethod(order.getPaymentMethod());
            orderRepository.save(billOrder);
        }

        model.addAttribute("order", order);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);

        return "bill"; 
    }
    
    @Transactional
    @PostMapping("/finalize-order")
    public String finalizeOrder(@ModelAttribute Order order, RedirectAttributes redirectAttributes, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/api/products/login";
        }

        cartRepository.deleteByUserUserId(loggedInUser.getUserId());
        cartItemsCount = 0;

        redirectAttributes.addFlashAttribute("success", "Order placed successfully!");
        return "order-sucess";
    }

    @GetMapping("/orders")
    public String getOrdersForUser(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        
        if (loggedInUser == null) {
            return "redirect:/api/products/login"; // Updated redirect URL to match login endpoint
        }

        Long userId = loggedInUser.getUserId();
        List<Order> userOrders = orderRepository.findByUser_UserId(userId);

        model.addAttribute("userOrders", userOrders);
        return "user-orders";
    }
}
