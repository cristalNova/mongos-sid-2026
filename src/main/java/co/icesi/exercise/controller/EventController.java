package co.icesi.exercise.controller;

import co.icesi.exercise.model.nosql.EventDocument;
import co.icesi.exercise.services.EventService;
import co.icesi.exercise.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/event")
public class EventController {

    @Autowired
    private EventService eventService;
    @Autowired
    private UserService userService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String list(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        int currentUserId = userService.getUserByEmail(userDetails.getUsername()).getId();
        List<EventDocument> events = eventService.getAllEvents();
        Set<String> subscribedEventIds = new HashSet<>();
        for (EventDocument ev : events) {
            boolean subscribed = ev.getSubscriptions().stream()
                    .anyMatch(s -> s.getUserId() != null && s.getUserId().equals(currentUserId));
            if (subscribed) subscribedEventIds.add(ev.getId());
        }
        model.addAttribute("events", events);
        model.addAttribute("subscribedEventIds", subscribedEventIds);
        model.addAttribute("userName", userDetails.getUsername());
        return "event/list";
    }

    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @GetMapping("/new")
    public String newForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("event", new EventDocument());
        model.addAttribute("userName", userDetails.getUsername());
        return "event/form";
    }

    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @PostMapping("/create")
    public String create(@ModelAttribute EventDocument event) {
        eventService.createEvent(event);
        return "redirect:/event/list";
    }

    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable String id, Model model,
                           @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("event", eventService.getEventById(id));
        model.addAttribute("userName", userDetails.getUsername());
        return "event/form";
    }

    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @PostMapping("/update/{id}")
    public String update(@PathVariable String id, @ModelAttribute EventDocument event) {
        eventService.updateEvent(id, event);
        return "redirect:/event/list";
    }

    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable String id) {
        eventService.deleteEvent(id);
        return "redirect:/event/list";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/subscribe")
    public String subscribe(@PathVariable String id,
                            @AuthenticationPrincipal UserDetails userDetails) {
        int userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        eventService.subscribeUser(id, userId);
        return "redirect:/event/list";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/unsubscribe")
    public String unsubscribe(@PathVariable String id,
                              @AuthenticationPrincipal UserDetails userDetails) {
        int userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        eventService.unsubscribeUser(id, userId);
        return "redirect:/event/list";
    }

    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @PostMapping("/{id}/attendance")
    public String markAttendance(@PathVariable String id,
                                 @RequestParam int userId,
                                 @RequestParam boolean attended) {
        eventService.markAttendance(id, userId, attended);
        return "redirect:/event/list";
    }
}
