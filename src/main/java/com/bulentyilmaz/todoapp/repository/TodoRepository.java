package com.bulentyilmaz.todoapp.repository;

import com.bulentyilmaz.todoapp.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    @Query("select t from Todo t where t.description=?1")
    List<Todo> findTodosByDescription(String description);
    @Query("select t from Todo t where t.description=?1 and t.dueDate=?2")
    List<Todo> findTodosByDescriptionAndDueDate(String description, LocalDate dueDate);

    @Query("select t from Todo t where t.dueDate=?1")
    List<Todo> findTodosByDueDate(LocalDate dueDate);
}
