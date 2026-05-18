package co.icesi.exercise.controller;

import co.icesi.exercise.dto.RoleDTO;
import co.icesi.exercise.model.Role;
import co.icesi.exercise.services.PermissionService;
import co.icesi.exercise.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/view")
    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    public String viewRoles(Model model, Authentication authentication) {
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("allPermissions", permissionService.getAllPermissions());
        model.addAttribute("roleForm", new RoleDTO());
        model.addAttribute("userName", authentication.getName());
        return "role/list";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    public String createRole(@ModelAttribute("roleForm") RoleDTO dto, RedirectAttributes ra) {
        Role role = new Role();
        role.setName(dto.getName());
        roleService.createRole(role, dto.getPermissionIds());
        ra.addFlashAttribute("flashSuccess", "Rol creado correctamente.");
        return "redirect:/role/view";
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    public String updateRole(@PathVariable int id,
                             @ModelAttribute RoleDTO dto, RedirectAttributes ra) {
        Role role = new Role();
        role.setName(dto.getName());
        roleService.updateRole(id, role, dto.getPermissionIds());
        ra.addFlashAttribute("flashSuccess", "Rol actualizado correctamente.");
        return "redirect:/role/view";
    }

    @PostMapping("/assign-permission")
    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    public String assignPermission(@RequestParam int roleId,
                                   @RequestParam int permissionId, RedirectAttributes ra) {
        roleService.assignPermissionToRole(roleId, permissionId);
        ra.addFlashAttribute("flashSuccess", "Permiso asignado al rol.");
        return "redirect:/role/view";
    }

    @PostMapping("/remove-permission")
    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    public String removePermission(@RequestParam int roleId,
                                   @RequestParam int permissionId, RedirectAttributes ra) {
        roleService.removePermissionFromRole(roleId, permissionId);
        ra.addFlashAttribute("flashSuccess", "Permiso removido del rol.");
        return "redirect:/role/view";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    public String deleteRole(@PathVariable int id, RedirectAttributes ra) {
        roleService.deleteRole(id);
        ra.addFlashAttribute("flashSuccess", "Rol eliminado.");
        return "redirect:/role/view";
    }
}