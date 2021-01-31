package com.leverx.blog.repository;

import com.leverx.blog.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {

    User findByEmail(String email);

    User save(User user);

}
