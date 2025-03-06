package org.source.transaction.management.app;

import com.google.common.hash.BloomFilter;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.source.transaction.management.domain.entity.TransactionEntity;
import org.source.transaction.management.domain.service.TransactionService;
import org.source.transaction.management.infrastructure.exception.BusinessExceptionEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
@Component
@AllArgsConstructor
public class TransactionApp {
    private final TransactionService transactionService;
    private final BloomFilter<String> bloomFilter;

    public TransactionEntity get(String transactionId) {
        // 一定不存在
        if (!bloomFilter.mightContain(transactionId)) {
            return null;
        }
        return transactionService.get(transactionId);
    }

    public Page<TransactionEntity> page(TransactionEntity entity, Pageable pageable) {
        return transactionService.page(entity, pageable);
    }

    public TransactionEntity add(TransactionEntity entity) {
        TransactionEntity fromDb = transactionService.get(entity.getTransactionId());
        BusinessExceptionEnum.RECORD_HAS_EXISTS.isNull(fromDb, "transactionId:{}", entity.getTransactionId());
        entity.setCreateTime(LocalDateTime.now());
        entity.setCreateUser("createUser");
        entity.setUpdateTime(LocalDateTime.now());
        entity.setUpdateUser("updateUser");
        entity.setDeleted(Boolean.FALSE);
        TransactionEntity save = transactionService.save(entity);
        // 加入布隆过滤器中
        bloomFilter.put(entity.getTransactionId());
        return save;
    }

    public TransactionEntity update(TransactionEntity entity) {
        TransactionEntity fromDb = transactionService.get(entity.getTransactionId());
        BusinessExceptionEnum.RECORD_NOT_FOUND.nonNull(fromDb, "transactionId:{}", entity.getTransactionId());
        if (StringUtils.isNotBlank(entity.getFromAccount())) {
            fromDb.setFromAccount(entity.getFromAccount());
        }
        if (StringUtils.isNotBlank(entity.getToAccount())) {
            fromDb.setToAccount(entity.getToAccount());
        }
        if (Objects.nonNull(entity.getAmount())) {
            fromDb.setAmount(entity.getAmount());
        }
        if (Objects.nonNull(entity.getType())) {
            fromDb.setType(entity.getType());
        }
        if (StringUtils.isNotBlank(entity.getDescription())) {
            fromDb.setDescription(entity.getDescription());
        }
        fromDb.setUpdateUser("updateUser");
        fromDb.setUpdateTime(LocalDateTime.now());
        return transactionService.save(fromDb);
    }

    public TransactionEntity delete(String transactionId) {
        return transactionService.delete(transactionId);
    }

}
