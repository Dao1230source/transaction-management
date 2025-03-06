## 银行交易管理系统

### 功能特性

银行系统内的交易数据管理系统，支持查看、新增、修改和删除交易数据。

### 快速开始

必要准备

- 安装 jdk21
- 安装 docker
- 使用 maven 管理依赖

#### 本地部署

- 进入项目根目录 `cd {your path}/transaction-management`
- docker构建 `docker buildx build -t transaction-management:latest .`
- docker运行 `docker run -p 8080:8080 transaction-management:latest`

访问路径 http://localhost:8080/index.html

#### 云上部署

访问路径：

### 技术栈

- 前端: Html
- 后端: Spring Boot:3.4.3,spring-cloud:2024.0.0,其他有自己写的jar
    - `io.github.dao1230source:web-extension-starter:0.0.11` 用于统一异常处理和统一接口返回包装，
      使系统返回数据格式统一变为 `org.source.spring.io.Response`，便于与其他系统交互
    - `io.github.dao1230source:spring-extension-starter:0.0.12` 是对spring cache的扩展，支持批量缓存和二级缓存
- 数据库: H2内存数据库
- 部署: Docker

### 配置说明

- `bootstrap.yml`为公用配置
- `application-dev.yml` 开发环境配置
- `application-prod.yml` 生产环境配置  
  **备注：** 这里只实现了dev的配置

### 项目结构

项目依据DDD的思路设计项目结构

```text
project-root/
├── src/                #   
│   ├── app             # 应用层，处理业务逻辑
│   ├── controllers     # 控制器，web接口  
│   ├── domain          # 数据模型 
│   │   ├── entity      # 实体类
│   │   ├── repository  # 数据库接口
│   │   └── service     # 数据库逻辑处理，主要是添加缓存、事物等
│   ├── facade          # 门面，用于外部数据和数据库数据转换 
│   │   ├── mapper      # 转换，mapstruct转换 
│   │   ├── param       # 参数，外部请求参数 
│   │   └── view        # 视图，返回给外部的数据 
│   └── infrastructure  # 基础建设 
│       ├── enums       # 枚举
│       ├── exception   # 异常
│       ├── config      # 配置
│       ├── constant    # 常量
├── tests/              # 测试用例  
└── ...
```

### API

- 遵循RESTful API设计原则
- http status 200表示成功，500表示系统异常
- 参数通过`spring-boot-starter-validation`校验
- 返回值由`org.source.web.unified.UnifiedResponseAdvice`处理统一包装为 `org.source.spring.io.Response`
    - 执行成功时返回，success=true
    ```json
    {
        "code": "SUCCESS",
        "message": "执行成功",
        "timestamp": "2025-03-06T13:10:44.353952632",
        "data": {},
        "success": true
    }
    ```
    - 执行失败时返回，success=false，code为错误码，message为错误信息，extraMessage为业务补充信息
    ```json
    {
        "code": "RECORD_HAS_EXISTS",
        "message": "记录已存在",
        "extraMessage": "transactionId:t00000-0001",
        "timestamp": "2025-03-06T13:10:32.61620521",
        "success": false
    }
    ```
- 异常由`org.source.web.unified.UnifiedExceptionHandler`统一转换为`org.source.spring.io.Response`
- 自定义异常枚举 `BusinessExceptionEnum`

#### **接口1**：根据交易ID查询

- url:`GET /transaction/{transactionId}`
- 请求体：
    - transactionId 交易ID 必填
- 返回值：`Response<TransactionView>`

#### **接口2**：分页查询

- url:`GET /transaction/page`
- 请求体：
    - pageNumber 当前页数默认0 可填
    - pageSize 每页大小默认10 可填
    - transactionId 交易ID 可填
    - fromAccount 出方账号 可填
    - toAccount 入方账号 可填
    - type 交易类型 可填
- 返回值：`Response<TransactionView>`

#### **接口3**：新增

- url:`POST /transaction/add`
- 请求体：
    - TransactionParam 交易参数 必填
        - transactionId 交易ID 必填
        - fromAccount 出方账号 必填
        - toAccount 入方账号 必填
        - type 交易类型 必填
        - amount 金额 必填
        - description 描述 可填
- 返回值：`Response<TransactionView>`

#### **接口4**：修改

- url:`PUT /transaction/{transactionId}`
- 请求体：
    - transactionId 交易ID 必填
    - TransactionParam 交易参数 必填
        - transactionId 交易ID 必填
        - fromAccount 出方账号 可填
        - toAccount 入方账号 可填
        - type 交易类型 可填
        - amount 金额 可填
        - description 描述 可填
- 返回值：`Response<TransactionView>`
- 此处是否应使用PUT存疑

#### **接口5**：根据交易ID删除

- url:`DELETE /transaction/{transactionId}`
- 请求体：
    - transactionId 交易ID 必填
- 返回值：`Response<TransactionView>`
- 此处是否应使用PUT存疑

### 性能优化

#### 数据库优化

- `transaction_id`字段唯一索引`@Index(name = "uk_transaction_id", columnList = "transaction_id", unique = true)`
- `update_time`字段倒序索引`@Index(name = "idx_update_time_desc", columnList = "update_time DESC")`

#### 缓存优化

由`spring-extension-starter`缓存模块处理

- 内容介绍详见 https://github.com/Dao1230source/spring-extension-starter/blob/main/spring-cache.md
- Demo详见 https://github.com/Dao1230source/demo/tree/main/spring-extension-starter

交易数据缓存再两个cacheName中
- 单条数据缓存在`cacheName=TRANSACTION`中
- 分页查询的数据缓存在`cacheName=TRANSACTION_PAGE`中
- **保存**操作时删除`cacheName=TRANSACTION`中`key=transactionId`的数据和`cacheName=TRANSACTION_PAGE`中所有数据
- **删除**操作时删除`cacheName=TRANSACTION`中`key=transactionId`的数据和`cacheName=TRANSACTION_PAGE`中所有数据

#### 布隆过滤器优化

根据`transactionId`查询时如果通过布隆过滤器判断不存在，直接返回null，减少对缓存和数据库的访问压力

### 单元测试

使用Junit进行单元测试
代码覆盖率如下图，其中除 `mapstruct`的实现类之外的业务代码覆盖率均是`100%`
![单元测试代码覆盖率.png](other/img/%E5%8D%95%E5%85%83%E6%B5%8B%E8%AF%95%E4%BB%A3%E7%A0%81%E8%A6%86%E7%9B%96%E7%8E%87.png)

### 压力测试

压测工具：Jmeter    
线程组配置:
![压测线程组配置.png](other/img/%E5%8E%8B%E6%B5%8B%E7%BA%BF%E7%A8%8B%E7%BB%84%E9%85%8D%E7%BD%AE.png)
分别配置单条查询get、分页查询page和新增add三个接口
压测结果：
![压测结果.png](other/img/%E5%8E%8B%E6%B5%8B%E7%BB%93%E6%9E%9C.png)
详细数据 [result.zip](other/result.zip)

docker容器指标统计
![docker容器指标统计.png](other/img/docker%E5%AE%B9%E5%99%A8%E6%8C%87%E6%A0%87%E7%BB%9F%E8%AE%A1.png)
