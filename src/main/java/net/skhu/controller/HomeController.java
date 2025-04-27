package net.skhu.controller;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.skhu.config.MyUserDetails;
import net.skhu.entity.User;
import net.skhu.model.UserOAuth2SignUp;
import net.skhu.model.UserSignUp;
import net.skhu.service.UserService;

@Controller
@Slf4j
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
            log.error("회원가입 에러", ex);
            bindingResult.rejectValue("", null, "등록할 수 없습니다.");
            return "home/signUp";
        }
    }

    @GetMapping("signUpComplete")
    public String signUpComplete(Model model) {
        return "home/signUpComplete";
    }

    @GetMapping("oauth2signup")
    public String OAuth2SignUp(Model model, OAuth2AuthenticationToken auth) {
        Optional<User> user = userService.findByOpenId(auth);
        if (user.isPresent()) {
            oauth2login(user.get(), auth);
            return "redirect:/";
        } else {
            UserOAuth2SignUp userOAuth2SignUp = userService.createUserOAuth2SignUp(auth);
            model.addAttribute("userOAuth2SignUp", userOAuth2SignUp);
            return "home/oauth2SignUp";
        }
    }

    @PostMapping("oauth2signup")
    public String OAuth2SignUp(Model model, OAuth2AuthenticationToken auth,
            @Valid UserOAuth2SignUp userOAuth2SignUp, BindingResult bindingResult) {
        try {
            User user = userService.insert(auth, userOAuth2SignUp, bindingResult);
            oauth2login(user, auth);
            return "redirect:/";
        } catch (Exception e) {
            log.error("OAuth2SignUp 에러", e);
            bindingResult.rejectValue("", null, "등록할 수 없습니다.");
            return "home/oauth2SignUp";
        }
    }

    void oauth2login(User user, OAuth2AuthenticationToken auth) {
        var userDetails = new MyUserDetails(user, auth.getPrincipal().getAttributes());
        var newAuth = new OAuth2AuthenticationToken(userDetails,
                userDetails.getAuthorities(), auth.getAuthorizedClientRegistrationId());
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
