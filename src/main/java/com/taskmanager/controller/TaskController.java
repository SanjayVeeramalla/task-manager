package com.taskmanager.controller;

import com.taskmanager.model.Task;
import com.taskmanager.model.TaskStatus;
import com.taskmanager.model.User;
import com.taskmanager.service.TaskService;
import com.taskmanager.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.web.csrf.CsrfToken;

import java.util.List;
import java.util.Optional;

@Controller
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @GetMapping("/tasks/{id}")
public String viewTask(@PathVariable Long id, Model model) {
    Task task = taskService.getTaskById(id)
                           .orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));
    model.addAttribute("task", task);
    return "task-view";
}


    @ModelAttribute
    public void addCsrfToken(CsrfToken token, Model model) {
        model.addAttribute("_csrf", token);
    }

    // Utility method to get the current logged-in user
    private User getLoggedInUser(Authentication auth) {
        return userService.findByUsername(auth.getName()).orElse(null);
    }

    // Dashboard showing tasks
    @GetMapping("/dashboard")
public String dashboard(Model model, Authentication auth,
                        @RequestParam(required = false) String filter) {

    User user = getLoggedInUser(auth);
    if (user == null) return "redirect:/login";

    List<Task> tasks;
    if ("completed".equals(filter)) {
        tasks = taskService.getTasksByUserAndStatus(user, TaskStatus.COMPLETED);
    } else if ("pending".equals(filter)) {
        tasks = taskService.getTasksByUserAndStatus(user, TaskStatus.PENDING);
    } else {
        tasks = taskService.getAllTasksByUser(user);
    }

    List<Task> upcomingTasks = taskService.getUpcomingTasks(user);

    // ➕ Calculate task stats
    long totalTasks = taskService.getAllTasksByUser(user).size();
    long completedTasks = taskService.getTasksByUserAndStatus(user, TaskStatus.COMPLETED).size();
    long pendingTasks = taskService.getTasksByUserAndStatus(user, TaskStatus.PENDING).size();
    long upcomingCount = upcomingTasks.size();

    model.addAttribute("tasks", tasks);
    model.addAttribute("upcomingTasks", upcomingTasks);
    model.addAttribute("totalTasks", totalTasks);
    model.addAttribute("completedTasks", completedTasks);
    model.addAttribute("pendingTasks", pendingTasks);
    model.addAttribute("upcomingTasks", upcomingCount);
    model.addAttribute("currentFilter", filter);
    model.addAttribute("user", user);

    return "dashboard";
}


    // Show new task form
    // @GetMapping("/tasks/new")
    // public String newTask(Model model) {
    //     model.addAttribute("task", new Task());
    //     return "task-form";
    // }

    @GetMapping("/tasks/new")
public String newTask(Model model, Authentication auth) {
    // add this line:
    User user = getLoggedInUser(auth);
    model.addAttribute("user", user);

    model.addAttribute("task", new Task());
    return "task-form";
}

    // Handle new task creation
    @PostMapping("/tasks")
    public String createTask(@Valid @ModelAttribute("task") Task task,
                             BindingResult result,
                             Authentication auth) {

        if (result.hasErrors()) {
            return "task-form";
        }

        User user = getLoggedInUser(auth);
        if (user == null) return "redirect:/login";

        task.setUser(user);
        taskService.createTask(task);

        return "redirect:/dashboard";
    }

    // Show edit form
    // @GetMapping("/tasks/{id}/edit")
    // public String editTask(@PathVariable Long id, Model model, Authentication auth) {
    //     Optional<Task> taskOpt = taskService.getTaskById(id);
    //     User user = getLoggedInUser(auth);

    //     if (taskOpt.isPresent() && user != null) {
    //         Task task = taskOpt.get();
    //         if (task.getUser().getId().equals(user.getId())) {
    //             model.addAttribute("task", task);
    //             return "task-form";
    //         }
    //     }
    //     return "redirect:/dashboard";
    // }

    @GetMapping("/tasks/{id}/edit")
public String editTask(@PathVariable Long id, Model model, Authentication auth) {
    User user = getLoggedInUser(auth);
    model.addAttribute("user", user);

    Optional<Task> taskOpt = taskService.getTaskById(id);
    if (taskOpt.isPresent() && user != null) {
        Task task = taskOpt.get();
        if (task.getUser().getId().equals(user.getId())) {
            model.addAttribute("task", task);
            return "task-form";
        }
    }
    return "redirect:/dashboard";
}

    // Handle task update
    @PostMapping("/tasks/{id}")
    public String updateTask(@PathVariable Long id,
                             @Valid @ModelAttribute("task") Task task,
                             BindingResult result,
                             Authentication auth) {

        if (result.hasErrors()) {
            return "task-form";
        }

        User user = getLoggedInUser(auth);
        Optional<Task> existingTaskOpt = taskService.getTaskById(id);

        if (existingTaskOpt.isPresent() && user != null) {
            Task existingTask = existingTaskOpt.get();
            if (existingTask.getUser().getId().equals(user.getId())) {
                task.setId(id);
                task.setUser(user);
                task.setCreatedAt(existingTask.getCreatedAt());
                taskService.updateTask(task);
            }
        }

        return "redirect:/dashboard";
    }

    // Handle task deletion
    @PostMapping("/tasks/{id}/delete")
    public String deleteTask(@PathVariable Long id, Authentication auth) {
        Optional<Task> taskOpt = taskService.getTaskById(id);
        User user = getLoggedInUser(auth);

        if (taskOpt.isPresent() && user != null) {
            Task task = taskOpt.get();
            if (task.getUser().getId().equals(user.getId())) {
                taskService.deleteTask(id);
            }
        }
        return "redirect:/dashboard";
    }

    // Toggle status between PENDING and COMPLETED
    @PostMapping("/tasks/{id}/toggle-status")
    public String toggleTaskStatus(@PathVariable Long id, Authentication auth) {
        Optional<Task> taskOpt = taskService.getTaskById(id);
        User user = getLoggedInUser(auth);

        if (taskOpt.isPresent() && user != null) {
            Task task = taskOpt.get();
            if (task.getUser().getId().equals(user.getId())) {
                task.setStatus(task.getStatus() == TaskStatus.PENDING ?
                               TaskStatus.COMPLETED : TaskStatus.PENDING);
                taskService.updateTask(task);
            }
        }
        return "redirect:/dashboard";
    }
}
