package com.example.demo.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailRequest {
    @JsonProperty("tutorialId")
    @NotBlank(message = "L'email ne peut pas être vide")
    @Email(message = "Email invalide")
    private long tutorialId;

    @JsonProperty("email")
    private String email;


}

