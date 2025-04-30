package edu.tcu.cs.frogcrewbackend.config;

import edu.tcu.cs.frogcrewbackend.CrewMember.CrewMember;
import edu.tcu.cs.frogcrewbackend.CrewMember.CrewMemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(CrewMemberRepository crewMemberRepository) {
        return username -> {
            CrewMember crewMember = crewMemberRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            return org.springframework.security.core.userdetails.User.builder()
                .username(crewMember.getEmail())
                .password(crewMember.getPassword())
                .authorities(crewMember.getRole())
                .build();
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            )
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/crew-members/register").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/v1/crew-members/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/crew-members/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/games/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable())
            .httpBasic(basic -> {});
        
        return http.build();
    }
}