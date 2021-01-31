package com.leverx.blog.service;

import com.leverx.blog.model.User;

public interface UserService {

    void createUser(User user, String code);

    void activateUser(String email);

    void changePassword(String email, String password);

}
