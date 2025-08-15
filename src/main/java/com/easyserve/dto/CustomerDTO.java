
package com.easyserve.dto;

import jakarta.validation.constraints.*;

public class CustomerDTO {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    @NotBlank
    private String email;

    @Pattern(regexp = "\\+?[0-9. ()-]{7,25}", message = "Invalid phone number")
    @NotBlank
    private String phone;

    // Getters and setters
}

