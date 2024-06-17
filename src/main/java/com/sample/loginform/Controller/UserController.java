package com.sample.loginform.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.sample.loginform.Entity.User;
import com.sample.loginform.Service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public String createUser(@RequestParam String username,
                             @RequestParam String password,
                             @RequestParam(required = false) String email,
                             @RequestParam(required = false) String mobilenumber,
                             @RequestParam(required = false) String address) {
        User user = new User();
        user.setUserName(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setMobilenumber(mobilenumber);
        user.setAddress(address);
        
        userService.saveDetails(user);
        return "redirect:/login"; 
    }

    @GetMapping("/register")
    public String showRegistrationPage() {
        return "register"; 
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String userName,
                            @RequestParam String password,
                            HttpSession session) {
        Optional<User> user = userService.findByUserNameAndPassword(userName, password);
        if (user.isPresent()) {
            session.setAttribute("loggedInUser", user.get());
            return "redirect:/api/products"; 
        } else {
            return "redirect:/login?error=true"; 
        }
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; 
    }

    @GetMapping("/home")
    public ModelAndView home(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            modelAndView.addObject("userName", loggedInUser.getUserName());
            modelAndView.setViewName("home"); 
        } else {
            modelAndView.setViewName("redirect:/login");
        }
        return modelAndView;
    }
}
