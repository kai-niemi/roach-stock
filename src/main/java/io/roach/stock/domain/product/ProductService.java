package io.roach.stock.domain.product;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Defines the business contract for browsing products and prices.
 */
public interface ProductService {
    void create(Product product);

    Product getProductById(UUID id) throws NoSuchProductException;

    Product getProductByRef(String productRef) throws NoSuchProductException;

    Page<Product> findProductsPage(Pageable page);
    Page<Product> findProductsByRandom(Pageable page);
}
