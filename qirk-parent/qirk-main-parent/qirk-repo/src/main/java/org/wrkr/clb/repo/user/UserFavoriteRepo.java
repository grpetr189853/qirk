package org.wrkr.clb.repo.user;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.Project_;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.model.user.UserFavorite;
import org.wrkr.clb.model.user.UserFavorite_;
import org.wrkr.clb.model.user.User_;
import org.wrkr.clb.repo.JPADeletingRepo;

@Repository
public class UserFavoriteRepo extends JPADeletingRepo<UserFavorite> {

    @Override
    public UserFavorite get(Long id) {
        return get(UserFavorite.class, id);
    }

    public UserFavorite getFirstByUser(User user) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<UserFavorite> query = cb.createQuery(UserFavorite.class);

        Root<UserFavorite> root = query.from(UserFavorite.class);
        root.fetch(UserFavorite_.project, JoinType.LEFT);

        query.where(cb.equal(root.get(UserFavorite_.user), user), cb.isNull(root.get(UserFavorite_.previous)));
        return getSingleResultOrNull(query);
    }

    public UserFavorite getByIdAndUserAndFetchNext(Long id, User user) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<UserFavorite> query = cb.createQuery(UserFavorite.class);

        Root<UserFavorite> root = query.from(UserFavorite.class);
        root.fetch(UserFavorite_.next, JoinType.LEFT);

        query.where(cb.equal(root.get(UserFavorite_.id), id), cb.equal(root.get(UserFavorite_.user), user));
        return getSingleResultOrNull(query);
    }

    public UserFavorite getByIdAndUserAndFetchPreviousAndNext(Long id, User user) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<UserFavorite> query = cb.createQuery(UserFavorite.class);

        Root<UserFavorite> root = query.from(UserFavorite.class);
        root.fetch(UserFavorite_.previous, JoinType.LEFT);
        root.fetch(UserFavorite_.next, JoinType.LEFT);

        query.where(cb.equal(root.get(UserFavorite_.id), id), cb.equal(root.get(UserFavorite_.user), user));
        return getSingleResultOrNull(query);
    }

    public UserFavorite getByIdAndUserAndFetchEverything(Long id, User user) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<UserFavorite> query = cb.createQuery(UserFavorite.class);

        Root<UserFavorite> root = query.from(UserFavorite.class);
        root.fetch(UserFavorite_.project, JoinType.LEFT);
        root.fetch(UserFavorite_.previous, JoinType.LEFT);
        root.fetch(UserFavorite_.next, JoinType.LEFT);

        query.where(cb.equal(root.get(UserFavorite_.id), id), cb.equal(root.get(UserFavorite_.user), user));
        return getSingleResultOrNull(query);
    }

    public UserFavorite getByUserAndProject(User user, Project project) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<UserFavorite> query = cb.createQuery(UserFavorite.class);

        Root<UserFavorite> root = query.from(UserFavorite.class);

        query.where(cb.equal(root.get(UserFavorite_.user), user), cb.equal(root.get(UserFavorite_.project), project));
        return getSingleResultOrNull(query);
    }

    public UserFavorite getByUserIdAndProjectAndFetchPreviousAndNext(Long userId, Project project) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<UserFavorite> query = cb.createQuery(UserFavorite.class);

        Root<UserFavorite> userFavoriteRoot = query.from(UserFavorite.class);
        userFavoriteRoot.fetch(UserFavorite_.previous, JoinType.LEFT);
        userFavoriteRoot.fetch(UserFavorite_.next, JoinType.LEFT);
        Join<UserFavorite, User> userJoin = userFavoriteRoot.join(UserFavorite_.user);

        query.where(cb.equal(userFavoriteRoot.get(UserFavorite_.project), project),
                cb.equal(userJoin.get(User_.id), userId));
        return getSingleResultOrNull(query);
    }

    public UserFavorite getByUserAndProjectIdAndFetchPreviousAndNext(User user, Long projectId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<UserFavorite> query = cb.createQuery(UserFavorite.class);

        Root<UserFavorite> userFavoriteRoot = query.from(UserFavorite.class);
        Join<UserFavorite, Project> projectJoin = userFavoriteRoot.join(UserFavorite_.project);
        userFavoriteRoot.fetch(UserFavorite_.previous, JoinType.LEFT);
        userFavoriteRoot.fetch(UserFavorite_.next, JoinType.LEFT);

        query.where(cb.equal(userFavoriteRoot.get(UserFavorite_.user), user),
                cb.equal(projectJoin.get(Project_.id), projectId));
        return getSingleResultOrNull(query);
    }

    public UserFavorite getByUserAndProjectUiIdAndFetchPreviousAndNext(User user, String projectUiId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<UserFavorite> query = cb.createQuery(UserFavorite.class);

        Root<UserFavorite> userFavoriteRoot = query.from(UserFavorite.class);
        Join<UserFavorite, Project> projectJoin = userFavoriteRoot.join(UserFavorite_.project);
        userFavoriteRoot.fetch(UserFavorite_.previous, JoinType.LEFT);
        userFavoriteRoot.fetch(UserFavorite_.next, JoinType.LEFT);

        query.where(cb.equal(userFavoriteRoot.get(UserFavorite_.user), user),
                cb.equal(projectJoin.get(Project_.uiId), projectUiId));
        return getSingleResultOrNull(query);
    }
}
