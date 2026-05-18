package co.icesi.exercise.controller;
import co.icesi.exercise.dto.PermissionDTO;
import co.icesi.exercise.model.Permission;
import co.icesi.exercise.services.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/create")
    @PreAuthorize("hasAuthority('MANAGE_PERMISSIONS')")
    public String createForm(Model model, Authentication authentication) {
        model.addAttribute("permission", new PermissionDTO());
        model.addAttribute("userName", authentication.getName());
        return "permission/form";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('MANAGE_PERMISSIONS')")
    public String createPermission(@ModelAttribute PermissionDTO dto, RedirectAttributes ra) {

        Permission permission = new Permission();
        permission.setName(dto.getName());

        permissionService.createPermission(permission);
        ra.addFlashAttribute("flashSuccess", "Permiso creado correctamente.");

        return "redirect:/permission/view";
    }
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('MANAGE_PERMISSIONS')")
    public String editForm(@PathVariable int id, Model model, Authentication authentication) {

        Permission permission = permissionService.getPermissionById(id);

        PermissionDTO dto = new PermissionDTO();
        dto.setName(permission.getName());

        model.addAttribute("permission", dto);
        model.addAttribute("permissionId", id);
        model.addAttribute("userName", authentication.getName());

        return "permission/form";
    }
    @PostMapping("/update/{id}")
    @PreAuthorize("hasAuthority('MANAGE_PERMISSIONS')")
    public String updatePermission(@PathVariable int id, @ModelAttribute PermissionDTO dto,
                                   RedirectAttributes ra) {

        Permission permission = new Permission();
        permission.setName(dto.getName());

        permissionService.updatePermission(id, permission);
        ra.addFlashAttribute("flashSuccess", "Permiso actualizado correctamente.");

        return "redirect:/permission/view";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('MANAGE_PERMISSIONS')")
    public String deletePermission(@PathVariable int id, RedirectAttributes ra) {
        permissionService.deletePermission(id);
        ra.addFlashAttribute("flashSuccess", "Permiso eliminado.");
        return "redirect:/permission/view";
    }

    @GetMapping("/view")
    @PreAuthorize("hasAuthority('MANAGE_PERMISSIONS')")
    public String viewPermissions(Model model, Authentication authentication) {
        model.addAttribute("permissions", permissionService.getAllPermissions());
        model.addAttribute("userName", authentication.getName());
        return "permission/list";
    }


}
