package com.iot.thingshadowanddevicemanege.dao;

import com.iot.thingshadowanddevicemanege.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDao extends JpaRepository<Product, Long> {
}
