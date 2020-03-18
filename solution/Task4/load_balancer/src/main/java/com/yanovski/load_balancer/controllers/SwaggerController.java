package com.yanovski.load_balancer.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Home Controller - serves Swagger UI
 */
@RestController
public class SwaggerController {
    private final Logger logger = LogManager.getLogger(SwaggerController.class);

    @GetMapping("/")
    public void redirect(HttpServletResponse response) throws IOException {
        logger.info("Redirecting to Swagger.");
        response.sendRedirect("/swagger-ui.html");
    }
}
