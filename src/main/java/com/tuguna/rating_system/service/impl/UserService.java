package com.tuguna.rating_system.service.impl;

import com.tuguna.rating_system.dto.user.UserResponse;
import com.tuguna.rating_system.exception.ApiException;
import com.tuguna.rating_system.exception.ErrorCode;
import com.tuguna.rating_system.model.entity.User.User;
import com.tuguna.rating_system.model.entity.User.UserSpecification;
import com.tuguna.rating_system.model.enums.Role;
import com.tuguna.rating_system.repository.CommentRepository;
import com.tuguna.rating_system.repository.UserRepository;
import com.tuguna.rating_system.service.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ModelMapper mapper;

    private static final Map<String, Sort> SORTING_MAP = Map.of(
            "rating_asc", Sort.by("averageRating").ascending(),
            "rating_desc", Sort.by("averageRating").descending(),
            "createdat_asc", Sort.by("createdAt").ascending(),
            "createdat_desc", Sort.by("createdAt").descending()
    );


    public List<UserResponse> getFilteredUsers(Double minRating, Double maxRating, Integer minReviews,String sort,String gameTitle) {
        Specification<User> spec = Specification.allOf(
                UserSpecification.isSeller(),
                UserSpecification.isEmailVerified(),
                UserSpecification.hasMinRating(minRating),
                UserSpecification.hasMaxRating(maxRating),
                UserSpecification.hasMinReviews(minReviews),
                UserSpecification.hasGameTitle(gameTitle)
        );
        String key = (sort == null) ? "" : sort.toLowerCase();
        Sort sorting = SORTING_MAP.getOrDefault(key, Sort.unsorted());

        List<User> users = userRepository.findAll(spec, sorting);
        return toResponseList(users);
    }

    public UserResponse getUserById(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "User not found with id: " + id));
        if (user.getRole() != Role.SELLER || !user.getEmailVerified()) {
            throw new ApiException(ErrorCode.ACCESS_DENIED, "Only verified sellers can be viewed");
        }
        return toResponse(user);
    }

    public User createUser(User user){
        return userRepository.save(user);
    }

    public void deleteUser(long id){
        userRepository.deleteById(id);
    }

    public List<UserResponse> adminGetAllUsers() {
        List<User> users = userRepository.findAll();
        return toResponseList(users);
    }

    public UserResponse adminGetUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "User not found with id: " + id));
        return toResponse(user);
    }

    @Transactional
    public UserResponse adminVerifyUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "User not found with id: " + id));
        user.setEmailVerified(true);
        return toResponse(user);
    }

    @Transactional
    public UserResponse adminChangeRole(Long id, String roleStr) {
        roleStr = roleStr.trim().toUpperCase();
        if (!roleStr.equals("ADMIN") && !roleStr.equals("SELLER")) {
            throw new ApiException(ErrorCode.INVALID_ROLE, "Invalid role. Allowed values: SELLER, ADMIN UPPERCASE!!!");
        }
        Role newRole = Role.valueOf(roleStr);
        User user = userRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, "User not found with id: " + id));
        user.setRole(newRole);
        return toResponse(user);
    }

    public void adminDeleteUser(Long id) {
        if (!userRepository.existsById(id)) {throw new ApiException(ErrorCode.USER_NOT_FOUND, "User not found with id: " + id);}
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

    private UserResponse toResponse(User user) {
        UserResponse dto = mapper.map(user, UserResponse.class);
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());

        dto.setAverageRating(user.getAverageRating());
        dto.setRatingsCount(user.getRatingsCount());

        return dto;
    }

    private List<UserResponse> toResponseList(List<User> users) {
        return users.stream()
                .map(this::toResponse)
                .toList();
    }
}
