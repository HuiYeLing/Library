package com.library.service;

import com.library.domain.Product;
import com.library.entity.vo.ApiResult;


public interface ProductService {
    ApiResult getProductById(Long id);
    ApiResult getAllProducts();
    ApiResult addProduct(Product product);
    ApiResult updateProduct(Product product);
    ApiResult deleteProduct(Long id);
    ApiResult buy(Long userId, Long productId);
}