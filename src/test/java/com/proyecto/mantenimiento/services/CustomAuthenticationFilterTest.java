package com.proyecto.mantenimiento.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.proyecto.mantenimiento.entities.Usuario;
import com.proyecto.mantenimiento.exceptions.customs.TokenNoValidoException;
import com.proyecto.mantenimiento.exceptions.customs.UsuarioNoEncontradoException;
import com.proyecto.mantenimiento.repos.IUsuariosRepo;
import com.proyecto.mantenimiento.security.filter.CustomAuthenticationFilter;
import com.proyecto.mantenimiento.security.jwt.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.AdditionalMatchers.not;

@ExtendWith(MockitoExtension.class)
public class CustomAuthenticationFilterTest {

    @Mock
    private TokenService service;

    @Mock
    private IUsuariosRepo repo;

    @InjectMocks
    private CustomAuthenticationFilter customAuthenticationFilter;

    private AuthenticationManager manager;
    private BasicAuthenticationFilter chain;

    private String token;

    private DecodedJWT decodedToken;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.clearContext();
        UsernamePasswordAuthenticationToken requestLogin = UsernamePasswordAuthenticationToken
                .unauthenticated("usuario", "12345");
        Authentication usuario = UsernamePasswordAuthenticationToken
                .authenticated("usuario", "12345", AuthorityUtils.createAuthorityList("ROLE_user"));

        manager = Mockito.mock(AuthenticationManager.class);

        chain = new BasicAuthenticationFilter(manager, new BasicAuthenticationEntryPoint());

        token = JWT.create()
                .withIssuer("coasth_")
                .withClaim("email", "hola1@hotmail.com")
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plusSeconds(60*60*2))
                .sign(Algorithm.HMAC512("LjWYIzO5xKCpRrWdujjxMPDT8jTm0ccO9AM7Wo6q"));

        decodedToken = JWT.decode(token);
    }


    @Test
    public void dadaLaRequestSinHeaderAuthorization_returnException() {
        //simulamos una request hacia el endpoint protegido por nuestro filtro
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/hola");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        //verificamos que se devuelve el error deseado
        assertThrows(TokenNoValidoException.class, () -> customAuthenticationFilter.doFilter(request, response, filterChain));
    }

    @Test
    public void dadaLaRequestConHeaderAuthorizationVacio_returnException() {
        //simulamos una request hacia el endpoint protegido por nuestro filtro con nuestro header de un token vacio
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/hola");
        request.addHeader("Authorization", "");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        //verificamos que se devuelve el error deseado
        assertThrows(TokenNoValidoException.class, () -> customAuthenticationFilter.doFilter(request, response, filterChain));
    }

    @Test
    public void dadaLaRequestValida_returnExitoso() throws ServletException, IOException {
        //simulamos una request hacia el endpoint protegido por nuestro filtro con nuestro token valido
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/hola");
        request.addHeader("Authorization", "Bearer token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        Mockito.when(service.validarJwt(anyString()))
                        .thenReturn(decodedToken);
        Mockito.when(repo.findByEmail(anyString()))
                        .thenReturn(Optional.of(new Usuario("hola@hotmail.com", "12345")));

        //nos aseguramos que el securityContextHolder este vacio para la creacio de la sesion de seguridad
        Assertions.assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        FilterChain filterChain = Mockito.mock(FilterChain.class);
        customAuthenticationFilter.doFilter(request, response, filterChain);

        //nos aseguramos que haya un objecto en nuestro securityContextHolder
        Assertions.assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        //nos aseguramos que la sesion pertenezca al usuario del token
        Assertions.assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString())
                .isEqualToIgnoringCase("hola@hotmail.com");
    }

    @Test
    public void dadaLaRequestValidaConEmailNoEncontrado_returnException() throws ServletException, IOException {
        //simulamos una request hacia el endpoint protegido por nuestro filtro con nuestro token sin usuario valido
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/hola");
        request.addHeader("Authorization", "Bearer token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        Mockito.when(service.validarJwt(anyString()))
                .thenReturn(decodedToken);//accion para validar el token
        Mockito.when(repo.findByEmail(anyString()))
                .thenReturn(Optional.empty());//accion que nos devuelve un usuario no valido

        FilterChain filterChain = Mockito.mock(FilterChain.class);
        assertThrows(UsuarioNoEncontradoException.class, () -> customAuthenticationFilter.doFilter(request, response, filterChain));
    }



}
