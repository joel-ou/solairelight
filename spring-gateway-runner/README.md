# Solairelight-Gateway

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

网关部分用于拓展solairelight的集群功能。 例如： 集群消息广播，WebSocket的节点分配。当然这些你都可以自行实现，这个模块更多的是提供一种参考。
## 如何使用
在你的Spring-Gateway项目POM文件加入依赖。
```xml
<dependency>
    <groupId>io.github.joelou.solairelight</groupId>
    <artifactId>solairelight-cluster</artifactId>
    <version>1.0.0-alpha</version>
</dependency>
```
然后在你的Configure配置类引入SolairelightGatewayConfiguration
<br>例如：
```java
@Configuration
@Import(SolairelightGatewayConfiguration.class)
public class GatewayConfig {
}
```
然后在你的网关配置文件上添加相应的路由配置即可
``` xml
spring:
  cloud:
    gateway:
      routes:
        - id: solairelight-broadcast-route
          uri: solaire://broadcast
          predicates:
          - Path=/solairelight/broadcast
        - id: solairelight-websocket-route
          uri: lb:ws://solairelight
          predicates:
          - Path=/solairelight
```
## work flow

![workflow.png](..%2Fworkflow.png)