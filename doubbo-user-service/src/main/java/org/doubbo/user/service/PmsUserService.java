package org.doubbo.user.service;

import org.doubbo.common.User;
import org.doubbo.user.facede.PmsUserFacede;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("pmsUser")
public class PmsUserService implements PmsUserFacede{
	
	@Autowired
	private PmsUserDAO pmsUserDAO;

	public void addUser(User user) {
		pmsUserDAO.addUser(user);
	}

}
