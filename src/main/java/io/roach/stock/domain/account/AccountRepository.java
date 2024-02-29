package io.roach.stock.domain.account;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.roach.stock.domain.common.Money;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    @Query("SELECT a.balance FROM Account a WHERE a.id = :id")
    Optional<Money> getBalanceById(@Param("id") UUID id);
}
