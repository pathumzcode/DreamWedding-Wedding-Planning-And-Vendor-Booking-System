package com.wedding.dreamwedding.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "useractions")
public class UserAction {
    @Id
    private String id;
    private String userId;
    private String userEmail;
    private String action; // e.g., "LOGIN", "LOGOUT"
    private LocalDateTime timestamp;
    private String role;
}
