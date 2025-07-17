// This is a placeholder for TaskService.java
// src/main/java/com/taskmanager/service/TaskService.java
package com.taskmanager.service;

import com.taskmanager.model.Task;
import com.taskmanager.model.TaskStatus;
import com.taskmanager.model.User;
import com.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }
    
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }
    
    public List<Task> getAllTasksByUser(User user) {
        return taskRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    public List<Task> getTasksByUserAndStatus(User user, TaskStatus status) {
        return taskRepository.findByUserAndStatusOrderByCreatedAtDesc(user, status);
    }
    
    public Task updateTask(Task task) {
        return taskRepository.save(task);
    }
    
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
    
    public List<Task> getUpcomingTasks(User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextWeek = now.plusDays(7);
        return taskRepository.findTasksWithUpcomingDeadlines(user, now, nextWeek);
    }
}

