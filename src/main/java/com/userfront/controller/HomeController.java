package com.userfront.controller;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.userfront.dao.RoleDao;
import com.userfront.domain.PrimaryAccount;
import com.userfront.domain.SavingsAccount;
import com.userfront.domain.User;
import com.userfront.domain.security.UserRole;
import com.userfront.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
public class HomeController {

	@Autowired
	private UserService userService;

	@Autowired
    private RoleDao roleDao;

	@RequestMapping("/")
	public String home() {
		return "redirect:/index";
	}

	@RequestMapping("/index")
    public String index() {
        return "index";
    }

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signup(Model model) {
        User user = new User();

        model.addAttribute("user", user);

        return "signup";
    }
	//Antes tenia @ModelAttribute("user")
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> signupPost(@RequestBody User user, Model model) {

        System.out.println("Valor de User " + user);
        if(userService.checkUserExists(user.getUsername(), user.getEmail()))  {
            System.out.println("Existe un usuario con username o email");
            HttpHeaders headers = new HttpHeaders();
            headers.add("hee", "ok");

            return new ResponseEntity<User>(headers, HttpStatus.CONFLICT);


        } else {
        	 Set<UserRole> userRoles = new HashSet<>();
            System.out.println("El Usuario es correcto");

             userRoles.add(new UserRole(user, roleDao.findByName("ROLE_USER")));
            System.out.println("User Roles " + userRoles);
            User userCreated =userService.createUser(user, userRoles);
          //  HttpHeaders headers = new HttpHeaders();
            return new ResponseEntity<User>(userCreated, HttpStatus.CREATED);
        }

    }


	@RequestMapping("/userFront")
    @ResponseStatus(HttpStatus.ACCEPTED)
	public User userFront(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
      /*  PrimaryAccount primaryAccount = user.getPrimaryAccount();
        SavingsAccount savingsAccount = user.getSavingsAccount();*/
/*
        model.addAttribute("primaryAccount", primaryAccount);
        model.addAttribute("savingsAccount", savingsAccount);*/
        System.out.println("EL LOGIN FUE ACEPTADO");
        return user;
    }

    @RequestMapping(value="/salida", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void logoutPage () {
      //  User user = userService.findByUsername(principal.getName());
        System.out.println("EL  LOGOUT FUE ACEPTADO");

    }

    @RequestMapping(value="/errorLogin", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String errorLoginPage () {
        //  User user = userService.findByUsername(principal.getName());
        return "Fail Username or Password";
    }

    @RequestMapping(value="/errorSignUp", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String errorSignUpPage () {
        //  User user = userService.findByUsername(principal.getName());
        return " Email and User exist please SignUp Again";
    }
}
