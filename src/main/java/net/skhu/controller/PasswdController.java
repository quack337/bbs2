package net.skhu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.skhu.model.SendPwMail;
import net.skhu.service.PasswdService;

@Controller
@Slf4j
@RequestMapping("passwd")
public class PasswdController {

    @Autowired PasswdService passwdService;

    @GetMapping("reset1")
    public String reset1(Model model) {
        model.addAttribute("sendPwMail", new SendPwMail());
        return "passwd/reset1";
    }

    @PostMapping("reset1")
    public String reset1(Model model,
            @Valid SendPwMail sendPwMail, BindingResult bindingResult) {
        try {
            passwdService.sendResetPasswdMail(sendPwMail, bindingResult);
            model.addAttribute("message", "이메일을 전송했습니다");
            return "passwd/reset1";
        } catch (Exception e) {
            log.error("에러", e);
            bindingResult.rejectValue("", null, "이메일 전송 실패");
            return "passwd/reset1";
        }
    }

    @GetMapping("reset3")
    public String reset3g(Model model, @RequestParam("guid") String guid) {
        model.addAttribute("guid", guid);
        return "passwd/reset3";
    }

    @PostMapping("reset3")
    public String reset3p(Model model, @RequestParam("guid") String guid) {
        try {
            String newPw = passwdService.resetPasswd(guid);
            model.addAttribute("newPw", newPw);
            return "passwd/reset3";
        }
        catch (Exception e) {
            log.error("에러", e);
            model.addAttribute("error", "비밀번호 초기화 실패했습니다.");
            return "passwd/reset3";
        }
    }
}
