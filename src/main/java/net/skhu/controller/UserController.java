package net.skhu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.skhu.model.Pagination;
import net.skhu.model.UserEdit;
import net.skhu.service.UserService;

@Controller
@RequestMapping("user")
@Secured("ROLE_ADMIN")
@Slf4j
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

    @GetMapping("edit")
    public String edit(Model model, Pagination pagination, @RequestParam("id") int id) {
        model.addAttribute("userEdit", userService.findById(id));
        return "user/edit";
    }

    @PostMapping(value="edit", params="cmd=save")
    public String edit(Model model, Pagination pagination,
            @Valid UserEdit userEdit, BindingResult bindingResult) {
        try {
            userService.update(userEdit, bindingResult);
            return "redirect:list?" + pagination.getQueryString();
        }
        catch (Exception ex) {
            log.error("사용자 수정 에러", ex);
            bindingResult.rejectValue("", null, "수정할 수 없습니다.");
            return "user/edit";
        }
    }

    @PostMapping(value="edit", params="cmd=delete")
    public String delete(Model model, Pagination pagination,
            UserEdit userEdit, BindingResult bindingResult) {
        try {
            userService.delete(userEdit.getId());
            return "redirect:list?" + pagination.getQueryString();
        }
        catch (Exception ex) {
            log.error("사용자 수정 에러", ex);
            bindingResult.rejectValue("", null, "삭제할 수 없습니다.");
            return "user/edit";
        }
    }
}
