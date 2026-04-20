package com.fisioterapiakinevid.kinevid.config.security;

import com.fisioterapiakinevid.kinevid.rest.repository.u.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 15/02/2026
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        var user = userRepository.findByUsernameAuthentication(username.toLowerCase().trim())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + username));

        // Query 2: Todos los nombres de roles activos (1 query plana)
        List<String> roleNames = userRepository.findRoleNamesByUserId(user.getId());

        // Query 3: Todos los nombres de permisos activos (1 query plana â€” antes era N queries)
        List<String> permissionNames = userRepository.findPermissionNamesByUserId(user.getId());

        // Combinar roles + permisos en la lista de GrantedAuthority
        List<SimpleGrantedAuthority> authorities = Stream
                .concat(roleNames.stream(), permissionNames.stream())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        log.debug("Usuario autenticado: {} | Roles: {} | Permisos: {}",
                user.getUsername(), roleNames.size(), permissionNames.size());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities);
    }
}

