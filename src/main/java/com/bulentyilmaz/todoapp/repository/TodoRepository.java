package com.bulentyilmaz.todoapp.repository;

import com.bulentyilmaz.todoapp.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    @Query("select t from Todo t where t.description=?1")
    Optional<Todo> findTodoByDescription(String description);

}
