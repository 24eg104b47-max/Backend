package com.smarttaskmanager.service;

import com.smarttaskmanager.entity.Project;
import com.smarttaskmanager.entity.Task;
import com.smarttaskmanager.entity.User;
import com.smarttaskmanager.repository.ProjectRepository;
import com.smarttaskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class DemoService {
    private final TaskService taskService;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final NotificationService notificationService;

    public DemoService(TaskService taskService, TaskRepository taskRepository,
                       ProjectRepository projectRepository, NotificationService notificationService) {
        this.taskService = taskService;
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.notificationService = notificationService;
    }

    /** Seeds clearly-labeled demo records owned by the caller. Idempotent-ish: skips if demo rows exist. */
    public int seed(String username) {
        User u = taskService.currentUser(username);
        if (taskRepository.countByUserIdAndDemoTrue(u.getId()) > 0) return 0;
        Project p = new Project();
        p.setName("Demo Launch 🚀");
        p.setDescription("Sample project for the college demo — safe to reset.");
        p.setColor("#a78bfa"); p.setIcon("🎓"); p.setOwner(u);
        projectRepository.save(p);

        Object[][] rows = {
                { "Prepare demo slides", "10-slide story: problem, solution, architecture", "High", "TODO", -1, "College" },
                { "Rehearse 5-min pitch", "Time the Kanban + focus timer walkthrough", "Critical", "IN_PROGRESS", 0, "College" },
                { "Wire backend health check", "GET /api/health for the status indicator", "Medium", "IN_REVIEW", 1, "Engineering" },
                { "Polish landing hero", "Tune 3D lighting and headline copy", "Medium", "TODO", 2, "Design" },
                { "Seed sample analytics", "This workspace powers the charts", "Low", "COMPLETED", -3, "Engineering" },
                { "Fix overdue example", "Intentionally overdue to show warnings", "High", "TODO", -2, "College" },
        };
        int n = 0;
        for (Object[] r : rows) {
            Task t = new Task();
            t.setTitle((String) r[0]);
            t.setDescription((String) r[1]);
            t.setPriority(Task.Priority.valueOf((String) r[2]));
            t.setStatus(Task.Status.valueOf((String) r[3]));
            t.setDeadline(LocalDate.now().plusDays((Integer) r[4]));
            t.setCategory((String) r[5]);
            t.setEstimatedMinutes(45);
            t.setUser(u);
            t.setProject(p);
            t.setDemo(true);
            taskRepository.save(t);
            n++;
        }
        notificationService.notify(u, "Demo workspace ready",
                n + " sample tasks + 1 project were added. Reset anytime from the Demo page.", "DEMO");
        return n;
    }

    /** Deletes ONLY the caller's demo-flagged rows. Never touches real data. */
    public int reset(String username) {
        User u = taskService.currentUser(username);
        List<Task> demos = taskRepository.findByUserIdAndDemoTrue(u.getId());
        int n = demos.size();
        taskRepository.deleteAll(demos);
        return n;
    }
}
