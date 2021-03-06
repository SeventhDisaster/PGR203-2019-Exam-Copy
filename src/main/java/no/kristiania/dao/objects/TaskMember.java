package no.kristiania.dao.objects;

import java.util.Objects;

public class TaskMember {

    private long taskId;
    private long userId;
    private long projectId;

    public TaskMember() {}

    public TaskMember(long taskId, long projectId, long userId) {
        this.taskId = taskId;
        this.projectId = projectId;
        this.userId = userId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setUserId(long userId) { this.userId = userId; }

    public long getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "TaskMember{" +
                "taskId=" + taskId +
                ", projectId=" + projectId +
                ", userId=" + userId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskMember that = (TaskMember) o;
        return taskId == that.taskId &&
                userId == that.userId &&
                projectId == that.projectId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, userId, projectId);
    }
}
