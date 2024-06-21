package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table( name = "users", 
        uniqueConstraints = { 
          @UniqueConstraint(columnNames = "username"),
          @UniqueConstraint(columnNames = "email") 
        })
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(max = 20)
  private String username;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  @NotBlank
  @Size(max = 120)
  private String password;

  @NotBlank
  @Size(max = 10)
  private String uniqueIdentifier;

  private String organismCode;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "organism_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JsonIgnore
  private Organism organism;

  public User(String username, String email, String password, String uniqueIdentifier) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.uniqueIdentifier = uniqueIdentifier;

  }

  public String getOrganismCode() {
    return this.organism != null ? this.organism.getCode() : null;
  }

}
