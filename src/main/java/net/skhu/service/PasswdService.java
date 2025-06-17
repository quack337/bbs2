package net.skhu.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import net.skhu.entity.ResetPasswd;
import net.skhu.entity.User;
import net.skhu.model.SendPwMail;
import net.skhu.repository.ResetPasswdRepository;
import net.skhu.repository.UserRepository;

@Service
@Slf4j
public class PasswdService {
    @Autowired UserRepository userRepository;
    @Autowired ResetPasswdRepository resetPasswdRepository;
    @Autowired JavaMailSender mailSender;
    @Autowired SpringTemplateEngine templateEngine;
    @Autowired PasswordEncoder passwordEncoder;
    @Value("${my.reset.passwd.url}") String resetPwUrl;
    @Value("${my.reset.passwd.timeout}") int resetPwTimeout;

    public void sendResetPasswdMail(SendPwMail sendPwMail, BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors())
            throw new Exception("입력 데이터 오류");
        Optional<User> user = userRepository.findByLoginName(sendPwMail.getLoginName());
        if (user.isEmpty() ||
            Objects.equals(user.get().getEmail(), sendPwMail.getEmail()) == false) {
            bindingResult.rejectValue("", null, "일치하는 사용자 정보가 없습니다");
            throw new Exception("입력 데이터 오류");
        }
        String guid = UUID.randomUUID().toString().replaceAll("-", "");
        ResetPasswd resetPasswd = new ResetPasswd();
        resetPasswd.setGuid(guid);
        resetPasswd.setUser(user.get());
        resetPasswd.setCreatedAt(LocalDateTime.now());
        resetPasswdRepository.save(resetPasswd);

        String url = resetPwUrl + "?guid=" + guid;
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setSubject("비밀번호 초기화");
        helper.setTo(user.get().getEmail());
        Context context = new Context();
        context.setVariable("url", url);
        String html = templateEngine.process("passwd/reset2", context);
        helper.setText(html, true);
        mailSender.send(message);
    }

    public String resetPasswd(String guid) throws Exception {
        ResetPasswd resetpw = resetPasswdRepository.findByGuid(guid)
                .orElseThrow(() -> new Exception("이상한 guid"));
        long minutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), resetpw.getCreatedAt());
        if (minutes > resetPwTimeout)
            throw new Exception("만기가 지난 요청입니다");
        User user = resetpw.getUser();
        String newPw = guid.substring(0, 4);
        user.setPassword(passwordEncoder.encode(newPw));
        userRepository.save(user);
        resetpw.setUsedAt(LocalDateTime.now());
        resetPasswdRepository.save(resetpw);
        return newPw;
    }
}
