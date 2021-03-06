package org.wrkr.clb.repo.project;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.project.ProjectMeta;
import org.wrkr.clb.model.project.imprt.jira.ImportedJiraProjectMeta;
import org.wrkr.clb.model.project.roadmap.RoadMeta;
import org.wrkr.clb.model.project.task.TaskMeta;
import org.wrkr.clb.model.user.UserMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.project.ProjectDocMapper;
import org.wrkr.clb.repo.mapper.project.ProjectNameAndUiIdMapper;
import org.wrkr.clb.repo.mapper.project.ProjectWithEverythingForReadAndSecurityMembershipMapper;
import org.wrkr.clb.repo.mapper.project.ProjectWithEverythingForReadMapper;
import org.wrkr.clb.repo.security.SecurityProjectRepo;

@Repository
public class JDBCProjectRepo extends JDBCBaseMainRepo {

    private static final String EXISTS_BY_ID = "SELECT 1 FROM " + ProjectMeta.TABLE_NAME + " " +
            "WHERE " + ProjectMeta.id + " = ?;"; // 1

    private static final String SELECT_PROJECT_ID_BY_UI_ID = "SELECT " + ProjectMeta.id + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "WHERE " + ProjectMeta.uiId + " = ?;"; // 1

    private static final String SELECT_UI_ID_BY_ID = "SELECT " + ProjectMeta.uiId + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "WHERE " + ProjectMeta.id + " = ?;"; // 1

    private static final String SELECT_PROJECT_ID_BY_ROAD_ID = "SELECT " + RoadMeta.projectId + " " +
            "FROM " + RoadMeta.TABLE_NAME + " " +
            "WHERE " + RoadMeta.id + " = ?;"; // 1

    private static final ProjectNameAndUiIdMapper NAME_AND_UI_ID_MAPPER = new ProjectNameAndUiIdMapper(ProjectMeta.TABLE_NAME);

    private static final String SELECT_NAME_AND_UI_ID_BY_TASK_ID = "SELECT " +
            NAME_AND_UI_ID_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "INNER JOIN " + TaskMeta.TABLE_NAME + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = " +
            TaskMeta.TABLE_NAME + "." + TaskMeta.projectId + " " +
            "WHERE " + TaskMeta.TABLE_NAME + "." + TaskMeta.id + " = ?;"; // 1

    private static final ProjectDocMapper PROJECT_DOC_MAPPER = new ProjectDocMapper();

    private static final String SELECT_BY_ID_FOR_DOCUMENTATION = "SELECT " +
            PROJECT_DOC_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "WHERE " + ProjectMeta.id + " = ?;"; // 1

    private static final String SELECT_BY_UI_ID_FOR_DOCUMENTATION = "SELECT " +
            PROJECT_DOC_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "WHERE " + ProjectMeta.uiId + " = ?;"; // 1

    private static final ProjectWithEverythingForReadMapper PROJECT_WITH_EVERYTHING_FOR_READ_MAPPER = new ProjectWithEverythingForReadMapper(
            ProjectMeta.TABLE_NAME);

    private static final String SELECT_BY_ID_WITH_EVERYTHING_FOR_READ = "SELECT " +
            PROJECT_WITH_EVERYTHING_FOR_READ_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "WHERE " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = ?;"; // 1

    private static final ProjectWithEverythingForReadAndSecurityMembershipMapper PROJECT_WITH_EVERYTHING_FOR_READ_AND_SECURITY_MEMBERSHIP_MAPPER = new ProjectWithEverythingForReadAndSecurityMembershipMapper(
            ProjectMeta.TABLE_NAME, ProjectMemberMeta.DEFAULT, UserMeta.DEFAULT);

    private static final String SELECT_BY_ID_WITH_EVERYTHING_FOR_READ_AND_FETCH_MEMBERSHIP_FOR_SECURITY = "SELECT " +
            PROJECT_WITH_EVERYTHING_FOR_READ_AND_SECURITY_MEMBERSHIP_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            SecurityProjectRepo.JOIN_NOT_FIRED_PROJECT_MEMBER_AND_USER_ON_PROJECT_ID_AND_USER_ID + " " +
            "WHERE " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = ?;"; // 2

    private static final ProjectNameAndUiIdMapper PROJECT_NAME_AND_UI_ID_MAPPER = new ProjectNameAndUiIdMapper(
            ProjectMeta.TABLE_NAME);

    private static final String SELECT_IMPORTED_FROM_JIRA = "SELECT " +
            PROJECT_NAME_AND_UI_ID_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ProjectMeta.TABLE_NAME + " " +
            "INNER JOIN " + ImportedJiraProjectMeta.TABLE_NAME + " " +
            "ON " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = " +
            ImportedJiraProjectMeta.TABLE_NAME + "." + ImportedJiraProjectMeta.projectId;

    private static final String UPDATE_FROZEN_BY_ID = "UPDATE " + ProjectMeta.TABLE_NAME + " " +
            "SET " + ProjectMeta.frozen + " = ? " +
            "WHERE " + ProjectMeta.id + " = ?;";

    public boolean exists(Long projectId) {
        return exists(EXISTS_BY_ID, projectId);
    }

    public Long getProjectIdByUiId(String projectUiId) {
        return queryForObjectOrNull(SELECT_PROJECT_ID_BY_UI_ID, Long.class, projectUiId);
    }

    public Project getUiIdById(Long projectId) {
        String uiId = queryForObjectOrNull(SELECT_UI_ID_BY_ID, String.class, projectId);
        if (uiId == null) {
            return null;
        }

        Project project = new Project();
        project.setId(projectId);
        project.setUiId(uiId);
        return project;

    }

    public Long getProjectIdByRoadId(Long roadId) {
        return queryForObjectOrNull(SELECT_PROJECT_ID_BY_ROAD_ID, Long.class, roadId);
    }

    public Project getNameAndUiIdByTaskId(Long taskId) {
        return queryForObjectOrNull(SELECT_NAME_AND_UI_ID_BY_TASK_ID, NAME_AND_UI_ID_MAPPER,
                taskId);
    }

    public Project getByIdForDocumentation(Long projectId) {
        return queryForObjectOrNull(SELECT_BY_ID_FOR_DOCUMENTATION, PROJECT_DOC_MAPPER,
                projectId);
    }

    public Project getByUiIdForDocumentation(String projectUiId) {
        return queryForObjectOrNull(SELECT_BY_UI_ID_FOR_DOCUMENTATION, PROJECT_DOC_MAPPER,
                projectUiId);
    }

    public Project getByIdWithEverythingForRead(Long projectId) {
        return queryForObjectOrNull(SELECT_BY_ID_WITH_EVERYTHING_FOR_READ, PROJECT_WITH_EVERYTHING_FOR_READ_MAPPER,
                projectId);
    }

    public Project getByIdWithEverythingForReadAndFetchMembershipForSecurity(Long projectId, Long userId) {
        return queryForObjectOrNull(SELECT_BY_ID_WITH_EVERYTHING_FOR_READ_AND_FETCH_MEMBERSHIP_FOR_SECURITY,
                PROJECT_WITH_EVERYTHING_FOR_READ_AND_SECURITY_MEMBERSHIP_MAPPER,
                userId, projectId);
    }

    public List<Project> listImportedFromJira() {
        return queryForList(SELECT_IMPORTED_FROM_JIRA, PROJECT_NAME_AND_UI_ID_MAPPER);
    }

    @Deprecated
    public void updateFrozen(Project project) {
        updateSingleRow(UPDATE_FROZEN_BY_ID, project.isFrozen(), project.getId());
    }
}
