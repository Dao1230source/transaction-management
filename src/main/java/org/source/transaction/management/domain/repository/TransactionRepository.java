package org.source.transaction.management.domain.repository;

import org.source.transaction.management.domain.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionEntity, String>,
        JpaSpecificationExecutor<TransactionEntity> {

    Optional<TransactionEntity> getByTransactionIdAndDeleted(String transactionId, Boolean deleted);

    List<TransactionEntity> findByTransactionIdInAndDeleted(Collection<String> transactionIds, Boolean deleted);

}
