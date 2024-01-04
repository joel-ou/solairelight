# Solairelight

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

Solairelight 是基于SpringFLux开发的WebSocket消息服务，支持单机以及集群部署（集群需要Redis）。

## 主要功能

* **提供消息广播接口**：Solairelight 提供了基于Rest接口，用于对建立连接的会话广播消息。
* **Session管理**：根据用户所属范围、特征对Session进行管理，在消息广播时可以根据以上因素进行定向广播。
* **用户端消息转发**：根据用户输入的消息、以及用户会话头信息进行路由转发（需进行二次开发，请看后续文档）。
* **可自定义的filter、event**：支持自定义filter以及注册自己的event。filter可对消息进行处理、转化、以及剔除非法消息，filter是同步且有序的。event 可以在特定的场景触发执行，event是异步执行且无序。
* **集群**：可集群部署多个节点，集群支持Redis。
* **连接请求转发**：当某个节点会话超过限制后，新的连接请求会自动转发到其他节点。

## 基础要求

* Spring-Boot 2.X.X</br>
* JDK 1.8 及以上

## 如何使用
在你的项目POM文件加入依赖。
```xml
<dependency>
    <groupId>io.github.joelou.solairelight</groupId>
    <artifactId>solairelight-spring-boot-starter</artifactId>
    <version>1.0.0-alpha</version>
</dependency>
```
项目需要二次开发，类似于Spring-Gateway，你可以在已有的项目引入依赖，或者新开项目并引入依赖。
</br>可参考Solairelight-runner。
<br>
<br>如果需要集群运行，则需要额外加入spring-boot-starter-data-redis依赖
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
    <version>2.7.18</version>
    <scope>compile</scope>
</dependency>
```

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

## 消息转发
为了兼容旧系统以及自定义的消息格式，Solairelight没有定义用户输入消息的格式。所以在进行消息转发之前需要对消息体进行解析以及填充消息特征用于转发时作为匹配条件。
<br><br>**为了实现该功能，可以参考以下代码**。
```java
@Component
public class MessageFeatureFilter extends MessageFilter {

    public MessageFeatureFilter() {
        super(MessageWay.incoming);
    }

    @Override
    public FilterContext<MessageWrapper> doFilter(FilterContext<MessageWrapper> filterContext) {
        MessageWrapper messageWrapper = filterContext.getPayload();
        WebSocketMessage webSocketMessage = ((WebSocketMessage) filterContext.getPayload().getRawMessage());
        Map<String, Object> features = new HashMap<>();

        //parse message. this just an example, do not use it directly.
        JsonParser jsonParser = new BasicJsonParser();
        String json = webSocketMessage.getPayload().toString(StandardCharsets.UTF_8);
        Map<String, Object> messageMap = jsonParser.parseMap(json);
        for (Map.Entry<String, Object> entry : messageMap.entrySet()) {
            if(entry.getKey().equals("sampleKey"))
                features.put(entry.getKey(), entry.getValue());
        }
        messageWrapper.setMessage(messageMap);
        messageWrapper.setFeatures(features);
        messageWrapper.setForwardable(true);
        return filterContext;
    }

    @Override
    public int order() {
        return 0;
    }
}
```
<br>你也可以将转发模块关掉，通过注册事件实现自己的转发功能，方法见文档的 “配置” 部分。

## 消息广播
消息广播可通过调用RestApi对区域内满足特定条件的用户进行广播，也可以全局广播。
消息广播有固定的格式，目前只支持JSON。
<br>**消息格式如下**：
```json
{
    "channel":"Websocket",
    "range": "node==1,node==2,node==3",
    "predicate": "isVip==true or isVip==false",
    "message": "dGhpcyBpcyBhIG1lc3NhZ2UgZm9yIGJheWVz",
    "id": 100
}
```
Channel：固定为Websocket
<br>Range：广播范围，例如某个端的Session，或者某个房间的Session。后续会说明如何定义一个Session的范围。
<br>Predicate：Session匹配规则，使用Spring EL表达式模块，数据输入源是Session Feature（后续会讲解）。
<br>Message：需要广播的原始消息，可以是字符串、Byte数组。Byte数组请Base64编码，广播给用户之前会进行解码。
<br>ID：该次广播的唯一ID，不可重复，重复的ID不会进行广播操作。
<br>
<br>
**RestApi地址：post host:port/solairelight/broadcast**
<br>
响应结果结构：
```json
{
  "success": true,
  "code": "success",
  "message": "success"
}
```

## 用户会话连接规范
* Web端因为无法支持自定义的Header，可将自定义的Header信息通过URL参数传入。
* 所有的Session建立连接时候都需要携带一个Token（JWT）Header，用于标识该Session所属范围，以及用户特征。
* 该Token的KEY为Metadata-Token，KEY Name可自定义。
<br>例如：Metadata-Token = eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMiwidXNlclJhbmdlcyI6eyJub2RlIjoxLCJyb29tIjo5OX0sInVzZXJGZWF0dXJlcyI6eyJsZXZlbCI6MTAsImlzVmlwIjpmYWxzZSwibmFtZSI6ImphY2sifX0.a-Nt-O2L_FGNA2LMDmNS05wrlzbsSfX76hSFwKdT9OFSgJ4g8iaMFQB_Br6oSEcAf6whxAt2kOUQFozNIjdzwA

<br>Token格式如下：
  ```json
  {
      "userRanges": {
        "node": 1,
        "room": 99
      },
      "userFeatures": {
        "level": 10,
        "isVip": false,
        "name": "jack"
      }
  }
  ```
UserRanges：用于定义Session所属的范围，例如用户所处客户端、以及所处位置。
用于消息广播时候的广播范围定义。
<br>UserFeatures：用于定义用户特征，例如是否VIP，名字，用户等级。用于在广播消息时对session进行过滤。

## 配置参数
```yaml
#SolairelightConfig example
solairelight:
  enable: true
  websocket-path: /solairelight
  cluster:
    enable: true #是否启用集群
    node-id-suffix: 1 #集群节点ID后缀
  secure:
    metadata-key: Metadata-Token
    #解析JWT只需要公钥
    public-key-base64: MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEEVs/o5+uQbTjL3chynL4wXgUg2R9q9UU8I5mEovUf86QZ7kOBIjJwqnzD1omageEHWwHdBO6B+dFabmdT9POxg==
  session:
    idle: 600 #seconds session闲置时间
    max-number: 20000 #单个节点最大可以建立的Session数量，0则不限制
  forward:
    enable: true #是否开启用户消息转发
    forwardHeader: Host #转发Header，会将Session建立时的Header信息进行转发。也可以新增Header，Key=Value格式即是定义新的，如果跟已有的冲突则以新的为准。
    routes:
      - uri: http://localhost:8081/example
        predicate:
          message: sampleKey=='v1' #转发条件1，对消息信息匹配
          session-header: h1==v1 #转发条件2，对session头进行匹配
          operator: or #上述两个条件的逻辑运算符。
```
集群节点ID后缀可以通过添加JAVA的执行参数设置 java -Dsolairelight.cluster.nodeIdSuffix=-99 
