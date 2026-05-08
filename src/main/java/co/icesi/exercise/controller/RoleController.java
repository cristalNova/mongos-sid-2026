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
    public String createRole(@ModelAttribute("roleForm") RoleDTO dto) {
        Role role = new Role();
        role.setName(dto.getName());
        roleService.createRole(role, dto.getPermissionIds());
        return "redirect:/role/view";
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    public String updateRole(@PathVariable int id,
                             @ModelAttribute RoleDTO dto) {
        Role role = new Role();
        role.setName(dto.getName());
        roleService.updateRole(id, role, dto.getPermissionIds());
        return "redirect:/role/view";
    }

    @PostMapping("/assign-permission")
    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    public String assignPermission(@RequestParam int roleId,
                                   @RequestParam int permissionId) {
        roleService.assignPermissionToRole(roleId, permissionId);
        return "redirect:/role/view";
    }

    @PostMapping("/remove-permission")
    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    public String removePermission(@RequestParam int roleId,
                                   @RequestParam int permissionId) {
        roleService.removePermissionFromRole(roleId, permissionId);
        return "redirect:/role/view";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    public String deleteRole(@PathVariable int id) {
        roleService.deleteRole(id);
        return "redirect:/role/view";
    }
}