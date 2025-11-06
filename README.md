# ERC20 Token 充值监听系统

基于 Spring Boot + MyBatis + Web3j 实现的链上充值事件监听和记录系统。
https://github.com/jiang7355608/erc20-deposit-demo
## 技术栈

- Spring Boot 2.7.18
- MyBatis 2.3.1
- Web3j 4.9.8
- PostgreSQL

## 功能

- 实时监听 ERC20 合约的 Transfer 事件
- 解析并保存充值记录到 PostgreSQL
- 提供 RESTful API 查询充值历史

# 操作流程
1.注册MetaMask，获取自己的钱包地址
2.到https://faucet.metana.io/# (或者其他水龙头，这个可以无需主网资产)领取测试SepoliaETH,
主要用来支付部署合约，转账的gas
3.使用remix(https://remix.ethereum.org/)部署智能合约并发布，注意链接自己的钱包选择Sepolia网络，
然后得到合约地址，本项目使用的Solidity代码见项目根目录的[MyToken.sol]
4.MetaMask里面切换至Sepolia网络,导入代币，这个地方有点坑，有时候导入成功但是看不到，多试几次
5.代币导入成功后新建一个MetaMask账户，然后就可以测试两个钱包相互转账并触发本项目的监听

