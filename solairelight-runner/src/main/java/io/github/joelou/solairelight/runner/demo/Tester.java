package io.github.joelou.solairelight.runner.demo;

import io.github.joelou.solairelight.util.CapacityLimitLinkedList;

import java.util.LinkedList;
import java.util.Random;

/**
 * @author Joel Ou
 */
public class Tester {

//    private static final WebClient webClient;

    private static CapacityLimitLinkedList<Integer> list = new CapacityLimitLinkedList<>(100);
    private static LinkedList<Integer> linkedList = new LinkedList<>();

//    static {
//        HttpClient httpClient = HttpClient.create()
//                .keepAlive(true)
//                .responseTimeout(Duration.ofSeconds(2))
//                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
//                .option(ChannelOption.MAX_MESSAGES_PER_WRITE, 1000);
//        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
//        webClient = WebClient.builder().clientConnector(connector).build();
//    }

    public static void main(String[] args) throws InterruptedException {
//        BroadcastParam param = new BroadcastParam();
//        param.setId("900");
//        param.setChannel("Websocket");
//        param.setMessage("dGVzdGluZyBieXRlIG1lc3NhZ2U");
//        param.setRange("node==1");
//        param.setPredicate("name=='jack'");
//        for (int i = 0; i < 100; i++) {
//            new Thread(()->{
//                webClient.post()
//                        .uri("http://127.0.0.1:8081/solairelight/broadcast")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .bodyValue(param)
//                        .header("Content-Type", "application/json")
//                        .retrieve()
//                        .toEntity(String.class)
//                        .subscribe(r->{
//                            System.out.println(r.toString());
//                        });
//            }).start();
//        }
//        while (true);

        for (int i = 0; i < 130; i++) {
            int finalI = i;
            new Thread(()->{
                try {
                    Thread.sleep(new Random().nextInt(5)+1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                list.add(finalI+1);
//                linkedList.add(finalI+1);
            }).start();
        }
        Thread.sleep(10000L);
        System.out.println("size_"+list.getList().size());
        list.getList().stream().sorted().forEach(System.out::println);
//        System.out.println("size_"+linkedList.size());
//        linkedList.stream().sorted().forEach(System.out::println);
    }
}
