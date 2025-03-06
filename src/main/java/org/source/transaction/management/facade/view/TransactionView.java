package org.source.transaction.management.facade.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TransactionView {
    /**
     * 主键
     */
    private Long id;
    /**
     * 交易ID，唯一索引
     */
    private String transactionId;
    /**
     * 出方账号
     */
    private String fromAccount;
    /**
     * 入方账号
     */
    private String toAccount;
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 交易类型
     */
    private Integer type;
    /**
     * 描述
     */
    private String description;

    /**
     * 是否已删除
     */
    private Boolean deleted;
    /**
     * 创建时间
     * 创建之后不更新
     */
    private LocalDateTime createTime;
    /**
     * 创建人
     * 创建之后不更新
     */
    private String createUser;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 更新人
     */
    private String updateUser;
}
