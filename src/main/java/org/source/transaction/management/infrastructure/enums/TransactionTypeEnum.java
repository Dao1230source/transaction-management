package org.source.transaction.management.infrastructure.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionTypeEnum {
    TRANSFER(0,"转账"),
    RED_PACKET(1,"红包"),
    ;
    private final Integer type;
    private final String description;

}
