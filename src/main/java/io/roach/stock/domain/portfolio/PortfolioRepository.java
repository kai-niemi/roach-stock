package io.roach.stock.domain.portfolio;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.roach.stock.annotation.TransactionMandatory;

@Repository
@TransactionMandatory
public interface PortfolioRepository extends JpaRepository<Portfolio, UUID> {
    @Query("select item.product.reference, sum(item.quantity) "
            + "from Portfolio p "
            + "left join p.items item "
            + "where p.id = :accountId "
            + "group by item.product.reference")
    List<Object[]> sumProductQuantityByAccountId(@Param("accountId") UUID accountId);

    @Query("select sum(item.quantity) "
            + "from Portfolio p "
            + "left join p.items item "
            + "where p.id = :accountId "
            + "and item.product.id = :productId")
    Integer sumQuantityByProductId(@Param("accountId") UUID accountId,
                                   @Param("productId") UUID productId);

    @Query(value
            = "select items from Portfolio p "
            + "inner join p.items items "
            + "inner join items.product product "
            + "where p.id = :accountId",
            countQuery
                    = "select count(items) from Portfolio p "
                    + "inner join p.items items "
                    + "where p.id = :accountId")
    Page<PortfolioItem> findById(@Param("accountId") UUID accountId, Pageable page);
}
