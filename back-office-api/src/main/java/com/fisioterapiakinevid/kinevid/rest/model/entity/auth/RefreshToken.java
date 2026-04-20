package com.fisioterapiakinevid.kinevid.rest.model.entity.auth;

import com.fisioterapiakinevid.kinevid.rest.model.base.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 27/03/2026
 * Entidad para almacenar Refresh Tokens
 * Permite revocar tokens y controlar sesiones de usuarios
 */
@Builder
@Getter
@Setter
@ToString(exclude = "user")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "refresh_token",
        indexes = {
                @Index(name = "IDX_REFRESH_TOKEN_TOKEN", columnList = "token", unique = true),
                @Index(name = "IDX_REFRESH_TOKEN_USER_ID", columnList = "user_id"),
                @Index(name = "IDX_REFRESH_TOKEN_EXPIRY_DATE", columnList = "expiry_date")
        })
public class RefreshToken extends AuditableEntity implements Serializable {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "SEQ_REFRESH_TOKEN_ID_GENERATOR", sequenceName = "SEQ_REFRESH_TOKEN_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_REFRESH_TOKEN_ID_GENERATOR")
    private Long id;

    @Basic
    @Column(name = "token", nullable = false, unique = true, columnDefinition = "TEXT")
    private String token;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "FK_REFRESH_TOKEN_USER"))
    private User user;

    @Basic
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Basic
    @Column(name = "revoked", nullable = false)
    private Boolean revoked;

    @Basic
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Basic
    @Column(name = "user_agent", length = 255)
    private String userAgent;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    public boolean isValid() {
        return !revoked && !isExpired();
    }
}
