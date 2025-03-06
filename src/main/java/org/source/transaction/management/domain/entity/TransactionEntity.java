package org.source.transaction.management.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "transaction", indexes = {
        @Index(name = "uk_transaction_id", columnList = "transaction_id", unique = true),
        @Index(name = "idx_update_time_desc", columnList = "update_time DESC")
})
public class TransactionEntity {
    /**
     * 主键
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    /**
     * 交易ID，唯一索引
     */
    @Column(name = "transaction_id")
    private String transactionId;
    /**
     * 出方账号
     */
    @Column(name = "from_account")
    private String fromAccount;
    /**
     * 入方账号
     */
    @Column(name = "to_account")
    private String toAccount;
    /**
     * 金额
     */
    @Column(name = "amount")
    private BigDecimal amount;
    /**
     * 交易类型
     */
    @Column(name = "type")
    private Integer type;
    /**
     * 描述
     */
    @Column(name = "description")
    private String description;

    /**
     * 是否已删除
     */
    @Column(name = "deleted")
    private Boolean deleted;
    /**
     * 创建时间
     * 创建之后不更新
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;
    /**
     * 创建人
     * 创建之后不更新
     */
    @Column(name = "create_user")
    private String createUser;
    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    /**
     * 更新人
     */
    @Column(name = "update_user")
    private String updateUser;
}
