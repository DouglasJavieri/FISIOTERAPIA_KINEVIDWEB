package com.fisioterapiakinevid.kinevid.rest.repository.auth;

import com.fisioterapiakinevid.kinevid.rest.model.entity.auth.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 27/03/2026
 * Repository para RefreshToken
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Query("SELECT rt " +
            "FROM RefreshToken rt " +
            "WHERE rt.token = :token AND rt.deleted = false")
    Optional<RefreshToken> findByToken(@Param("token") String token);

    @Query("SELECT rt FROM RefreshToken rt " +
            "WHERE rt.user.id = :userId " +
            "AND rt.revoked = false " +
            "AND rt.expiryDate > CURRENT_TIMESTAMP " +
            "AND rt.deleted = false " +
            "ORDER BY rt.createdBy DESC")
    List<RefreshToken> findValidTokensByUserId(@Param("userId") Long userId);

    @Query("SELECT rt " +
            "FROM RefreshToken rt " +
            "WHERE rt.user.id = :userId " +
            "AND rt.deleted = false " +
            "ORDER BY rt.createdBy DESC")
    List<RefreshToken> findAllTokensByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken rt SET rt.revoked = true " +
            "WHERE rt.user.id = :userId AND rt.revoked = false")
    int revokeAllUserTokens(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.token = :token")
    int revokeToken(@Param("token") String token);

    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < :expiryDate")
    int deleteExpiredTokens(@Param("expiryDate") LocalDateTime expiryDate);

    @Query("SELECT CASE WHEN COUNT(rt) > 0 THEN true ELSE false END " +
            "FROM RefreshToken rt " +
            "WHERE rt.token = :token " +
            "AND rt.revoked = false " +
            "AND rt.expiryDate > CURRENT_TIMESTAMP " +
            "AND rt.deleted = false")
    boolean existsValidToken(@Param("token") String token);

    @Query("SELECT COUNT(rt) FROM RefreshToken rt " +
            "WHERE rt.user.id = :userId " +
            "AND rt.revoked = false " +
            "AND rt.expiryDate > CURRENT_TIMESTAMP " +
            "AND rt.deleted = false")
    long countValidTokensByUserId(@Param("userId") Long userId);
}
