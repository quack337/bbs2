package net.skhu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("user/list")
    public String list() {
        return "user/list";
    }
}
