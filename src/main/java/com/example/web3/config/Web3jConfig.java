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
    }

    @Bean
    public Web3j web3j() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(httpTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(httpTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(httpTimeout, TimeUnit.MILLISECONDS)
                .build();

        return Web3j.build(new HttpService(rpcUrl, okHttpClient));
    }
}

