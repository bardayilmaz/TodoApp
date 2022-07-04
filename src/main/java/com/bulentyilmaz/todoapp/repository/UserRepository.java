package com.bulentyilmaz.todoapp.repository;

import com.bulentyilmaz.todoapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    public User findByEmail(String email);

    @Query("select u from User u where" +
            " (:firstName is null or u.firstName=:firstName) and" +
            " (:lastName is null or u.lastName=:lastName) and" +
            " (:email is null or u.email=:email) and" +
            " (:role is null or u.role=:role)")
    public List<User> findUsers(String firstName, String lastName, String email, Integer role);

}
