package com.example.demo.model;

import com.example.demo.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private Product product;
    private int quantity;
    
    public Double getSubtotal() {
        if (product != null && product.getPrice() != null) {
            return product.getPrice() * quantity;
        }
        return 0.0;
    }
}
