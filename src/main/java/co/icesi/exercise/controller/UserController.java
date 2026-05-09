package co.icesi.exercise.controller;

import co.icesi.exercise.dto.AppUserDTO;
import co.icesi.exercise.model.AppUser;
import co.icesi.exercise.services.RoleService;
import co.icesi.exercise.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String dashboard(Authentication authentication, Model model) {
        model.addAttribute("userName", authentication.getName());
        return "dashboard/index";
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('VIEW_USERS')")
    public String listUsers(Model model, Authentication authentication) {

        model.addAttribute("users", userService.getAllAppUsers());
        model.addAttribute("userName", authentication.getName());

        return "user/list";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("user", new AppUserDTO());
        return "login/signup";
    }

    @PostMapping("/signup")
    public String createUser(@ModelAttribute AppUserDTO dto) {

        AppUser user = new AppUser();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setAge(dto.getAge());
        user.setWeight(dto.getWeight());
        user.setHeight(dto.getHeight());

        // rol default (USER) — buscar por nombre para no depender del id
        int userRoleId = roleService.getRoleByName("USER").getId();
        userService.createAppUser(user, new ArrayList<>(java.util.List.of(userRoleId)));

        return "redirect:/login";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public String editUser(@PathVariable int id, Model model) {

        model.addAttribute("user", userService.getAppUserById(id));

        return "user/form";
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public String updateUser(@PathVariable int id, @ModelAttribute AppUserDTO dto) {

        AppUser user = new AppUser();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }

        user.setAge(dto.getAge());
        user.setWeight(dto.getWeight());
        user.setHeight(dto.getHeight());

        userService.updateAppUser(id, user, dto.getRoleIds());

        return "redirect:/user/list";
    }


    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public String deleteUser(@PathVariable int id) {
        userService.deleteAppUserById(id);
        return "redirect:/user/list";
    }

    @GetMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    public String manageRoles(@PathVariable int id, Model model) {

        model.addAttribute("user", userService.getAppUserById(id));
        model.addAttribute("roles", roleService.getAllRoles());

        return "user/roles";
    }

    @PostMapping("/{id}/roles/add")
    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    public String addRole(@PathVariable int id, @RequestParam int roleId) {

        userService.assignRoleToUser(id, roleId);

        return "redirect:/user/" + id + "/roles";
    }

    @PostMapping("/{id}/roles/remove")
    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    public String removeRole(@PathVariable int id, @RequestParam int roleId) {

        userService.removeRoleFromUser(id, roleId);

        return "redirect:/user/" + id + "/roles";
    }


    @GetMapping("/{id}/trainer")
    @PreAuthorize("hasAuthority('ASSIGN_TRAINER')")
    public String trainerPage(@PathVariable int id, Model model) {

        model.addAttribute("user", userService.getAppUserWithTrainers(id));
        model.addAttribute("trainers", userService.getUsersByRoleId(roleService.getRoleByName("TRAINER").getId()));

        return "user/trainer";
    }

    @PostMapping("/{id}/trainer/assign")
    @PreAuthorize("hasAuthority('ASSIGN_TRAINER')")
    public String assignTrainer(@PathVariable int id, @RequestParam int trainerId) {

        userService.assignTrainerToUser(id, trainerId);

        return "redirect:/user/" + id + "/trainer";
    }

    @GetMapping("/my-students")
    @PreAuthorize("hasAuthority('VIEW_USERS')")
    public String myStudents(Model model, Authentication authentication) {
        int trainerId = userService.getUserByEmail(authentication.getName()).getId();
        model.addAttribute("students", userService.getUsersByTrainerId(trainerId));
        model.addAttribute("userName", authentication.getName());
        return "user/my-students";
    }
}