package co.icesi.exercise.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Adds currentPath to every model so templates can detect the active nav tab
 * without relying on #request (removed in Thymeleaf 3.1).
 */
@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute("currentPath")
    public String currentPath(HttpServletRequest request) {
        return request.getServletPath();
    }
}
