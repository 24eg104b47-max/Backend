package com.smarttaskmanager.service;

import com.smarttaskmanager.dto.ProjectDto;
import com.smarttaskmanager.entity.Project;
import com.smarttaskmanager.entity.Task;
import com.smarttaskmanager.entity.User;
import com.smarttaskmanager.exception.ResourceNotFoundException;
import com.smarttaskmanager.repository.ProjectRepository;
import com.smarttaskmanager.repository.TaskRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TaskService taskService;

    public ProjectService(ProjectRepository projectRepository, TaskRepository taskRepository,
                         TaskService taskService) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.taskService = taskService;
    }

    private ProjectDto toDto(Project p) {
        ProjectDto d = new ProjectDto();
        d.setId(p.getId()); d.setName(p.getName()); d.setDescription(p.getDescription());
        d.setColor(p.getColor()); d.setIcon(p.getIcon());
        List<com.smarttaskmanager.entity.Task> tasks =
                taskRepository.findByUserIdOrderByCreatedAtDesc(p.getOwner().getId())
                        .stream().filter(t -> t.getProject() != null
                                && t.getProject().getId().equals(p.getId())).toList();
        d.setTotalTasks(tasks.size());
        d.setCompletedTasks(tasks.stream().filter(t -> t.getStatus() == Task.Status.COMPLETED).count());
        return d;
    }

    public List<ProjectDto> list(String username) {
        User u = taskService.currentUser(username);
        return projectRepository.findByOwnerIdOrderByCreatedAtDesc(u.getId())
                .stream().map(this::toDto).toList();
    }

    public ProjectDto create(String username, ProjectDto dto) {
        User u = taskService.currentUser(username);
        Project p = new Project();
        p.setName(dto.getName()); p.setDescription(dto.getDescription());
        if (dto.getColor() != null) p.setColor(dto.getColor());
        if (dto.getIcon() != null) p.setIcon(dto.getIcon());
        p.setOwner(u);
        return toDto(projectRepository.save(p));
    }

    public ProjectDto update(String username, Long id, ProjectDto dto) {
        Project p = owned(username, id);
        p.setName(dto.getName()); p.setDescription(dto.getDescription());
        if (dto.getColor() != null) p.setColor(dto.getColor());
        if (dto.getIcon() != null) p.setIcon(dto.getIcon());
        return toDto(projectRepository.save(p));
    }

    public void delete(String username, Long id) {
        Project p = owned(username, id);
        taskRepository.findByUserIdOrderByCreatedAtDesc(p.getOwner().getId()).stream()
                .filter(t -> t.getProject() != null && t.getProject().getId().equals(id))
                .forEach(t -> t.setProject(null));
        projectRepository.delete(p);
    }

    private Project owned(String username, Long id) {
        Project p = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        User u = taskService.currentUser(username);
        if (!p.getOwner().getId().equals(u.getId()))
            throw new AccessDeniedException("Not your project");
        return p;
    }
}
