package io.roach.stock.domain.order;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.roach.stock.annotation.TransactionMandatory;

@Repository
@TransactionMandatory
public interface OrderItemRepository extends JpaRepository<BookingOrderItem, BookingOrderItem.Id> {
    @Query("select oi from BookingOrderItem oi "
            + "where oi.id.orderId = :orderId "
            + "and oi.id.accountId = :accountId")
    List<BookingOrderItem> findByOrderId(
            @Param("orderId") UUID orderId,
            @Param("accountId") UUID accountId);
}
