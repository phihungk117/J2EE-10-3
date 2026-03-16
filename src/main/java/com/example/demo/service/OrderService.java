package com.example.demo.service;

import com.example.demo.entity.Account;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderDetail;
import com.example.demo.model.CartItem;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.OrderDetailRepository;
import com.example.demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private AccountRepository accountRepository;

    public void checkout(Account account, List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) return;

        Order order = new Order();
        order.setAccount(account);
        order.setOrderDate(new Date());

        double totalAmount = 0;
        for (CartItem item : cartItems) {
            totalAmount += item.getSubtotal();
        }
        order.setTotalAmount(totalAmount);

        // Save order first to generate ID
        Order savedOrder = orderRepository.save(order);

        // Save order details
        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(savedOrder);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getProduct().getPrice());
            orderDetailRepository.save(detail);
        }
    }
}
