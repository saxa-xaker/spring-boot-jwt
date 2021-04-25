package ru.rcaltd.springBootJwt.controllers.v0;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.rcaltd.springBootJwt.entities.users.User;
import ru.rcaltd.springBootJwt.repository.RoleRepository;
import ru.rcaltd.springBootJwt.repository.UserRepository;
import ru.rcaltd.springBootJwt.services.UserService;

import java.util.Arrays;

@RestController
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
        return "Yo!";
    }

    @PostMapping("/signin")
    public String signin(@RequestBody User user) {
        return userService.signin(user.getUsername(), user.getPassword());
    }

    @PostMapping("/signup")
    public String signup(@RequestBody User user) {

        // Set to new user "USER" role, its can be changed in future.
        user.setRoles(Arrays.asList(roleRepository.findByName("USER")));

        user.setEnabled(true);
        return userService.signup(user);
    }
}
