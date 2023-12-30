# Solairelight

[![CircleCI](https://circleci.com/gh/alibaba/spring-cloud-alibaba/tree/2022.x.svg?style=svg)](https://circleci.com/gh/alibaba/spring-cloud-alibaba/tree/2022.x)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

Solairelight 是基于SpringFLux开发的WebSocket消息服务，支持单机以及集群部署（集群需要Redis）。

## 主要功能

* **提供消息广播接口**：Solairelight 提供了基于Rest接口，用于对建立连接的会话广播消息。
* **Session管理**：根据用户所属范围、特征对Session进行管理，在消息广播时可以根据以上因素进行定向广播。
* **用户端消息转发**：根据用户输入的消息、以及用户会话头信息进行路由转发（需进行二次开发，请看后续文档）。
* **可自定义的filter、event**：支持自定义filter以及注册自己的event。filter可对消息进行处理、转化、以及剔除非法消息，filter是同步且有序的。event 可以在特定的场景触发执行，event是异步执行且无序。
* **集群**：可集群部署多个节点，集群支持Redis。

## 基础要求

* Spring-Boot 2.7.18 </br>
* JDK 1.8 及以上

## 如何使用
在你的项目POM文件加入依赖。
```xml
<dependency>
    <groupId>cn.solairelight</groupId>
    <artifactId>solairelight-spring-boot-starter</artifactId>
    <version>1.0.0-alpha</version>
</dependency>
```
项目需要二次开发，类似于Spring-Gateway，你可以在已有的项目引入依赖，或者新开项目并引入依赖。
</br>可参考Solairelight-runner。


## 演示 Demo

暂无，后续会添加。

## 自定义Filter
因为项目是基于Spring开发的，所有实现自己的Filter也是非常的简单的。
<br>只需要实现特定的接口，然后将其注册为Spring Bean即可。
<br>**Filter 是同步的，请勿在Filter执行IO操作或者阻塞线程的操作，如需此类操作请使用Event。**
<br>目前提供了两种Filter：
* **MessageFilter**：应用于消息阶段，分类三种类型 none（默认，应用所有消息类型）, incoming（入站消息、例如：用户消息）, broadcast（广播消息）; 
<br>要自定义该类型Filter，非常简单，只需继承MessageFilter，同时设置MessageWay（上述三种之一）即可。
```java
@Component
public class IncomingMessageFilterImpl extends MessageFilter {

    public IncomingMessageFilterImpl() {
        super(MessageWay.incoming);
    }

    @Override
    public FilterContext<MessageWrapper> doFilter(FilterContext<MessageWrapper> filterContext) {
        System.out.println(filterContext.getPayload().isForwardable());
        log.info("message incoming filter. {}", filterContext);
        return FilterContext.pass(filterContext);
    }
}
```
FilterContext用于传递消息或者其他需要进行操作的对象，依据不同的种类Filter会有不同的payload（如果Filter执行成功则将payload传递给下一个Filter）。
<br>返回FilterContext.pass则代表成功，失败则返回FilterContext.abort。
<br>后续所有种类的Filter都是相同的操作，只是实现的接口或者继承的类不相同。

* **SessionFilter**：用于过滤非法的Session，以及对Session做额外的操作。
<br>只需简单的实现SessionFilter即可。
```java
@Component
public class SessionFilterImpl implements SessionFilter {
    @Override
    public FilterContext<BasicSession> doFilter(FilterContext<BasicSession> filterContext) {
        System.out.println(filterContext.getPayload().getSessionId());
        log.info("session filter. {}", filterContext);
        return FilterContext.pass(filterContext.getPayload());
    }
}
```
以上是两种Filter的实现方式。

## 注册Event
目前提供五种事件：Message消息入站事件、Broadcast广播事件、SessionConnected会话连接、SessionDisconnected会话断开连接、Global全局事件。
<br>所有种类的事件都是异步执行的，可以操作IO（尽量使用非阻塞）、阻塞线程（不建议，尽量减少阻塞时长）。
<br>事件是无序的，无法保证同种类的事件执行顺序，内部使用了较少的线程（cores*2），建议耗时操作使用Reactor以及类似的响应式类库。
* **Message**：实现MessageEvent接口，并将其注册为SpringBean。
* **Broadcast**：实现SessionConnectedEvent接口，并将其注册为SpringBean。
* **SessionConnected**：实现SessionDisconnectedEvent接口，并将其注册为SpringBean。
* **SessionDisconnected**：实现MessageEvent接口，并将其注册为SpringBean。
* **Global**：实现GlobalEvent接口，并将其注册为SpringBean。Global事件可以通过EventContext得知由何种事件触发。
```java
@Component
public class MessageEventImpl implements MessageEvent {

    @Override
    public void execute(EventContext<MessageWrapper> context) {
        System.out.println(context.getArgument().isForwardable());
        log.info("message event. {}", context);
    }
}
```