package com.userfront.controller;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.userfront.dao.RoleDao;
import com.userfront.domain.PrimaryAccount;
import com.userfront.domain.SavingsAccount;
import com.userfront.domain.User;
import com.userfront.domain.security.UserRole;
import com.userfront.service.UserService;


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
    public User signupPost(@RequestBody User user, Model model) {

        System.out.println("Valor de User " + user);
        if(userService.checkUserExists(user.getUsername(), user.getEmail()))  {

            if (userService.checkEmailExists(user.getEmail())) {
                model.addAttribute("emailExists", true);
            }

            if (userService.checkUsernameExists(user.getUsername())) {
                model.addAttribute("usernameExists", true);
            }

            return user;
        } else {
        	 Set<UserRole> userRoles = new HashSet<>();
            System.out.println("Role Dao Result: " + roleDao.findByName("ROLE_USER"));

             userRoles.add(new UserRole(user, roleDao.findByName("ROLE_USER")));
            System.out.println("User Roles " + userRoles);
            User userCreated =userService.createUser(user, userRoles);

            return userCreated;
        }
    }

	@RequestMapping("/userFront")
    @ResponseStatus(HttpStatus.ACCEPTED)
	public User userFront(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
        PrimaryAccount primaryAccount = user.getPrimaryAccount();
        SavingsAccount savingsAccount = user.getSavingsAccount();

        model.addAttribute("primaryAccount", primaryAccount);
        model.addAttribute("savingsAccount", savingsAccount);
        System.out.println("EL LOGIN FUE ACEPTADO");
        return user;
    }
}
