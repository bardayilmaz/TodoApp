package com.bulentyilmaz.todoapp.repository;

import com.bulentyilmaz.todoapp.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    //@Query("select t from Todo t where (:description is null or t.description=:description) and (cast(:dueDate as date) is null or t.dueDate=:dueDate)")
    @Query(value="select * from todo where" +
            " (:description is null or description=:description) and (cast(:dueDate as date) is null or due_date=:dueDate)"
            , nativeQuery = true)
    List<Todo> findTodos(String description, LocalDate dueDate);
}
