package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotBlank(message = "Name must not be blank")
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Length(min = 0, max = 200, message = "Image must not exceed 200 characters")
    @Column(name = "image", length = 200)
    private String image;
    
    @NotNull(message = "Price must not be null")
    @Min(value = 1, message = "Price must be greater than or equal to 1")
    @Max(value = 9999999, message = "Price must be less than or equal to 9999999")
    @Column(name = "price", nullable = false)
    private Double price;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
