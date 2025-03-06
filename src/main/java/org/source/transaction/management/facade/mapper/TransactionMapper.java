package org.source.transaction.management.facade.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.source.transaction.management.domain.entity.TransactionEntity;
import org.source.transaction.management.facade.param.TransactionParam;
import org.source.transaction.management.facade.view.TransactionView;
import org.source.utility.mapstruct.ThreeMapper;

@Mapper
public interface TransactionMapper extends ThreeMapper<TransactionParam, TransactionEntity, TransactionView> {

    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

}
