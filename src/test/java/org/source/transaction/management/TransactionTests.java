package org.source.transaction.management;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.source.spring.io.Response;
import org.source.transaction.management.controller.TransactionController;
import org.source.transaction.management.facade.param.TransactionParam;
import org.source.transaction.management.facade.view.TransactionView;
import org.source.transaction.management.infrastructure.constant.Constants;
import org.source.transaction.management.infrastructure.enums.TransactionTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@ActiveProfiles(value = "dev")
@SpringBootTest
class TransactionTests {
    private static final String TRANSACTION_ID_TEST = "t000001";
    private static final String TRANSACTION_ID_TEST2 = "t000002";
    private static final String TRANSACTION_ID_TEST3 = "t000003";

    @Autowired
    private TransactionController controller;

    @BeforeAll
    static void setup(@Autowired TransactionController controller) {
        TransactionParam param = TransactionParam.builder().transactionId(TRANSACTION_ID_TEST).fromAccount("1234567890123456")
                .toAccount("123456789012345671").amount(new BigDecimal(1)).type(TransactionTypeEnum.TRANSFER.getType())
                .build();
        controller.add(param);
        TransactionParam param2 = TransactionParam.builder().transactionId(TRANSACTION_ID_TEST2).fromAccount("1234567890123456")
                .toAccount("123456789012345671").amount(new BigDecimal(2)).type(TransactionTypeEnum.TRANSFER.getType())
                .build();
        controller.add(param2);
    }

    @Test
    void get() {
        log.info("test get");
        Response<TransactionView> response = controller.get(TRANSACTION_ID_TEST);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(TRANSACTION_ID_TEST, response.getData().getTransactionId());
        Response<TransactionView> response2 = controller.get(TRANSACTION_ID_TEST + "01");
        Assertions.assertNull(response2.getData());
    }

    @Test
    void page() {
        log.info("test page");
        Response<Page<TransactionView>> response = controller.page(TRANSACTION_ID_TEST, null, null,
                null, Constants.PAGE_NUMBER_DEFAULT, Constants.PAGE_SIZE_DEFAULT);
        Assertions.assertNotNull(response);
        Page<TransactionView> page = response.getData();
        Assertions.assertTrue(CollectionUtils.isNotEmpty(page.getContent()));
        TransactionView view = page.getContent().getFirst();
        Assertions.assertEquals(TRANSACTION_ID_TEST, view.getTransactionId());
        Response<Page<TransactionView>> response2 = controller.page(null, "1234567890123456", null,
                null, Constants.PAGE_NUMBER_DEFAULT, Constants.PAGE_SIZE_DEFAULT);
        Assertions.assertTrue(CollectionUtils.isNotEmpty(response2.getData().getContent()));
        Response<Page<TransactionView>> response3 = controller.page(null, null, "123456789012345671",
                null, Constants.PAGE_NUMBER_DEFAULT, Constants.PAGE_SIZE_DEFAULT);
        Assertions.assertTrue(CollectionUtils.isNotEmpty(response3.getData().getContent()));
        Response<Page<TransactionView>> response4 = controller.page(null, null, null,
                TransactionTypeEnum.TRANSFER.getType(), Constants.PAGE_NUMBER_DEFAULT, Constants.PAGE_SIZE_DEFAULT);
        Assertions.assertTrue(CollectionUtils.isNotEmpty(response4.getData().getContent()));
    }

    @Test
    void add() {
        log.info("test add");
        TransactionParam param = TransactionParam.builder().transactionId(TRANSACTION_ID_TEST3).fromAccount("1234567890123456")
                .toAccount("123456789012345671").amount(new BigDecimal(2)).type(TransactionTypeEnum.TRANSFER.getType())
                .build();
        Response<TransactionView> response = controller.add(param);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(TRANSACTION_ID_TEST3, response.getData().getTransactionId());
    }

    @Test
    void update() {
        log.info("test update");
        TransactionParam param = TransactionParam.builder().transactionId(TRANSACTION_ID_TEST).fromAccount("1234567890123456")
                .toAccount("123456789012345672").amount(new BigDecimal(3)).type(TransactionTypeEnum.RED_PACKET.getType())
                .description("test update").build();
        Response<TransactionView> response = controller.update(param.getTransactionId(), param);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(TRANSACTION_ID_TEST, response.getData().getTransactionId());
        Assertions.assertEquals("123456789012345672", response.getData().getToAccount());
        TransactionParam param2 = TransactionParam.builder().transactionId(TRANSACTION_ID_TEST)
                .fromAccount("12345678901234562").build();
        Response<TransactionView> response2 = controller.update(param.getTransactionId(), param2);
        Assertions.assertEquals("12345678901234562", response2.getData().getFromAccount());
        TransactionParam param3 = TransactionParam.builder().transactionId(TRANSACTION_ID_TEST)
                .toAccount("1234567890123456723").build();
        Response<TransactionView> response3 = controller.update(param.getTransactionId(), param3);
        Assertions.assertEquals("1234567890123456723", response3.getData().getToAccount());
        TransactionParam param4 = TransactionParam.builder().transactionId(TRANSACTION_ID_TEST)
                .amount(new BigDecimal(4)).build();
        Response<TransactionView> response4 = controller.update(param.getTransactionId(), param4);
        Assertions.assertEquals(new BigDecimal(4), response4.getData().getAmount());
        TransactionParam param5 = TransactionParam.builder().transactionId(TRANSACTION_ID_TEST)
                .type(TransactionTypeEnum.TRANSFER.getType()).build();
        Response<TransactionView> response5 = controller.update(param.getTransactionId(), param5);
        Assertions.assertEquals(TransactionTypeEnum.TRANSFER.getType(), response5.getData().getType());
        TransactionParam param6 = TransactionParam.builder().transactionId(TRANSACTION_ID_TEST)
                .description("test update6").build();
        Response<TransactionView> response6 = controller.update(param.getTransactionId(), param6);
        Assertions.assertEquals("test update6", response6.getData().getDescription());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Test
    void delete() {
        log.info("test delete");
        Response<TransactionView> r = controller.get(TRANSACTION_ID_TEST2);
        Assertions.assertNotNull(r.getData());
        Response<TransactionView> response = controller.delete(TRANSACTION_ID_TEST2);
        Assertions.assertNotNull(response.getData());
        Assertions.assertTrue(response.getData().getDeleted());
    }

}
