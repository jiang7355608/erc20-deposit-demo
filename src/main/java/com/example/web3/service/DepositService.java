package com.example.web3.service;

import com.example.web3.entity.DepositRecord;
import com.example.web3.mapper.DepositMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 充值服务
 * @author jiangyuxuan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DepositService {

    private final DepositMapper depositMapper;

    /**
     * 保存充值记录（带幂等性检查）
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveDepositRecord(DepositRecord record) {
        try {
            // TODO 后续考虑加分布式锁
            DepositRecord existing = depositMapper.findByTxHash(record.getTxHash());
            if (existing != null) {
                log.info("记录已存在，跳过: {}", record.getTxHash());
                return;
            }

            int rows = depositMapper.insert(record);
            if (rows > 0) {
                log.info("保存成功 - txHash: {}, from: {}, to: {}, amount: {}",
                        record.getTxHash(), record.getFromAddress(), 
                        record.getToAddress(), record.getAmountDecimal());
            }
        } catch (Exception e) {
            log.error("保存失败: {}", record.getTxHash(), e);
            throw e;
        }
    }

    /**
     * 根据地址查询充值记录
     */
    public List<DepositRecord> getDepositsByAddress(String address, int limit) {
        return depositMapper.findByToAddress(address, 0, limit);
    }

    /**
     * 查询最近的充值记录
     */
    public List<DepositRecord> getRecentDeposits(int limit) {
        return depositMapper.findAll(0, limit);
    }

    /**
     * 根据交易hash查询
     */
    public DepositRecord getDepositByTxHash(String txHash) {
        return depositMapper.findByTxHash(txHash);
    }
}

