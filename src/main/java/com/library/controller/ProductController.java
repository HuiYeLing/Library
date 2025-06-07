package com.library.controller;

import com.library.domain.Order;
import com.library.domain.Product;
import com.library.entity.vo.ApiResult;
import com.library.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/getById")
    public ApiResult getProductById(@RequestParam Long id) {
        return productService.getProductById(id);
    }
    @GetMapping("/getAll")
    public ApiResult getAllProducts() {
        return productService.getAllProducts();
    }
    @PostMapping("/add")
    public ApiResult addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }
    @PutMapping("/update")
    public ApiResult updateProduct(@RequestBody Product product) {
        return productService.updateProduct(product);
    }
    @DeleteMapping("/delete")
    public ApiResult deleteProduct(@RequestParam Long id) {
        return productService.deleteProduct(id);
    }
    // 商品购买接口：用户购买商品并生成订单
    @PostMapping("/buy")
    public ApiResult buy(@RequestParam Long userId, @RequestParam Long productId) {
        // 直接调用 service 层的 buy 方法
        return productService.buy(userId, productId);
    }

    @Value("${uploadPath}")
    private String uploadPath;

    @PostMapping("/upload")
    public ApiResult uploadProductImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResult.error("文件为空");
        }
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + suffix;
        File dir = new File(uploadPath);
        if (!dir.exists()) dir.mkdirs(); // 确保目录存在
        File dest = new File(dir, fileName);
        try {
            file.transferTo(dest);
            String imageUrl = "/uploads/" + fileName;
            return ApiResult.ok(imageUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResult.error("上传失败");
        }
    }
}