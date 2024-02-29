package io.roach.stock.domain.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.roach.stock.annotation.TransactionMandatory;

@Repository
@TransactionMandatory
public interface OrderRepository extends JpaRepository<BookingOrder, UUID> {
    @Query(value = "select value from limits where name=?1", nativeQuery = true)
    BigDecimal getLimit(String name);

    @Query("select o from BookingOrder o "
            + "left join o.items oi "
            + "where oi.account.id = :id "
            + "order by o.placedDate")
    List<BookingOrder> findByAccountId(@Param("id") UUID accountId);
}
