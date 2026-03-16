package com.example.demo.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product{
    private int id;
    
    @NotBlank(message = "Name must not be blank")
    private String name;

    @Length(min = 0, max = 200, message = "Image must not exceed 200 characters")
    private String image;

    @NotNull(message = "Price must not be null")
    @Min(value = 1, message = "Price must be greater than or equal to 1")
    @Max(value = 9999999, message = "Price must be less than or equal to 9999999")
    private long price;

    private Category category;
}