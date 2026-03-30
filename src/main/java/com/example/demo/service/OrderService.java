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
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng trống!");
        }

        // 1. Tính tổng tiền (Calculate Total Amount)
        double totalAmount = cartItems.stream()
                                      .mapToDouble(CartItem::getSubtotal)
                                      .sum();

        // 2. Tạo Order (Create Order)
        Order order = new Order();
        order.setAccount(account);
        order.setOrderDate(new Date());
        order.setTotalAmount(totalAmount);

        // Lưu Order để lấy ID tự tăng
        Order savedOrder = orderRepository.save(order);

        // 3. Lưu Order Detail (Save Order Details)
        List<OrderDetail> orderDetails = cartItems.stream().map(item -> {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(savedOrder);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getProduct().getPrice());
            return detail;
        }).toList();

        orderDetailRepository.saveAll(orderDetails);
    }
}
