package co.icesi.exercise.controller;

import co.icesi.exercise.model.nosql.ProgressRecordDocument;
import co.icesi.exercise.services.ProgressRecordService;
import co.icesi.exercise.services.RoutineService;
import co.icesi.exercise.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/progress")
public class ProgressRecordController {

    @Autowired
    private ProgressRecordService progressRecordService;
    @Autowired
    private RoutineService routineService;
    @Autowired
    private UserService userService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String list(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        int userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        model.addAttribute("records", progressRecordService.getProgressRecordsByUserId(userId));
        model.addAttribute("userName", userDetails.getUsername());
        return "progress/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/new")
    public String newForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        int userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        model.addAttribute("record", new ProgressRecordDocument());
        model.addAttribute("routines", routineService.getRoutinesByOwnerId(userId));
        model.addAttribute("userName", userDetails.getUsername());
        return "progress/form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String create(@ModelAttribute ProgressRecordDocument record,
                         @RequestParam String routineId,
                         @AuthenticationPrincipal UserDetails userDetails) {
        int userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        progressRecordService.createProgressRecord(record, userId, routineId);
        return "redirect:/progress/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable String id, Model model,
                           @AuthenticationPrincipal UserDetails userDetails) {
        int userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        model.addAttribute("record", progressRecordService.getProgressRecordById(id));
        model.addAttribute("routines", routineService.getRoutinesByOwnerId(userId));
        model.addAttribute("userName", userDetails.getUsername());
        return "progress/form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/update/{id}")
    public String update(@PathVariable String id, @ModelAttribute ProgressRecordDocument record) {
        progressRecordService.updateProgressRecord(id, record);
        return "redirect:/progress/list";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable String id) {
        progressRecordService.deleteProgressRecord(id);
        return "redirect:/progress/list";
    }
}
