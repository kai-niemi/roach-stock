package io.roach.stock.domain.order;

import java.util.List;
import java.util.UUID;

import io.roach.stock.domain.account.NoSuchSystemAccountException;
import io.roach.stock.domain.product.NoSuchProductException;

/**
 * Defines the business contract for placing and retrieving product orders.
 */
public interface OrderService {
    /**
     * Places a product order that is immediately approved.
     *
     * @param orderRequest a request object describing all order details
     * @return a placed order receipt
     * @throws IllegalArgumentException     if the first argument does
     *                                      not represent a valid order reference (length > 0)
     *                                      or is null, or the second argument contains illegal parameters
     * @throws NoSuchSystemAccountException if an account affected by the order
     *                                      placement does not exist
     * @throws NoSuchProductException       if the referenced product
     *                                      does not exist
     * @throws NegativeBalanceException     if an affected account or
     *                                      portfolio end up with a negative balance
     * @throws OrderRejectedException       if the order is rejected due to its
     *                                      specified product price
     */
    BookingOrder placeOrder(OrderRequest orderRequest);

    /**
     * Get a placed order by given reference.
     *
     * @param orderRef a unique order reference scoped to the client namespace
     * @return a placed order receipt
     * @throws IllegalArgumentException if the first argument does
     *                                  not represent a valid order reference (length > 0)
     *                                  or is null
     * @throws NoSuchOrderException     if the referenced order
     *                                  does not exist
     */
    BookingOrder getOrderByRef(String orderRef);

    /**
     * Find all placed orders for a given account reference.
     *
     * @param accountId a unique account reference scoped to the client namespace
     * @return list of placed order receipts, or an empty list if nothing is found
     * @throws IllegalArgumentException     if the first argument does
     *                                      not represent a valid account reference (length > 0)
     *                                      or is null
     * @throws NoSuchSystemAccountException if an account with the given
     *                                      reference does not exist
     */
    List<BookingOrder> findOrdersByAccountId(UUID accountId);
}
