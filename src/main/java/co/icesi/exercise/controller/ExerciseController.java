package co.icesi.exercise.controller;

import co.icesi.exercise.model.nosql.ExerciseDocument;
import co.icesi.exercise.model.nosql.VisualSupportDocument;
import co.icesi.exercise.services.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/exercise")
public class ExerciseController {

    @Autowired
    private ExerciseService exerciseService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String list(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("exercises", exerciseService.getAllExercises());
        model.addAttribute("userName", userDetails.getUsername());
        return "exercise/list";
    }

    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @GetMapping("/new")
    public String newForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("exercise", new ExerciseDocument());
        model.addAttribute("userName", userDetails.getUsername());
        return "exercise/form";
    }

    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @PostMapping("/create")
    public String create(@ModelAttribute ExerciseDocument exercise) {
        exerciseService.createExercise(exercise);
        return "redirect:/exercise/list";
    }

    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable String id, Model model,
                           @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("exercise", exerciseService.getExerciseById(id));
        model.addAttribute("userName", userDetails.getUsername());
        return "exercise/form";
    }

    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @PostMapping("/update/{id}")
    public String update(@PathVariable String id, @ModelAttribute ExerciseDocument exercise) {
        exerciseService.updateExercise(id, exercise);
        return "redirect:/exercise/list";
    }

    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable String id) {
        exerciseService.deleteExercise(id);
        return "redirect:/exercise/list";
    }

    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @GetMapping("/{id}/visual-supports")
    public String visualSupports(@PathVariable String id, Model model,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("exercise", exerciseService.getExerciseById(id));
        model.addAttribute("userName", userDetails.getUsername());
        return "exercise/visual-supports";
    }

    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @PostMapping("/{id}/visual-supports/add")
    public String addVisualSupport(@PathVariable String id,
                                   @ModelAttribute VisualSupportDocument vs) {
        exerciseService.addVisualSupport(id, vs);
        return "redirect:/exercise/" + id + "/visual-supports";
    }

    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @PostMapping("/{id}/visual-supports/remove/{index}")
    public String removeVisualSupport(@PathVariable String id, @PathVariable int index) {
        exerciseService.removeVisualSupport(id, index);
        return "redirect:/exercise/" + id + "/visual-supports";
    }
}
