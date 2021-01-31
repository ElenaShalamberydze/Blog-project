package com.leverx.blog.controller;

import com.leverx.blog.exception.ValidationException;
import com.leverx.blog.model.RedisCode;
import com.leverx.blog.repository.RedisCodeRepository;
import com.leverx.blog.service.Impl.MailSender;
import com.leverx.blog.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.UUID;

@RestController
@CrossOrigin
public class UserController {

    private static final Logger logger = Logger.getLogger(UserController.class);

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RedisCodeRepository redisCodeRepository;
    private final MailSender mailSender;

    public UserController(UserService userService, PasswordEncoder passwordEncoder,
                          RedisCodeRepository redisCodeRepository, MailSender mailSender) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.redisCodeRepository = redisCodeRepository;
        this.mailSender = mailSender;
    }

    @GetMapping(value = "/login")
    public ModelAndView loginPage(ModelAndView model) {
        model.setViewName("login");
        return model;
    }

    @GetMapping(value = "/auth/forgot_password")
    public ModelAndView forgotPasswordPage(ModelAndView model) {
        model.setViewName("forgot-password");
        return model;
    }

    @PostMapping(value = "/auth/forgot_password")
    public RedirectView forgotPassword(@RequestParam String email) {

        final String code = UUID.randomUUID().toString();
        redisCodeRepository.save(new RedisCode(code, email));

        String message = String.format("Hi! Here is a code to reset your password: %s", code);
        mailSender.send(email, "Reset password code", message);

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/auth/reset");
        return redirectView;
    }

    @GetMapping(value = "/auth/reset")
    public ModelAndView resetPasswordPage(ModelAndView model) {
        model.setViewName("reset-password");
        return model;
    }

    @PostMapping(value = "/auth/reset")
    public RedirectView resetPassword(@RequestParam String code, @RequestParam String password) {
        RedisCode redisCode = redisCodeRepository.findByCode(code);
        if (null == redisCode) {
            logger.error("code is not valid");
            throw new ValidationException("Code is not valid");
        }
        userService.changePassword(redisCode.getEmail(), passwordEncoder.encode(password));
        redisCodeRepository.deleteByCode(code);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/articles");
        return redirectView;
    }

    @GetMapping(value = "/auth/check_code")
    public ModelAndView checkCode(@RequestParam String code, ModelAndView model) {
        String result = (null == redisCodeRepository.findByCode(code)) ? "Code has expired" : "Code is valid";
        model.addObject("check", result);
        model.setViewName("check-code");
        return model;
    }

    @RequestMapping("/activate/{email}")
    public RedirectView activateUser(@PathVariable(name = "email") String email) {
        final String code = UUID.randomUUID().toString();
        redisCodeRepository.save(new RedisCode(code, email));

        String message = String.format("Hello! Please, activate your account via link: http://localhost:8080/auth/confirm/%s", code);
        mailSender.send(email, "Activation link", message);

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/login?inactive=true");
        return redirectView;
    }

}
