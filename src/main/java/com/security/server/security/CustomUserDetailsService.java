package com.security.server.security;

import com.security.server.entity.*;
import com.security.server.repository.RolePermissionRepository;
import com.security.server.repository.UserRepository;
import com.security.server.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = userRepository
                .findByUsernameOrEmail(identifier, identifier)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));

        List<UserRole> userRoles = userRoleRepository.findByUser(user);

        Set<GrantedAuthority> authorities = new HashSet<>();

        for (UserRole userRole : userRoles){
            Role role = userRole.getRole();

            authorities.add(new SimpleGrantedAuthority("Role_" + role.getRoleName()));

            List<RolePermission> rolePermissions = rolePermissionRepository.findByRole(role);

            for (RolePermission rp : rolePermissions){
                Permission permission = rp.getPermission();
                authorities.add(new SimpleGrantedAuthority(permission.getName()));
            }
        }
        return new CustomUserDetails(user, authorities);
    }

    @Transactional
    public UserDetails loadUserByID(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid user id"));

        List<UserRole> userRoles = userRoleRepository.findByUser(user);

        Set<GrantedAuthority> authorities = new HashSet<>();

        for (UserRole userRole : userRoles) {
            Role role = userRole.getRole();

            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));

            List<RolePermission> rolePermissions = rolePermissionRepository.findByRole(role);

            for (RolePermission rp : rolePermissions) {
                Permission permission = rp.getPermission();
                authorities.add(new SimpleGrantedAuthority(permission.getName()));
            }
        }
        return new CustomUserDetails(user, authorities);
    }
}
