package com.sample.loginform.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sample.loginform.Entity.User;

@Repository
public interface UserRepo extends JpaRepository<User,Long>{
	 Optional<User> findByUserNameAndPassword(String userName, String password);
	}


