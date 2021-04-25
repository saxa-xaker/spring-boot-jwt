package ru.rcaltd.springBootJwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.rcaltd.springBootJwt.entities.users.Role;
import ru.rcaltd.springBootJwt.entities.users.User;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);

    List<Role> findByUsers(User user);
}
