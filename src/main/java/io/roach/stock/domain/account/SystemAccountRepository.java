package io.roach.stock.domain.account;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;

@Repository
public interface SystemAccountRepository extends JpaRepository<SystemAccount, UUID> {
    @Query(value = "from SystemAccount where id=?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    // Not supported by Hibernate :/
    @QueryHints(value = {
            @QueryHint(name = "javax.persistence.lock.timeout", value = "1000"),
            @QueryHint(name = "javax.persistence.lock.scope", value = "EXTENDED")})
    Optional<SystemAccount> getByIdForUpdate(UUID id);
}
