package com.wedding.dreamwedding.dto;

import lombok.Data;

@Data
public class CustomerProfileUpdateRequest {
    private String firstName;
    private String lastName;
    private String password; // optional new password
}
