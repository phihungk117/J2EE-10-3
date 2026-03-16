package com.example.demo.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Category {
    private int id;
    @NotBlank(message = "Name must not be blank")
    private String name;
}