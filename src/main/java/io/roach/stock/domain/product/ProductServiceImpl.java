package io.roach.stock.domain.product;

import io.roach.stock.annotation.TransactionMandatory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@TransactionMandatory
public class ProductServiceImpl implements ProductService {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void create(Product product) {
        productRepository.save(product);
    }

    @Override
    public Page<Product> findProductsPage(Pageable page) {
        return productRepository.findAll(page);
    }

    @Override
    public Page<Product> findProductsByRandom(Pageable page) {
        return productRepository.findAllByRandom(page);
    }

    @Override
    public Product getProductByRef(String productRef) {
        return productRepository.findByReference(productRef)
                .orElseThrow(() -> new NoSuchProductException(productRef));
    }

    @Override
    public Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NoSuchProductException(id));
    }
}
