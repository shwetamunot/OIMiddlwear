package com.tcs.controller;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tcs.dao.UserDAO;
import com.tcs.model.ErrorClasses;
import com.tcs.model.Users;

@Controller

public class UsersController {
	@Autowired
	private UserDAO userDao;
@RequestMapping(value="/register",method=RequestMethod.POST)
public ResponseEntity<?> register(@RequestBody Users user)
{
	try
	{
		if(!userDao.isUsernameValid(user.getUsername()))
		{
			ErrorClasses error=new ErrorClasses(2,"username already exists,please choose different username");
			return new ResponseEntity<ErrorClasses>(error,HttpStatus.CONFLICT);
		}
		if(!userDao.isEmailValid(user.getEmail()))
		{
			ErrorClasses error=new ErrorClasses(3,"Emailid already exists please enter different email");
			return new ResponseEntity<ErrorClasses>(error,HttpStatus.CONFLICT);
		}	
		userDao.register(user);
	}
	catch(Exception e)
	{
		ErrorClasses error=new ErrorClasses(1,"Unable to register");
		return new ResponseEntity<ErrorClasses>(error,HttpStatus.INTERNAL_SERVER_ERROR);
	
	}
	return new ResponseEntity<Users>(user,HttpStatus.OK);
}

@RequestMapping(value="/login",method=RequestMethod.POST)
public ResponseEntity<?> login(@RequestBody Users user,HttpSession session)
{
	Users validuser=userDao.login(user);
	if(validuser==null)
	{
		ErrorClasses error=new ErrorClasses(4,"Invalid username/password");
	return	new ResponseEntity<ErrorClasses>(error,HttpStatus.UNAUTHORIZED);
	}else
	{
		validuser.setOnline(true);
		session.setAttribute("username",validuser.getUsername());
		userDao.updateUser(validuser);
		return new ResponseEntity<Users>(validuser,HttpStatus.OK);
	}
	
	
}

@RequestMapping(value="/logout",method=RequestMethod.GET)
public ResponseEntity<?> logout(HttpSession session)
{
	String username=(String) session.getAttribute("username");
	if(username==null)
	{
	ErrorClasses error=new ErrorClasses(5,"Unauthorized access");
	return new ResponseEntity<ErrorClasses>(error,HttpStatus.UNAUTHORIZED);
		
	}
	Users user=userDao.getUserByUsername(username);
	user.setOnline(false);
	session.removeAttribute("username");
	session.invalidate();

	return new ResponseEntity<Void>(HttpStatus.OK);	
}

}
