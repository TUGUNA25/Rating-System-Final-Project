package com.tuguna.rating_system.model.entity.User;


import com.tuguna.rating_system.model.entity.Game;
import com.tuguna.rating_system.model.entity.GameObject;
import com.tuguna.rating_system.model.enums.Role;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> hasMinRating(Double value) {
        return (root, query, cb) -> {
            if (value == null) return null;
            return cb.greaterThanOrEqualTo(root.get("averageRating"), value);
        };
    }

    public static Specification<User> hasMaxRating(Double value) {
        return (root, query, cb) -> {
            if (value == null) return null;
            return cb.lessThanOrEqualTo(root.get("averageRating"), value);
        };
    }

    public static Specification<User> hasMinReviews(Integer value) {
        return (root, query, cb) -> {
            if (value == null) return null;
            return cb.greaterThanOrEqualTo(root.get("ratingsCount"), value);
        };
    }

    public static Specification<User> isSeller() {
        return (root, query, cb) ->
                cb.equal(root.get("role"), Role.SELLER);
    }

    public static Specification<User> isEmailVerified() {
        return (root, query, cb) ->
                cb.isTrue(root.get("emailVerified"));
    }

    public static Specification<User> hasGameTitle(String title) {
        return (root, query, cb) -> {
            if (title == null || title.isBlank()) return null;

            Join<User, GameObject> objects = root.join("objects", JoinType.LEFT);
            Join<GameObject, Game> game = objects.join("game", JoinType.LEFT);

            return cb.like(cb.lower(game.get("title")), "%" + title.toLowerCase() + "%");
        };
    }

}