package co.icesi.exercise.controller;

import co.icesi.exercise.model.nosql.RoutineDocument;
import co.icesi.exercise.services.ExerciseService;
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
@RequestMapping("/routine")
public class RoutineController {

    @Autowired
    private RoutineService routineService;
    @Autowired
    private ExerciseService exerciseService;
    @Autowired
    private UserService userService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String list(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("routines", routineService.getPublicRoutines());
        model.addAttribute("userName", userDetails.getUsername());
        return "routine/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mine")
    public String myRoutines(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        int userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        model.addAttribute("routines", routineService.getRoutinesByOwnerId(userId));
        model.addAttribute("userName", userDetails.getUsername());
        return "routine/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/new")
    public String newForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("routine", new RoutineDocument());
        model.addAttribute("userName", userDetails.getUsername());
        return "routine/form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String create(@ModelAttribute RoutineDocument routine,
                         @AuthenticationPrincipal UserDetails userDetails) {
        int ownerId = userService.getUserByEmail(userDetails.getUsername()).getId();
        routineService.createRoutine(routine, ownerId);
        return "redirect:/routine/mine";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable String id, Model model,
                           @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("routine", routineService.getRoutineById(id));
        model.addAttribute("userName", userDetails.getUsername());
        return "routine/form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/update/{id}")
    public String update(@PathVariable String id, @ModelAttribute RoutineDocument routine) {
        routineService.updateRoutine(id, routine);
        return "redirect:/routine/mine";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable String id) {
        routineService.deleteRoutine(id);
        return "redirect:/routine/mine";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/exercises")
    public String manageExercises(@PathVariable String id, Model model,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("routine", routineService.getRoutineById(id));
        model.addAttribute("allExercises", exerciseService.getAllExercises());
        model.addAttribute("userName", userDetails.getUsername());
        return "routine/exercises";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/exercises/add")
    public String addExercise(@PathVariable String id, @RequestParam String exerciseId) {
        routineService.addExercise(id, exerciseId);
        return "redirect:/routine/" + id + "/exercises";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/exercises/remove/{index}")
    public String removeExercise(@PathVariable String id, @PathVariable int index) {
        routineService.removeExercise(id, index);
        return "redirect:/routine/" + id + "/exercises";
    }
}
