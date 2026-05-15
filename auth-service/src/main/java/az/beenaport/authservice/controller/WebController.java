package az.beenaport.authservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class WebController {

    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException {
        response.sendRedirect("/login.html");
    }

    @GetMapping("/register")
    public void register(HttpServletResponse response) throws IOException {
        response.sendRedirect("/register.html");
    }
}
