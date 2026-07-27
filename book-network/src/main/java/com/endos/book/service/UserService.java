package com.endos.book.service;

import com.endos.book.common.PageResponse;
import com.endos.book.dto.response.UserResponse;
import org.springframework.security.core.Authentication;

public interface UserService {

    PageResponse<UserResponse> findAllUsers(int page, int size);

    UserResponse findUserById(Integer userId);

    void toggleAccountLock(Integer userId);

    void toggleAccountEnabled(Integer userId);

    UserResponse getOwnProfile(Authentication connectedUser);
}
