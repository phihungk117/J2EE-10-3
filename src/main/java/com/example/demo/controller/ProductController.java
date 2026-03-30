package com.example.demo.controller;

import com.example.demo.entity.Product;
import com.example.demo.entity.Category;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("")
    public String index(
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            Model model) {
        int pageSize = 5;
        Page<Product> page = productService.findPaginated(pageNo, pageSize, keyword, categoryId, sortBy);

        model.addAttribute("listproduct", page.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("sortBy", sortBy);
        
        model.addAttribute("listCategory", categoryService.getAllCategories());

        return "products"; 
    }

    @GetMapping("/create")
    public String create(Model model) {
        Product newProduct = new Product();
        Category category = new Category();
        newProduct.setCategory(category);
        model.addAttribute("product", newProduct);
        model.addAttribute("listCategory", categoryService.getAllCategories());
        return "create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("product") Product product,
                         BindingResult result,
                         @RequestParam("imageFile") MultipartFile imageFile,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("listCategory", categoryService.getAllCategories());
            return "create";
        }
        
        if (!imageFile.isEmpty()) {
            try {
                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path uploadPath = Paths.get("images");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                product.setImage("/images/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        productService.saveProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") int id, Model model) {
        Product product = productService.getProductById(id);
        if (product != null) {
            model.addAttribute("product", product);
            model.addAttribute("listCategory", categoryService.getAllCategories());
            return "edit";
        }
        return "redirect:/products";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") int id, Model model) {
        Product product = productService.getProductById(id);
        if (product != null) {
            model.addAttribute("product", product);
            return "detail";
        }
        return "redirect:/products";
    }

    @PostMapping("/edit")
    public String edit(@Valid @ModelAttribute("product") Product product,
                       BindingResult result,
                       @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                       Model model) {
        if (result.hasErrors()) {
            model.addAttribute("listCategory", categoryService.getAllCategories());
            return "edit";
        }

        Product existingProduct = productService.getProductById(product.getId());
        if (existingProduct != null) {
            String imagePath = existingProduct.getImage();
            
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                    Path uploadPath = Paths.get("images");
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    imagePath = "/images/" + fileName;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            product.setImage(imagePath);
            productService.saveProduct(product);
        }

        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }
}
