package no.kristiania.http.controllers;

import no.kristiania.dao.daos.TaskMemberDao;
import no.kristiania.dao.daos.UserDao;
import no.kristiania.dao.objects.TaskMember;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskMembersController extends AbstractDaoController {

    private final TaskMemberDao taskMemberDao;
    private final UserDao userDao;

    public TaskMembersController(TaskMemberDao taskMemberDao, UserDao userDao) {
        this.userDao = userDao;
        this.taskMemberDao = taskMemberDao;
    }

    @Override
    public void handle(String requestAction, String requestTarget, Map<String, String> query, String body, OutputStream out) throws IOException {
        setUrlQuery(HttpMessage.getQueryString(requestTarget));
        try {
            if(requestAction.equals("POST")){
                query = HttpMessage.parseQueryString(body);
                setUrlQuery(query.get("taskid"));

                String taskId = query.get("taskid");
                String userId = URLDecoder.decode(query.get("member"), StandardCharsets.UTF_8);
                String projectId = query.get("projectid");
                TaskMember taskMember =  new TaskMember();
                taskMember.setTaskId(Long.parseLong(taskId));
                taskMember.setUserId(Long.parseLong(userId.substring(userId.indexOf('#')+1).trim()));
                taskMember.setProjectId(Long.parseLong(projectId));
                if(taskMemberDao.listMembersOf(Long.parseLong(taskId)).contains(taskMember)){
                    clientErrorResponse(out, "Member is already part of this task!", 409);
                    return;
                } else {
                    taskMemberDao.insert(taskMember);
                    serverRedirectResponse(query, out,
                            "http://localhost:8080/task.html?projectid=" + projectId + "&taskid=" + taskId);
                    return;
                }
            }
            serverResponse(query, out);
        } catch (SQLException e) {
            serverErrorResponse(out,e);
        }
    }

    public String getBody() throws SQLException {
        String urlQuery = super.getUrlQuery();
        Map<String, String> query = HttpMessage.parseQueryString(urlQuery);
        long taskId;
        if(query.size() != 0){
            taskId = Long.parseLong(query.get("taskid"));
        } else {
            taskId = Long.parseLong(urlQuery);
        }
        return taskMemberDao.listMembersOf(taskId).stream()
                .map(tm -> {
                    try {
                        long userId = tm.getUserId();
                        return String.format("<li id=%s>%s</li>",
                                tm.getUserId(), userDao.getUserById(userId).getName());
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return "Internal Server Error - 500";
                    }
                })
                .collect(Collectors.joining(""));
    }

}
