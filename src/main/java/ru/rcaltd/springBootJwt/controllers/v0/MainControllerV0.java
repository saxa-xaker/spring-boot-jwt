package ru.rcaltd.springBootJwt.controllers.v0;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import ru.rcaltd.springBootJwt.entities.users.User;
import ru.rcaltd.springBootJwt.exception.CustomException;
import ru.rcaltd.springBootJwt.repository.RoleRepository;
import ru.rcaltd.springBootJwt.repository.UserRepository;
import ru.rcaltd.springBootJwt.services.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v0")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MainControllerV0 {

    final UserService userService;
    final RoleRepository roleRepository;
    final UserRepository userRepository;

    public MainControllerV0(UserService userService, RoleRepository roleRepository, UserRepository userRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    String getRoot() {
        return "Welcome to V0 api!";
    }

    @PostMapping("/signin")
    public String signin(@RequestBody User user) {
        return userService.signin(user.getUsername(), user.getPassword());
    }

    @PostMapping("/signup")
    public String signup(@RequestBody User user) {

        // Set to new user "USER" role, its can be changed in future.
        user.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER")));

        user.setEnabled(true);
        return userService.signup(user);
    }

    @PutMapping(value = "/update/{user}")
    public HttpStatus updateUser(@PathVariable User user, HttpServletRequest req) {
        if (!user.getUsername().equals(userService.whoami(req).getUsername())) {
            throw new CustomException("You not allowed to update another users", HttpStatus.METHOD_NOT_ALLOWED);
        }
        try {
            user.setId(userRepository.findByUsername(user.getUsername()).getId());
            userRepository.save(user);
        } catch (Exception e) {
            throw new CustomException("Update user failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return HttpStatus.OK;
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/delete/{username}")
    public HttpStatus deleteUser(@PathVariable String username) {
        try {
            userService.delete(username);
        } catch (UsernameNotFoundException ignored) {
            throw new CustomException("Delete user failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return HttpStatus.OK;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/getuserslist", produces = "application/json")
    public List<User> getUsersList() {
        return userRepository.findAll();
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/getuserbyusername/{username}", produces = "application/json")
    public User getUserByUsername(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new CustomException("User not found", HttpStatus.NOT_FOUND);
        }
        return user;
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/getuserbyid/{id}", produces = "application/json")
    public User getUserById(@PathVariable long id) throws CustomException {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new CustomException("User not found", HttpStatus.NOT_FOUND);
        }
        return user;
    }


    @GetMapping(value = "/whoami", produces = "application/json")
    public User whoAmI(HttpServletRequest req) {
        return userService.whoami(req);
    }


    @GetMapping(value = "/refreshtoken")
    public String refreshToken(HttpServletRequest req) {
        User user = userService.whoami(req);
        return userService.refresh(user);
    }
}
