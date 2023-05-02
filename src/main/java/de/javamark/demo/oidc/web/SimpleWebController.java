package de.javamark.demo.oidc.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@Controller
public class SimpleWebController {
    private static final Logger log = LoggerFactory.getLogger(SimpleWebController.class);

    @GetMapping(value = "/example",
            produces = MediaType.TEXT_HTML_VALUE)
    public String example(@RequestHeader Map<String, String> headers, Model model) {
        headers.forEach((key, value) -> {
            log.info(String.format("Header '%s' = %s", key, value));
        });

        // Füge alle empfangenen Header hinzu
        model.addAttribute("headersMap", headers);
        return "example";
    }
}
