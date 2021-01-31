package com.leverx.blog.service.Impl;

import com.leverx.blog.exception.NotFoundException;
import com.leverx.blog.exception.NotModifiedException;
import com.leverx.blog.exception.ValidationException;
import com.leverx.blog.model.User;
import com.leverx.blog.model.UserDetailsImpl;
import com.leverx.blog.repository.UserRepository;
import com.leverx.blog.service.UserService;
import com.leverx.blog.service.validation.BlogValidator;
import org.apache.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private static final Logger logger = Logger.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final MailSender mailSender;

    public UserServiceImpl(UserRepository userRepository, MailSender mailSender) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return new UserDetailsImpl(userRepository.findByEmail(email));
    }

    @Override
    @Transactional
    public void createUser(User user, String code) {
        if (!BlogValidator.isCorrect(user)) {
            logger.error("Error saving user into DB: validation failed");
            throw new ValidationException("Incorrect data!");
        }
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (null != userRepository.findByEmail(user.getEmail())) {
            if (!existingUser.isActive() &&
                    existingUser.getCreatedAt().isBefore(LocalDate.now().minusDays(1))) {
                user.setId(existingUser.getId());
            } else {
                throw new ValidationException("User already exists!");
            }
        }
        user.setCreatedAt(LocalDate.now());
        if (null == userRepository.save(user)) {
            logger.error("Error saving user into DB: not modified");
            throw new NotModifiedException("Error: data havent been modified");
        }
        String message = String.format("Hello, %s! Please, activate your account via link: http://localhost:8080/auth/confirm/%s",
                user.getFirstName(), code);
        mailSender.send(user.getEmail(), "Activation link", message);
    }

    @Override
    @Transactional
    public void activateUser(String email) {
        User user = userRepository.findByEmail(email);
        if (null == user) {
            logger.error("Error activating user: user not found");
            throw new NotFoundException("user not found");
        }
        user.setActive(true);
        if (null == userRepository.save(user)) {
            logger.error("Error activating user: not modified");
            throw new NotModifiedException("Error: havent been activated");
        }
    }

    @Override
    @Transactional
    public void changePassword(String email, String password) {
        if (null == userRepository.findByEmail(email)) {
            logger.error("Error changing password: user not found");
            throw new NotFoundException("user not found");
        }
        User user = userRepository.findByEmail(email);
        user.setPassword(password);
        userRepository.save(user);
        if (!userRepository.findByEmail(email).getPassword().equals(password)) {
            logger.error("Error changing password: not modified");
            throw new NotModifiedException("Error: havent been activated");
        }
    }


}
