package com.banksphere.core.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    // private final UserRepository userRepository; // TODO: Uncomment when module available

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: Fetch user from repository and map to CustomUserDetails
        // User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        // return new CustomUserDetails(user.getId(), user.getUsername(), user.getPassword(), user.getEmail(), mapRoles(user.getRoles()), true, true);
        
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
