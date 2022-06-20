package com.bulentyilmaz.todoapp.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Entity
@Table(name="todo")
public class Todo {

    @Id
    @SequenceGenerator(name="todo_sequence", sequenceName = "todo_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_sequence")
    @Column(name="id")
    private Long id;
    @Column(name="description")
    private String description;
    @Column(name="due_date")
    private LocalDate dueDate;

    //created at, updated at

    public Todo(Long id, String description) {
        this.id = id;
        this.description = description;
        this.dueDate = null;
    }

    @Override
    public String toString() {
        return "[ID: " + id+"]\t[Description: " + description+"]\t[Due Date: " + dueDate.getDayOfMonth()
                + "/" + dueDate.getMonth()+"/"+ dueDate.getYear();
    }

    @Override
    public boolean equals(Object o) {
        if(o==null) {
            return false;
        }
        else if(!(o instanceof Todo)) {
            return false;
        }
        else {
            Todo t = (Todo)o;
            return id.equals(t.id) && description.equals(t.description) && dueDate.equals(t.dueDate);
        }
    }
}
