package com.endos.book.user;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private boolean accountLocked;
    private boolean enabled;
    private List<String> roles;
    private LocalDateTime createdDate;
}
