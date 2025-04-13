package net.skhu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import net.skhu.model.Pagination;
import net.skhu.service.UserService;

@Controller
@RequestMapping("user")
public class UserController {
    @Autowired UserService userService;

    @Secured("ROLE_ADMIN")
    @GetMapping("list")
    public String list(Model model, Pagination pagination) {
        model.addAttribute("users", userService.findAll(pagination));
        model.addAttribute("orderOptions", userService.getOrderOptions());
        model.addAttribute("searchOptions", userService.getSearchOptions());
        return "user/list";
    }

}
