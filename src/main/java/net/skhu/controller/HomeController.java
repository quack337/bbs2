package net.skhu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import net.skhu.model.UserSignUp;
import net.skhu.service.UserService;

@Controller
public class HomeController {

    @Autowired UserService userService;

    @GetMapping("/")
    public String index() {
        return "home/index";
    }

    @GetMapping("login")
    public String login() {
        return "home/login";
    }

    @GetMapping("signUp")
    public String signup(Model model) {
        model.addAttribute(new UserSignUp());
        return "home/signUp";
    }

    @PostMapping("signUp")
    public String signup(Model model,
            @Valid UserSignUp userSignUp, BindingResult bindingResult)
    {
        try {
            userService.insert(userSignUp, bindingResult);
            return "redirect:signUpComplete";
        } catch (Exception ex) {
            bindingResult.rejectValue("", null, "등록할 수 없습니다.");
            return "home/signUp";
        }
    }

    @GetMapping("signUpComplete")
    public String signUpComplete(Model model) {
        return "home/signUpComplete";
    }

}
