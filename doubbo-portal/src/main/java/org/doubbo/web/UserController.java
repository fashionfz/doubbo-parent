package org.doubbo.web;

import javax.annotation.Resource;

import org.doubbo.common.User;
import org.doubbo.user.facede.PmsUserFacede;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Resource(name = "pmsUserService")
	private PmsUserFacede pmsUser;

	@RequestMapping("/add.do")
	public void addUser(){
		User u = new User();
		pmsUser.addUser(u);
	}
}
