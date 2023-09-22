package com.proyecto.mantenimiento.security.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.proyecto.mantenimiento.entities.Usuario;
import com.proyecto.mantenimiento.repos.IUsuariosRepo;
import com.proyecto.mantenimiento.security.jwt.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService service;

    @Autowired
    private IUsuariosRepo repo;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        try {
            if (!request.getHeader("Authorization").startsWith("Bearer ")){
                throw new HttpRequestMethodNotSupportedException("No tiene token");
            }

        }catch (NullPointerException e){
            throw new HttpRequestMethodNotSupportedException("No tiene el header para la autenticacion del token");
        }

        String token = request.getHeader("Authorization").substring(7);

        DecodedJWT jwt = service.validarJwt(token);
        Usuario usuario = repo.findByEmail(jwt.getClaim("email").asString())
                .orElseThrow(() -> new UsernameNotFoundException("no existe usuario"));

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

        var authentication = new UsernamePasswordAuthenticationToken(
                usuario.getEmail(),
                usuario.getContrasenia(),
                usuario.getRoles().stream()
                        .map(rol -> new SimpleGrantedAuthority(rol.getNombre().toString())).collect(Collectors.toList())
        );

        securityContext.setAuthentication(authentication);

        SecurityContextHolder.setContext(securityContext);

        doFilter(request, response, filterChain);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().equals("/auth/acceder") ||
                request.getServletPath().equals("/auth/registrar") ||
                request.getServletPath().equals("/auth/validar");
    }
}
