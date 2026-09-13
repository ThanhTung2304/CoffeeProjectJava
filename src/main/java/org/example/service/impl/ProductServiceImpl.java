package org.example.service.impl;

import org.example.entity.Product;
import org.example.repository.ProductRepository;
import org.example.repository.impl.ProductRepositoryImpl;
import org.example.service.ProductService;

import java.util.List;

public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl() {
        this.productRepository = new ProductRepositoryImpl();
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(int id) {
        if (id <= 0) {
            return null;
        }
        return productRepository.findById(id);
    }

    @Override
    public List<Product> findByName(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        return productRepository.findByName(name);
    }

    @Override
    public void create(Product product) {

        if (product == null) {
            throw new IllegalArgumentException("Product không được null");
        }

        if (product.getName() == null || product.getName().isBlank()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }

        if (product.getPrice() <= 0) {
            throw new IllegalArgumentException("Giá sản phẩm phải > 0");
        }

        productRepository.save(product);
    }

    @Override
    public void update(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product không được null");
        }

        if (product.getName() == null || product.getName().isBlank()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }

        if (product.getPrice() <= 0) {
            throw new IllegalArgumentException("Giá sản phẩm phải > 0");
        }

        productRepository.update(product);
    }

    @Override
    public void deleteById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID sản phẩm không hợp lệ");
        }
        productRepository.deleteById(id);
    }
}
