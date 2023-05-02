package de.javamark.demo.oidc.web;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
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
    public String example(Authentication authentication, @RequestHeader Map<String, String> headers, Model model) {
        // Füge alle empfangenen Header hinzu
        model.addAttribute("headersMap", headers);

        // Füge alle User-Attributes hinzu
        OAuth2AuthenticatedPrincipal user = (OAuth2AuthenticatedPrincipal) authentication.getPrincipal();
        model.addAttribute("userAttributes", user.getAttributes());

        return "example";
    }
}
