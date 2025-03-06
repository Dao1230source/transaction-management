package org.source.transaction.management.facade.param;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.source.spring.valid.EnumExists;
import org.source.spring.valid.groups.Add;
import org.source.spring.valid.groups.Delete;
import org.source.spring.valid.groups.Update;
import org.source.transaction.management.infrastructure.enums.TransactionTypeEnum;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionParam {
    /**
     * 交易ID，唯一索引
     */
    @NotEmpty(groups = {Add.class, Delete.class, Update.class}, message = "交易ID(transactionId)不能为空")
    private String transactionId;
    /**
     * 出方账号
     */
    @Size(min = 5, max = 20, message = "出方账号(fromAccount)必须是5到20位")
    @NotEmpty(groups = {Add.class}, message = "出方账号(fromAccount)不能为空")
    private String fromAccount;
    /**
     * 入方账号
     */
    @Size(min = 5, max = 20, message = "入方账号(toAccount)必须是5到20位")
    @NotEmpty(groups = {Add.class}, message = "入方账号(toAccount)不能为空")
    private String toAccount;
    /**
     * 交易类型
     */
    @NotNull(groups = {Add.class}, message = "交易类型(type)不能为空")
    @EnumExists(clazz = TransactionTypeEnum.class, method = "getType", message = "交易类型(type)枚举值不正确")
    private Integer type;

    /**
     * 金额
     */
    @NotNull(groups = {Add.class}, message = "金额(amount)不能为空")
    @Positive(message = "金额(amount)必须大于零")
    private BigDecimal amount;

    /**
     * 描述
     */
    private String description;
}
