package com.example.web3.mapper;

import com.example.web3.entity.DepositRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author jiangyuxuan
 */
@Mapper
public interface DepositMapper {

    int insert(DepositRecord record);

    DepositRecord findByTxHash(@Param("txHash") String txHash);

    List<DepositRecord> findByToAddress(@Param("address") String address,
                                         @Param("offset") Integer offset,
                                         @Param("limit") Integer limit);

    List<DepositRecord> findAll(@Param("offset") Integer offset,
                                 @Param("limit") Integer limit);
}

