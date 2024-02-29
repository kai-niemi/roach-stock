package io.roach.stock.domain.product;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.roach.stock.annotation.TransactionMandatory;

@Repository
@TransactionMandatory
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findByReference(String productRef);

    @Query(value = "from Product p order by random()")
    Page<Product> findAllByRandom(Pageable pageable);
}
