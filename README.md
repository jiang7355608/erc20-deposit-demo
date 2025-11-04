# ERC20 Token 充值监听系统

基于 Spring Boot + MyBatis + Web3j 实现的链上充值事件监听和记录系统。

## 技术栈

- Spring Boot 2.7.18
- MyBatis 2.3.1
- Web3j 4.9.8
- PostgreSQL

## 功能

- 实时监听 ERC20 合约的 Transfer 事件
- 解析并保存充值记录到 PostgreSQL
- 提供 RESTful API 查询充值历史



