package cn.muskmelon.runner;

import cn.muskmelon.config.MuskmelonAutoConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Joel Ou
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplicationBuilder()
                .sources(Application.class)
                .web(WebApplicationType.REACTIVE)
                .build();
        springApplication.run();
    }
}
