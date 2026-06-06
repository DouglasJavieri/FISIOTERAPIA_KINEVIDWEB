package com.fisioterapiakinevid.kinevid.config.security;

import org.springframework.stereotype.Component;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 17/02/2026
 */
@Component("auditorAwareImpl")
public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {

            return Optional.of("SYSTEM");
        }

        return Optional.ofNullable(authentication.getName());
    }
}
