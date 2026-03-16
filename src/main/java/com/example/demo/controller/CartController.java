package com.example.demo.controller;

import com.example.demo.entity.Account;
import com.example.demo.entity.Product;
import com.example.demo.model.CartItem;
import com.example.demo.repository.AccountRepository;
import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private AccountRepository accountRepository;

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }
        
        double total = 0;
        for (CartItem item : cart) {
            total += item.getSubtotal();
        }
        
        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") int productId, 
                            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                            HttpSession session) {
        Product product = productService.getProductById(productId);
        if (product != null) {
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            if (cart == null) {
                cart = new ArrayList<>();
                session.setAttribute("cart", cart);
            }
            
            boolean found = false;
            for (CartItem item : cart) {
                if (item.getProduct().getId() == productId) {
                    item.setQuantity(item.getQuantity() + quantity);
                    found = true;
                    break;
                }
            }
            if (!found) {
                cart.add(new CartItem(product, quantity));
            }
        }
        return "redirect:/cart";
    }

    @GetMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable("productId") int productId, HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart != null) {
            cart.removeIf(item -> item.getProduct().getId() == productId);
        }
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session, Principal principal) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart != null && !cart.isEmpty() && principal != null) {
            String username = principal.getName();
            Account account = accountRepository.findByLoginName(username)
                            .orElse(null);
            
            if (account != null) {
                orderService.checkout(account, cart);
                session.removeAttribute("cart");
                return "redirect:/products?checkoutSuccess";
            }
        }
        return "redirect:/cart?checkoutError";
    }
}
