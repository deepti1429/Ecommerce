package com.sample.loginform.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sample.loginform.Entity.User;
import com.sample.loginform.Repository.UserRepo;

@Service
public class UserService {
@Autowired
	private UserRepo repo;

public User saveDetails(User user) {
	 if (user.getUserName() == null || user.getUserName().isEmpty()) {
         throw new IllegalArgumentException("userName must not be null or empty");
     }
	return repo.save(user);
}

public Optional<User> findByUserNameAndPassword(String userName, String password) {
    return repo.findByUserNameAndPassword(userName, password);
}
}
