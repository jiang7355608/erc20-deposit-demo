package com.example.web3.listener;

import com.example.web3.config.Web3jConfig;
import com.example.web3.entity.DepositRecord;
import com.example.web3.service.DepositService;
import io.reactivex.disposables.Disposable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Log;

import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * Transfer事件监听
 * @author jiangyuxuan
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenTransferListener implements CommandLineRunner {

    private final Web3j web3j;
    private final Web3jConfig web3jConfig;
    private final DepositService depositService;

    private Disposable subscription;

    private static final Event TRANSFER_EVENT = new Event("Transfer",
            Arrays.asList(
                    new TypeReference<Address>(true) {},
                    new TypeReference<Address>(true) {},
                    new TypeReference<Uint256>(false) {}
            ));

    private static final String TRANSFER_EVENT_SIGNATURE = EventEncoder.encode(TRANSFER_EVENT);

    // FIXME 先写死，后面可以调合约的decimals()拿
    private static final int TOKEN_DECIMALS = 6;

    @Override
    public void run(String... args) {
        log.info("启动监听器...");
        startListening();
    }

    public void startListening() {
        try {
            String contractAddress = web3jConfig.getToken().getContractAddress();
            String contractName = web3jConfig.getToken().getContractName();

            log.info("开始监听 {} - {}", contractName, contractAddress);

            EthFilter filter = new EthFilter(
                    DefaultBlockParameterName.LATEST,
                    DefaultBlockParameterName.LATEST,
                    contractAddress
            );
            filter.addSingleTopic(TRANSFER_EVENT_SIGNATURE);

            subscription = web3j.ethLogFlowable(filter).subscribe(
                    this::handleTransferEvent,
                    error -> {
                        log.error("监听出错: {}", error.getMessage(), error);
                        // TODO 加重连机制
                    }
            );

            log.info("监听启动成功");

        } catch (Exception e) {
            log.error("启动失败: {}", e.getMessage(), e);
        }
    }

    private void handleTransferEvent(Log eventLog) {
        try {
            // TODO 区块确认数检查，避免链重组
            
            List<String> topics = eventLog.getTopics();
            if (topics.size() < 3) {
                log.warn("topics数量不对: {}", topics.size());
                return;
            }

            String fromAddress = decodeAddress(topics.get(1));
            String toAddress = decodeAddress(topics.get(2));
            
            String data = eventLog.getData();
            BigInteger value = new BigInteger(data.substring(2), 16);

            BigDecimal amountDecimal = new BigDecimal(value)
                    .divide(BigDecimal.TEN.pow(TOKEN_DECIMALS));

            EthBlock.Block block = web3j.ethGetBlockByNumber(
                    org.web3j.protocol.core.DefaultBlockParameter.valueOf(
                            BigInteger.valueOf(eventLog.getBlockNumber().longValue())
                    ),
                    false
            ).send().getBlock();

            Long timestamp = block != null ? block.getTimestamp().longValue() : System.currentTimeMillis() / 1000;

            DepositRecord record = DepositRecord.builder()
                    .txHash(eventLog.getTransactionHash())
                    .blockNumber(eventLog.getBlockNumber().longValue())
                    .blockHash(eventLog.getBlockHash())
                    .contractAddress(eventLog.getAddress())
                    .fromAddress(fromAddress)
                    .toAddress(toAddress)
                    .amount(value.toString())
                    .amountDecimal(amountDecimal)
                    .decimals(TOKEN_DECIMALS)
                    .logIndex(eventLog.getLogIndex().intValue())
                    .transactionIndex(eventLog.getTransactionIndex().intValue())
                    .timestamp(timestamp)
                    .build();

            log.info("Transfer事件 - tx: {}, from: {}, to: {}, amount: {}",
                    record.getTxHash(), record.getFromAddress(), 
                    record.getToAddress(), record.getAmountDecimal());

            depositService.saveDepositRecord(record);

        } catch (Exception e) {
            log.error("处理事件失败: {}", e.getMessage(), e);
        }
    }

    // topic格式: 0x000000000000000000000000{address}
    private String decodeAddress(String topic) {
        if (topic.length() < 66) {
            return topic;
        }
        return "0x" + topic.substring(26);
    }

    @PreDestroy
    public void stopListening() {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
            log.info("监听器已停止");
        }
    }
}

