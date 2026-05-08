package co.icesi.exercise.controller;
import co.icesi.exercise.dto.PermissionDTO;
import co.icesi.exercise.model.Permission;
import co.icesi.exercise.services.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/create")
    @PreAuthorize("hasAuthority('MANAGE_PERMISSIONS')")
    public String createForm(Model model) {
        model.addAttribute("permission", new PermissionDTO());
        return "permission/form";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('MANAGE_PERMISSIONS')")
    public String createPermission(@ModelAttribute PermissionDTO dto) {

        Permission permission = new Permission();
        permission.setName(dto.getName());

        permissionService.createPermission(permission);

        return "redirect:/permission/view";
    }
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('MANAGE_PERMISSIONS')")
    public String editForm(@PathVariable int id, Model model) {

        Permission permission = permissionService.getPermissionById(id);

        PermissionDTO dto = new PermissionDTO();
        dto.setName(permission.getName());

        model.addAttribute("permission", dto);
        model.addAttribute("permissionId", id);

        return "permission/form";
    }
    @PostMapping("/update/{id}")
    @PreAuthorize("hasAuthority('MANAGE_PERMISSIONS')")
    public String updatePermission(@PathVariable int id, @ModelAttribute PermissionDTO dto) {

        Permission permission = new Permission();
        permission.setName(dto.getName());

        permissionService.updatePermission(id, permission);

        return "redirect:/permission/view";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('MANAGE_PERMISSIONS')")
    public String deletePermission(@PathVariable int id) {
        permissionService.deletePermission(id);
        return "redirect:/permission/view";
    }

    @GetMapping("/view")
    @PreAuthorize("hasAuthority('MANAGE_PERMISSIONS')")
    public String viewPermissions(Model model) {
        model.addAttribute("permissions", permissionService.getAllPermissions());

        return "permission/list";
    }


}
