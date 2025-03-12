# Warp Exchange

- parent: 管理版本和依赖
- ui: 前端ui界面
- build: 把所有模块放到一起编译
- config-repo: 存储Spring Cloud Config 服务器端的配置文件
- config: 配置服务器,负责读取所有配置
- trading-engine: 交易引擎包含：资产，订单，撮合引擎，清算功能
- trading-sequencer: 定序系统：完成订单排序，给每个订单请求一个全局唯一的递增序列号

## 技术方案：
- SpringBoot 3.x
- 微服务 SpringCloud 2023.0.0
- 数据库 MySQL 8.x
- 消息系统 Kafaka 3.x
- 缓存系统 Redis 6.x