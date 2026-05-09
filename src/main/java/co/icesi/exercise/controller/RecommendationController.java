package co.icesi.exercise.controller;

import co.icesi.exercise.model.Recommendation;
import co.icesi.exercise.services.RecommendationService;
import co.icesi.exercise.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;

@Controller
@RequestMapping("/recommendation")
public class RecommendationController {

    @Autowired private RecommendationService recommendationService;
    @Autowired private UserService userService;

    /** Received recommendations — accessible to all authenticated users */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String received(Model model, @AuthenticationPrincipal UserDetails ud) {
        int userId = userService.getUserByEmail(ud.getUsername()).getId();
        model.addAttribute("recommendations", recommendationService.getRecommendationsReceivedByUser(userId));
        model.addAttribute("userName", ud.getUsername());
        return "recommendation/list";
    }

    /** Sent recommendations — trainers / admins only */
    @PreAuthorize("hasAuthority('VIEW_USERS')")
    @GetMapping("/sent")
    public String sent(Model model, @AuthenticationPrincipal UserDetails ud) {
        int userId = userService.getUserByEmail(ud.getUsername()).getId();
        model.addAttribute("recommendations", recommendationService.getRecommendationsSentByUser(userId));
        model.addAttribute("users", userService.getAllAppUsers());
        model.addAttribute("userName", ud.getUsername());
        return "recommendation/sent";
    }

    /** Form to create a new recommendation */
    @PreAuthorize("hasAuthority('VIEW_USERS')")
    @GetMapping("/new")
    public String newForm(@RequestParam(required = false) Integer receiverId,
                          Model model,
                          @AuthenticationPrincipal UserDetails ud) {
        model.addAttribute("recommendation", new Recommendation());
        model.addAttribute("users", userService.getAllAppUsers());
        model.addAttribute("receiverId", receiverId);
        model.addAttribute("userName", ud.getUsername());
        return "recommendation/form";
    }

    @PreAuthorize("hasAuthority('VIEW_USERS')")
    @PostMapping("/create")
    public String create(@ModelAttribute Recommendation recommendation,
                         @RequestParam int receiverId,
                         @AuthenticationPrincipal UserDetails ud) {
        int senderId = userService.getUserByEmail(ud.getUsername()).getId();
        recommendation.setDate(Date.valueOf(LocalDate.now()));
        recommendationService.createRecommendation(recommendation, senderId, receiverId);
        return "redirect:/recommendation/sent";
    }

    @PreAuthorize("hasAuthority('VIEW_USERS')")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        recommendationService.deleteRecommendation(id);
        return "redirect:/recommendation/sent";
    }
}
