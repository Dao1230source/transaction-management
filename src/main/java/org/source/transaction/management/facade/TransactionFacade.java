package org.source.transaction.management.facade;

import lombok.AllArgsConstructor;
import org.source.transaction.management.app.TransactionApp;
import org.source.transaction.management.facade.mapper.TransactionMapper;
import org.source.transaction.management.facade.param.TransactionParam;
import org.source.transaction.management.facade.view.TransactionView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TransactionFacade {
    private final TransactionApp transactionApp;

    public TransactionView get(String transactionId) {
        return TransactionMapper.INSTANCE.y2z(transactionApp.get(transactionId));
    }

    public Page<TransactionView> page(TransactionParam param, Pageable pageable) {
        return transactionApp.page(TransactionMapper.INSTANCE.x2y(param), pageable).map(TransactionMapper.INSTANCE::y2z);
    }

    public TransactionView add(TransactionParam param) {
        return TransactionMapper.INSTANCE.y2z(transactionApp.add(TransactionMapper.INSTANCE.x2y(param)));
    }

    public TransactionView update(String transactionId, TransactionParam param) {
        param.setTransactionId(transactionId);
        return TransactionMapper.INSTANCE.y2z(transactionApp.update(TransactionMapper.INSTANCE.x2y(param)));
    }

    public TransactionView delete(String transactionId) {
        return TransactionMapper.INSTANCE.y2z(transactionApp.delete(transactionId));
    }

}
