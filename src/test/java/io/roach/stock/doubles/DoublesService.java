package io.roach.stock.doubles;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import io.roach.stock.domain.account.SystemAccountRepository;
import io.roach.stock.domain.account.AccountService;
import io.roach.stock.domain.account.TradingAccountRepository;
import io.roach.stock.domain.order.OrderItemRepository;
import io.roach.stock.domain.order.OrderRepository;
import io.roach.stock.domain.portfolio.PortfolioRepository;
import io.roach.stock.domain.product.ProductRepository;
import io.roach.stock.domain.product.ProductService;

@Service
public class DoublesService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AccountService accountService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SystemAccountRepository systemAccountRepository;

    @Autowired
    private TradingAccountRepository tradingAccountRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public void createTestDoubles() {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "wrong tx state");

        String isolationLevel = jdbcTemplate.queryForObject("SHOW transaction_isolation", String.class);
        logger.info("Reported isolation level: " + isolationLevel);

        accountService.createSystemAccount(TestDoubles.SYSTEM_ACCOUNT_A,
                "TRADER:A", TestDoubles.TRADER_INITIAL_BALANCE);

        accountService.createTradingAccount(TestDoubles.SYSTEM_ACCOUNT_A, TestDoubles.USER_ACCOUNT_ALICE,
                "ALICE", TestDoubles.USER_INITIAL_BALANCE);
        accountService.createTradingAccount(TestDoubles.SYSTEM_ACCOUNT_A, TestDoubles.USER_ACCOUNT_BOB,
                "BOB", TestDoubles.USER_INITIAL_BALANCE);
        accountService.createTradingAccount(TestDoubles.SYSTEM_ACCOUNT_A, TestDoubles.USER_ACCOUNT_BOBBY_TABLES,
                "BOBBY_TABLES", TestDoubles.USER_INITIAL_BALANCE);

        Arrays.stream(TestDoubles.ALL_PRODUCTS)
                .forEachOrdered(product -> productService.create(product));
    }

    @Transactional
    public void removeTestDoubles() {
        orderItemRepository.deleteAllInBatch();
        orderItemRepository.flush();

        orderRepository.deleteAllInBatch();
        orderRepository.flush();

        portfolioRepository.deleteAll();
        portfolioRepository.flush();

        tradingAccountRepository.deleteAllInBatch();
        tradingAccountRepository.flush();
        
        systemAccountRepository.deleteAllInBatch();
        systemAccountRepository.flush();

        productRepository.deleteAllInBatch();
        productRepository.flush();
    }
}
