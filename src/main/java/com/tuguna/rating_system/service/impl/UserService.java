package com.tuguna.rating_system.service.impl;

import com.tuguna.rating_system.model.entity.Comment;
import com.tuguna.rating_system.model.entity.User;
import com.tuguna.rating_system.model.enums.CommentStatus;
import com.tuguna.rating_system.repository.CommentRepository;
import com.tuguna.rating_system.repository.UserRepository;
import com.tuguna.rating_system.service.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public UserService(UserRepository userRepository,CommentRepository commentRepository){
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUserById(long id){
        return userRepository.findById(id).orElse(null);
    }

    public User createUser(User user){
        return userRepository.save(user);
    }

    public void deleteUser(long id){
        userRepository.deleteById(id);
    }

    public CustomUserDetails getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails;
        }

        if (principal instanceof String s && s.equals("anonymousUser")) {
            return null;
        }

        return null;
    }

    @Transactional
    public void updateSellerRating(User seller) {

        long count = commentRepository.countApproved(seller.getId());
        Double avg = commentRepository.averageApproved(seller.getId());

        if (count == 0) {
            seller.setAverageRating(0.0);
            seller.setRatingsCount(0);
        } else {
            seller.setAverageRating(avg);
            seller.setRatingsCount((int) count);
        }

    }
}
