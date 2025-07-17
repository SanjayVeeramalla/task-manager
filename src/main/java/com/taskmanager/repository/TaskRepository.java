// This is a placeholder for TaskRepository.java
// src/main/java/com/taskmanager/repository/TaskRepository.java
package com.taskmanager.repository;

import com.taskmanager.model.Task;
import com.taskmanager.model.TaskStatus;
import com.taskmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserOrderByCreatedAtDesc(User user);
    List<Task> findByUserAndStatusOrderByCreatedAtDesc(User user, TaskStatus status);
    
    @Query("SELECT t FROM Task t WHERE t.user = :user AND t.deadline BETWEEN :start AND :end")
    List<Task> findTasksWithUpcomingDeadlines(@Param("user") User user, 
                                              @Param("start") LocalDateTime start, 
                                              @Param("end") LocalDateTime end);
}

