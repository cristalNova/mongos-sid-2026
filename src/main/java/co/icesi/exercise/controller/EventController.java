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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String list(Model model,
                       @AuthenticationPrincipal UserDetails userDetails,
                       @RequestParam(required = false) String q) {
        int currentUserId = userService.getUserByEmail(userDetails.getUsername()).getId();
        List<EventDocument> allEvents = eventService.getAllEvents();
        List<EventDocument> events = (q != null && !q.isBlank())
                ? allEvents.stream()
                    .filter(e -> e.getName() != null &&
                                 e.getName().toLowerCase().contains(q.toLowerCase()))
                    .toList()
                : allEvents;
        Set<String> subscribedEventIds = new HashSet<>();
        for (EventDocument ev : allEvents) {
            boolean subscribed = ev.getSubscriptions().stream()
                    .anyMatch(s -> s.getUserId() != null && s.getUserId().equals(currentUserId));
            if (subscribed) subscribedEventIds.add(ev.getId());
        }
        model.addAttribute("events", events);
        model.addAttribute("subscribedEventIds", subscribedEventIds);
        model.addAttribute("q", q);
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
    public String create(@ModelAttribute EventDocument event, RedirectAttributes ra) {
        eventService.createEvent(event);
        ra.addFlashAttribute("flashSuccess", "Evento creado correctamente.");
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
    public String update(@PathVariable String id, @ModelAttribute EventDocument event,
                         RedirectAttributes ra) {
        eventService.updateEvent(id, event);
        ra.addFlashAttribute("flashSuccess", "Evento actualizado correctamente.");
        return "redirect:/event/list";
    }

    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable String id, RedirectAttributes ra) {
        eventService.deleteEvent(id);
        ra.addFlashAttribute("flashSuccess", "Evento eliminado.");
        return "redirect:/event/list";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/subscribe")
    public String subscribe(@PathVariable String id,
                            @AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes ra) {
        int userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        eventService.subscribeUser(id, userId);
        ra.addFlashAttribute("flashSuccess", "¡Te has inscrito al evento!");
        return "redirect:/event/list";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/unsubscribe")
    public String unsubscribe(@PathVariable String id,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes ra) {
        int userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        eventService.unsubscribeUser(id, userId);
        ra.addFlashAttribute("flashSuccess", "Inscripción cancelada.");
        return "redirect:/event/list";
    }

    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @GetMapping("/{id}/attendance")
    public String attendancePage(@PathVariable String id, Model model,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        var event = eventService.getEventById(id);
        long attendedCount = event.getSubscriptions().stream()
                .filter(s -> Boolean.TRUE.equals(s.getAttendance()))
                .count();
        int totalCount = event.getSubscriptions().size();
        int attendancePct = totalCount > 0 ? (int) (attendedCount * 100 / totalCount) : 0;
        model.addAttribute("event", event);
        model.addAttribute("attendedCount", attendedCount);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("attendancePct", attendancePct);
        model.addAttribute("userName", userDetails.getUsername());
        return "event/attendance";
    }

    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @PostMapping("/{id}/attendance")
    public String markAttendance(@PathVariable String id,
                                 @RequestParam int userId,
                                 @RequestParam boolean attended,
                                 RedirectAttributes ra) {
        eventService.markAttendance(id, userId, attended);
        ra.addFlashAttribute("flashSuccess", attended ? "Asistencia marcada." : "Asistencia quitada.");
        return "redirect:/event/" + id + "/attendance";
    }
}
