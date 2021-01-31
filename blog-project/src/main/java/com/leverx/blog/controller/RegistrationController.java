package com.leverx.blog.controller;

import com.leverx.blog.exception.ValidationException;
import com.leverx.blog.model.RedisCode;
import com.leverx.blog.model.User;
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
public class RegistrationController {
    private static final Logger logger = Logger.getLogger(RegistrationController.class);

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RedisCodeRepository redisCodeRepository;

    public RegistrationController(UserService userService, PasswordEncoder passwordEncoder,
                                  RedisCodeRepository redisCodeRepository) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.redisCodeRepository = redisCodeRepository;
    }

    @GetMapping(value = "/registration")
    public ModelAndView registrationPage(ModelAndView model) {
        model.setViewName("registration");
        return model;
    }

    @PostMapping(value = "/registration")
    public RedirectView registration(@RequestParam String firstName, @RequestParam String lastName,
                                     @RequestParam String email, @RequestParam String password
    ) {
        if (null == firstName || null == lastName || null == email || null == password) {
            logger.error("Error creating user: empty fields");
            throw new ValidationException("Incorrect data!");
        }
        User user = new User(firstName, lastName, passwordEncoder.encode(password), email);

        final String code = UUID.randomUUID().toString();
        redisCodeRepository.save(new RedisCode(code, email));

        userService.createUser(user, code);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/articles");
        return redirectView;
    }

    @RequestMapping("/auth/confirm/{hash_code}")
    public RedirectView activateUser(@PathVariable(name = "hash_code") String code) {
        RedisCode redisCode = redisCodeRepository.findByCode(code);
        if (null == redisCode) {
            logger.error("code is not valid");
            throw new ValidationException("Code is not valid");
        }
        userService.activateUser(redisCode.getEmail());
        redisCodeRepository.deleteByCode(code);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/login");
        return redirectView;
    }
}
