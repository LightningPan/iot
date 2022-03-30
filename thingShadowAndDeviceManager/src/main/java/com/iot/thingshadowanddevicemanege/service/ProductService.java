package com.iot.thingshadowanddevicemanege.service;

import com.iot.thingshadowanddevicemanege.dao.ProductDao;
import com.iot.thingshadowanddevicemanege.model.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {
    private final ProductDao productDao;

    @Autowired
    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public Optional<Product> findProductById(Long id) {
        return productDao.findById(id);
    }
}
