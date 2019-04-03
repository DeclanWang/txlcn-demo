#### [分布式事务基础知识分享](https://github.com/DeclanWang/txlcn-demo/blob/master/distributed-transaction.md)

------

## 一、TX-LCN简介

> TX-LCN定位于一款事务协调性框架，**框架其本身并不操作事务**，而是基于对事务的协调从而达到事务一致性的效果。



LCN的命名含义：

- 锁定事务单元（lock）
- 确认事务模块状态(confirm)
- 通知事务(notify)



框架提供三种事务模式：

1. ***LCN***：通过代理Connection的方式实现对本地事务的操作，然后在由TxManager统一协调控制事务。当本地事务提交回滚或者关闭连接时将会执行假操作，该代理的连接将由LCN连接池管理。
2. ***TXC***：命名来源于淘宝，实现原理是在执行SQL之前，先查询SQL的影响数据，然后保存执行的SQL快走信息和创建锁。当需要回滚的时候就采用这些记录数据回滚数据库，目前锁实现依赖redis分布式锁控制。
3. ***TCC***：相对于传统事务机制（X/Open XA Two-Phase-Commit），其特征在于它不依赖资源管理器(RM)对XA的支持，而是通过对（由业务系统提供的）业务逻辑的调度来实现分布式事务。主要由三步操作，Try: 尝试执行业务、 Confirm:确认执行业务、 Cancel: 取消执行业务。



项目官网：https://www.txlcn.org/zh-cn/index.html





## 二、框架基本原理

TX-LCN由两大模块组成, **TxClient**、**TxManager**：

1. TxClient作为模块的依赖框架，提供TX-LCN的标准支持；
2. TxManager作为分布式事务的控制放。事务发起方或者参与反都由TxClient端来控制；

![theory](https://www.txlcn.org/img/docs/yuanli.png)



### 核心步骤

- 创建事务组
  是指在事务发起方**开始执行业务代码之前**先调用TxManager创建事务组对象，然后拿到事务标示GroupId的过程。
- 加入事务组
  添加事务组是指**参与方在执行完业务方法以后**，将该模块的事务信息通知给TxManager的操作。
- 通知事务组
  是指在**发起方执行完业务代码以后**，将发起方执行结果状态通知给TxManager,TxManager将根据事务最终状态和事务组的信息来通知相应的参与模块提交或回滚事务，并返回结果给事务发起方。





## 三、部署依赖

说明：该Demo基于SringCloud框架，相关部署环境依赖如下：

- JDK 8+
- Maven
- Redis
- Mysql
- Consul



## 四、启动步骤

1. 执行脚本txlcn.sql（该脚本放置在db文件夹中）初始化数据库；
2. 启动Redis；
3. 启动consul；
4. 启动（TM模块）txlcn-demo-tm；
5. 启动（相关TC模块）txlcn-demo-spring-service-a、txlcn-demo-spring-service-b、txlcn-demo-spring-service-c；



## 五、运行测试

在所有服务正常启动之后，

1. 正常提交事务
   访问发起方提供的Rest接口 `/txlcn?value=the-value`。发现事务全部提交
   ![result](https://www.txlcn.org/img/docs/result.png)

2. 回滚事务
   访问发起方提供的Rest接口 `/txlcn?value=the-value&ex=throw`。发现发起方由本地事务回滚，而参与方ServiceB、ServiceC，由于TX-LCN的协调，数据也回滚了

   ![error](https://www.txlcn.org/img/docs/error_result.png)
