package io.github.joelou.solairelight.runner.controller;

import lombok.Data;

/**
 * @author Joel Ou
 */
@Data
public class NewSessionResponse {
    private String token;

    private String instance;
}
