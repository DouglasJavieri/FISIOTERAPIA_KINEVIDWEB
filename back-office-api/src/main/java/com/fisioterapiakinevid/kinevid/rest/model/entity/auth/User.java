package com.fisioterapiakinevid.kinevid.rest.model.entity.auth;

import com.fisioterapiakinevid.kinevid.rest.model.base.AuditableEntity;
import com.fisioterapiakinevid.kinevid.rest.model.enums.auth.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 01/02/2026
 */
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User extends AuditableEntity implements Serializable {
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "SEQ_USER_ID_GENERATOR", sequenceName = "SEQ_USER_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_USER_ID_GENERATOR")
    private Long id;

    @Basic
    @Column(name = "username", nullable = false, length = 30, unique = true)
    private String username;

    @Basic
    @Column(name = "email", nullable = false, length = 50, unique = true)
    private String email;

    @Basic
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "state_user", length = 30)
    private UserStatus status;
}

