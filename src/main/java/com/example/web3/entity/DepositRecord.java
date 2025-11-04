package com.example.web3.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author jiangyuxuan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositRecord {

    private Long id;
    
    private String txHash;
    
    private Long blockNumber;
    
    private String blockHash;
    
    private String contractAddress;
    
    private String fromAddress;
    
    private String toAddress;
    
    // 原始值
    private String amount;
    
    // 转换后的实际值
    private BigDecimal amountDecimal;
    
    private Integer decimals;
    
    private Integer logIndex;
    
    private Integer transactionIndex;
    
    private Long timestamp;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}

