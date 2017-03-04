package org.doubbo.user.service;

import org.doubbo.common.User;
import org.springframework.stereotype.Repository;

@Repository
public class PmsUserDAO {

	public void addUser(User user){
		System.out.println("dao add user ....");
	}
}
