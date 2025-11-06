package com.example.web3.config;

import lombok.Data;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.concurrent.TimeUnit;

/**
 * Web3j配置
 * @author jiangyuxuan
 */
@Configuration
@ConfigurationProperties(prefix = "web3j")
@Data
@Validated
public class Web3jConfig {

    @NotBlank(message = "RPC URL 不能为空")
    private String rpcUrl;

    @NotNull(message = "链 ID 不能为空")
    private Long chainId;

    private Long httpTimeout = 60000L;
    
    private Long websocketTimeout = 60000L;

    private TokenConfig token = new TokenConfig();

    @Data
    public static class TokenConfig {
        @NotBlank(message = "合约地址不能为空")
        private String contractAddress;
        
        private String contractName = "ERC20";
        
        private String startBlock = "latest";
        
        private Long pollingInterval = 3000L;
        
        /**
         * 区块确认数：防止链重组
         * 建议值：测试网 3-6，主网 12-32
         */
        private Integer confirmations = 3;
    }

    @Bean
    public Web3j web3j() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(httpTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(httpTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(httpTimeout, TimeUnit.MILLISECONDS)
                // 添加重试机制
                .retryOnConnectionFailure(true)
                // 添加连接池配置
                .connectionPool(new okhttp3.ConnectionPool(5, 5, TimeUnit.MINUTES))
                .build();

        HttpService httpService = new HttpService(rpcUrl, okHttpClient);
        // 设置包含异常详情
        httpService.addHeader("User-Agent", "Web3j-Spring-Boot-App/1.0");
        
        return Web3j.build(httpService);
    }
}

