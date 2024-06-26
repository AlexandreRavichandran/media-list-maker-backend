package com.medialistmaker.list.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medialistmaker.list.connector.appuser.AppUserConnectorProxy;
import com.medialistmaker.list.dto.AppUserDTO;
import com.medialistmaker.list.dto.ErrorDTO;
import com.medialistmaker.list.utils.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.ArrayList;

import static java.util.Objects.isNull;

@Component
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final AppUserConnectorProxy userConnectorProxy;

    private final JwtTokenService service;

    public JwtAuthenticationFilter(AppUserConnectorProxy userConnectorProxy, JwtTokenService service) {
        this.userConnectorProxy = userConnectorProxy;
        this.service = service;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String authorizationHeader = request.getHeader("Authorization");
        String token;
        String username;

        if(isNull(authorizationHeader)) {
                this.manageAuthenticationFailure(response);
                return;
        }

        token = authorizationHeader.substring(7);

        username = this.service.getUsername(token);

        if(isNull(username)) {
            this.manageAuthenticationFailure(response);
            return;
        }

        AppUserDTO userDTO = this.userConnectorProxy.getById(Long.valueOf(username));

        UserDetails userDetails = this.getUserDetailsByUserDTO(userDTO);

        Boolean isTokenValid = this.service.isTokenValid(token, userDetails);

        if(Boolean.FALSE.equals(isTokenValid)) {
            this.manageAuthenticationFailure(response);
            return;
        }

        this.manageAuthenticationSuccessful(userDetails, request);

        filterChain.doFilter(request, response);

    }
    private void manageAuthenticationSuccessful(UserDetails userDetails, HttpServletRequest request) {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

    }

    private void manageAuthenticationFailure(HttpServletResponse response) throws IOException {

        ErrorDTO errorDTO = new ErrorDTO("Unauthorized", new ArrayList<>());

        response.getWriter().print(new ObjectMapper().writeValueAsString(errorDTO));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private UserDetails getUserDetailsByUserDTO(AppUserDTO userDTO) {
        return new User(userDTO.getId().toString(), userDTO.getPassword(), new ArrayList<>());
    }

}
