package io.roach.stock.aspect;

import io.roach.stock.annotation.AdvisorOrder;
import io.roach.stock.annotation.TimeTravel;
import io.roach.stock.annotation.TimeTravelMode;
import io.roach.stock.annotation.TransactionBoundary;
import io.roach.stock.util.DatabaseInfo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

@Aspect
@Order(AdvisorOrder.TX_ATTRIBUTES_ADVISOR)
public class TransactionDecoratorAspect {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private boolean cancel;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Around(value = "Pointcuts.anyTransactionBoundaryOperation(transactionBoundary)",
            argNames = "pjp,transactionBoundary")
    public Object doInTransaction(ProceedingJoinPoint pjp, TransactionBoundary transactionBoundary)
            throws Throwable {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "No tx!");

        if (cancel || !DatabaseInfo.isCockroachDB(jdbcTemplate.getDataSource())) {
            cancel = true;
            return pjp.proceed();
        }

        // Grab from type if needed (for non-annotated methods)
        if (transactionBoundary == null) {
            transactionBoundary = TransactionRetryAspect.findAnnotation(pjp, TransactionBoundary.class);
        }

        if (!"(empty)".equals(transactionBoundary.applicationName())) {
            jdbcTemplate.update("SET application_name=?", transactionBoundary.applicationName());
        }

        if (!TransactionBoundary.Priority.normal.equals(transactionBoundary.priority())) {
            jdbcTemplate.execute("SET TRANSACTION PRIORITY " + transactionBoundary.priority().name());
        }

        if (transactionBoundary.timeout() > 0) {
            jdbcTemplate.update("SET statement_timeout=?", transactionBoundary.timeout() * 1000);
        }

        if (transactionBoundary.readOnly()) {
            jdbcTemplate.execute("SET transaction_read_only=true");
        }

        TimeTravel timeTravel = transactionBoundary.timeTravel();

        if (timeTravel.mode().equals(TimeTravelMode.FOLLOWER_READ)) {
            jdbcTemplate.execute("SET TRANSACTION AS OF SYSTEM TIME follower_read_timestamp()");
        } else if (timeTravel.mode().equals(TimeTravelMode.SNAPSHOT_READ)) {
            jdbcTemplate.update("SET TRANSACTION AS OF SYSTEM TIME INTERVAL '"
                    + timeTravel.interval() + "'");
        }

        if (logger.isTraceEnabled()) {
            logger.trace("Transaction attributes applied for {}: {}",
                    pjp.getSignature().toShortString(),
                    transactionBoundary);
        }

        return pjp.proceed();
    }
}
