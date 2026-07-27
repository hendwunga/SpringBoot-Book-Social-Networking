package com.endos.book.user;

import com.endos.book.common.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public PageResponse<UserResponse> findAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<User> users = userRepository.findAll(pageable);
        List<UserResponse> userResponse = users.stream()
                .map(this::toUserResponse)
                .toList();
        return new PageResponse<>(
                userResponse,
                users.getNumber(),
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages(),
                users.isFirst(),
                users.isLast()
        );
    }

    public UserResponse findUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return toUserResponse(user);
    }

    public void toggleAccountLock(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setAccountLocked(!user.isAccountLocked());
        userRepository.save(user);
    }

    public void toggleAccountEnabled(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    public UserResponse getOwnProfile(Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        return toUserResponse(user);
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .accountLocked(user.isAccountLocked())
                .enabled(user.isEnabled())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName())
                        .toList())
                .createdDate(user.getCreatedDate())
                .build();
    }
}
