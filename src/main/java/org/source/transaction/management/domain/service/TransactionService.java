package org.source.transaction.management.domain.service;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.source.jpa.enhance.Condition;
import org.source.spring.cache.configure.CacheInJvm;
import org.source.spring.cache.configure.CacheInRedis;
import org.source.spring.cache.configure.ConfigureCache;
import org.source.spring.expression.VariableConstants;
import org.source.transaction.management.domain.entity.TransactionEntity;
import org.source.transaction.management.domain.repository.TransactionRepository;
import org.source.transaction.management.infrastructure.constant.CacheNames;
import org.source.transaction.management.infrastructure.constant.Constants;
import org.source.transaction.management.infrastructure.exception.BusinessExceptionEnum;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    /**
     * <pre>
     *  注解{@code @ConfigureCache}是{@code @Cacheable}的扩展，支持批量操作以及二级缓存
     *  默认 redis=true，jvm=false
     * </pre>
     * 这里只使用本地缓存，不开启redis缓存
     *
     * @param transactionId transactionId
     * @return TransactionEntity
     */
    @ConfigureCache(cacheNames = CacheNames.TRANSACTION, key = "#transactionId",
            cacheInJvm = @CacheInJvm(enable = true, jvmMaxSize = Constants.CACHE_JVM_MAX_SIZE),
            cacheInRedis = @CacheInRedis(enable = false))
    public TransactionEntity get(String transactionId) {
        return transactionRepository.getByTransactionIdAndDeleted(transactionId, Boolean.FALSE).orElse(null);
    }

    @ConfigureCache(cacheNames = CacheNames.TRANSACTION_PAGE,
            key = "T(org.source.transaction.management.domain.service.TransactionService).generatePageKey(#entity, #pageable)",
            cacheInJvm = @CacheInJvm(enable = true, jvmMaxSize = Constants.CACHE_JVM_MAX_SIZE),
            cacheInRedis = @CacheInRedis(enable = false))
    public Page<TransactionEntity> page(TransactionEntity entity, Pageable pageable) {
        Condition<TransactionEntity> condition = new Condition<TransactionEntity>()
                .eq(TransactionEntity::getDeleted, Boolean.FALSE);
        // 这应该将判空判断提取为方法，但尚未实现
        if (StringUtils.isNotBlank(entity.getTransactionId())) {
            condition.eq(TransactionEntity::getTransactionId, entity.getTransactionId());
        }
        if (StringUtils.isNotBlank(entity.getFromAccount())) {
            condition.eq(TransactionEntity::getFromAccount, entity.getFromAccount());
        }
        if (StringUtils.isNotBlank(entity.getToAccount())) {
            condition.eq(TransactionEntity::getToAccount, entity.getToAccount());
        }
        if (Objects.nonNull(entity.getType())) {
            condition.eq(TransactionEntity::getType, entity.getType());
        }
        return transactionRepository.findAll(condition.getSpecification(), pageable);
    }

    public static String generatePageKey(TransactionEntity entity, Pageable pageable) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(pageable.getPageSize());
        stringBuilder.append("-").append(pageable.getPageNumber());
        if (StringUtils.isNotBlank(entity.getTransactionId())) {
            stringBuilder.append("-").append(entity.getTransactionId());
        }
        if (StringUtils.isNotBlank(entity.getFromAccount())) {
            stringBuilder.append("-").append(entity.getFromAccount());
        }
        if (StringUtils.isNotBlank(entity.getToAccount())) {
            stringBuilder.append("-").append(entity.getToAccount());
        }
        if (Objects.nonNull(entity.getType())) {
            stringBuilder.append("-").append(entity.getType());
        }
        return stringBuilder.toString();
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.TRANSACTION, key = "#entity.transactionId"),
            @CacheEvict(cacheNames = CacheNames.TRANSACTION_PAGE, allEntries = true)
    })
    public TransactionEntity save(TransactionEntity entity) {
        return transactionRepository.save(entity);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.TRANSACTION, key = "#transactionId"),
            @CacheEvict(cacheNames = CacheNames.TRANSACTION_PAGE, allEntries = true)
    })
    public TransactionEntity delete(String transactionId) {
        TransactionEntity fromDb = transactionRepository.getByTransactionIdAndDeleted(transactionId, Boolean.FALSE)
                .orElseThrow(() -> BusinessExceptionEnum.RECORD_NOT_FOUND.except("transactionId:{}", transactionId));
        fromDb.setDeleted(Boolean.TRUE);
        return transactionRepository.save(fromDb);
    }
}
