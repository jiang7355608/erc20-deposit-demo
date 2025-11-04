package com.example.web3.controller;

import com.example.web3.entity.DepositRecord;
import com.example.web3.service.DepositService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 充值记录查询接口
 * @author jiangyuxuan
 */
@RestController
@RequestMapping("/api/deposits")
@RequiredArgsConstructor
public class DepositController {

    private final DepositService depositService;

    /**
     * 根据地址查询充值记录
     */
    @GetMapping("/address/{address}")
    public ApiResponse<List<DepositRecord>> getDepositsByAddress(
            @PathVariable String address,
            @RequestParam(defaultValue = "100") int limit) {
        
        List<DepositRecord> records = depositService.getDepositsByAddress(address, limit);
        return ApiResponse.success(records);
    }

    /**
     * 查询最近的充值记录
     */
    @GetMapping
    public ApiResponse<List<DepositRecord>> getRecentDeposits(
            @RequestParam(defaultValue = "100") int limit) {
        
        List<DepositRecord> records = depositService.getRecentDeposits(limit);
        return ApiResponse.success(records);
    }

    /**
     * 根据交易hash查询充值记录
     */
    @GetMapping("/tx/{txHash}")
    public ApiResponse<DepositRecord> getDepositByTxHash(@PathVariable String txHash) {
        DepositRecord record = depositService.getDepositByTxHash(txHash);
        if (record == null) {
            return ApiResponse.error("记录不存在");
        }
        return ApiResponse.success(record);
    }

    /**
     * 统一响应格式
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiResponse<T> {
        private int code;
        private String message;
        private T data;

        public static <T> ApiResponse<T> success(T data) {
            return new ApiResponse<>(200, "success", data);
        }

        public static <T> ApiResponse<T> error(String message) {
            return new ApiResponse<>(500, message, null);
        }
    }
}

