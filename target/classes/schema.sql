CREATE DATABASE IF NOT EXISTS smart_task_manager CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smart_task_manager;

-- v1 tables (kept compatible; status/priority are VARCHAR so new enum values need no ALTER)
CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  email VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_user_email (email),
  INDEX idx_user_username (username)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS projects (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  color VARCHAR(20) DEFAULT '#38bdf8',
  icon VARCHAR(20) DEFAULT '📁',
  user_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_projects_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_project_user (user_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tasks (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(150) NOT NULL,
  description TEXT,
  priority VARCHAR(20) NOT NULL DEFAULT 'Medium',
  start_date DATE NULL,
  deadline DATE NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'TODO',
  category VARCHAR(60),
  tags VARCHAR(200),
  estimated_minutes INT NULL,
  actual_minutes INT NOT NULL DEFAULT 0,
  notes TEXT,
  demo TINYINT(1) NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  user_id BIGINT NOT NULL,
  project_id BIGINT NULL,
  CONSTRAINT fk_tasks_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_tasks_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE SET NULL,
  INDEX idx_task_user (user_id),
  INDEX idx_task_status (status),
  INDEX idx_task_priority (priority),
  INDEX idx_task_deadline (deadline),
  INDEX idx_task_demo (demo)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS task_subtasks (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  task_id BIGINT NOT NULL,
  title VARCHAR(200) NOT NULL,
  done TINYINT(1) NOT NULL DEFAULT 0,
  CONSTRAINT fk_subtask_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
  INDEX idx_subtask_task (task_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS task_comments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  task_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  text TEXT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_comment_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
  CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_comment_task (task_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS focus_sessions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  task_id BIGINT NULL,
  kind VARCHAR(20) NOT NULL DEFAULT 'FOCUS',
  minutes_planned INT NOT NULL DEFAULT 25,
  minutes_actual INT NOT NULL DEFAULT 25,
  completed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_focus_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_focus_user (user_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS notifications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  title VARCHAR(150) NOT NULL,
  message TEXT,
  kind VARCHAR(30) DEFAULT 'INFO',
  is_read TINYINT(1) NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_notif_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_notif_user (user_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS achievements (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(60) NOT NULL UNIQUE,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(255),
  icon VARCHAR(10) DEFAULT '🏆',
  xp INT NOT NULL DEFAULT 10
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS user_achievements (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  achievement_id BIGINT NOT NULL,
  earned_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_ua_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_ua_ach FOREIGN KEY (achievement_id) REFERENCES achievements(id) ON DELETE CASCADE,
  UNIQUE KEY uq_user_achievement (user_id, achievement_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS task_activity_logs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  task_id BIGINT NULL,
  action VARCHAR(40) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_log_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_log_user_action (user_id, action, created_at)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS user_preferences (
  user_id BIGINT PRIMARY KEY,
  theme VARCHAR(10) DEFAULT 'dark',
  accent VARCHAR(20) DEFAULT '#38bdf8',
  focus_minutes INT DEFAULT 25,
  short_break_minutes INT DEFAULT 5,
  long_break_minutes INT DEFAULT 15,
  notify_overdue TINYINT(1) DEFAULT 1,
  CONSTRAINT fk_prefs_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Migration for databases created by v1 (status/priority values):
-- UPDATE tasks SET status='TODO' WHERE status='Pending';
-- UPDATE tasks SET status='IN_PROGRESS' WHERE status='In_Progress';
-- UPDATE tasks SET status='COMPLETED' WHERE status='Completed';
