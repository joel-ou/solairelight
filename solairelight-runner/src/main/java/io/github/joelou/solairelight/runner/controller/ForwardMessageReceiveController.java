package io.github.joelou.solairelight.runner.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Joel Ou
 */
@RestController
@Slf4j
public class ForwardMessageReceiveController {

    @RequestMapping("example")
    public String receiver(@RequestBody Map<String, Object> jsonBody){
        log.info("receive forwarded message. {}", jsonBody);
        return "success";
    }
}
