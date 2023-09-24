package com.proyecto.mantenimiento.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.proyecto.mantenimiento.entities.Usuario;
import com.proyecto.mantenimiento.exceptions.customs.CredencialesErroneas;
import com.proyecto.mantenimiento.exceptions.customs.UsuarioEnUsoException;
import com.proyecto.mantenimiento.payload.request.LoginReqRecord;
import com.proyecto.mantenimiento.payload.request.ValidarTokenReqRecord;
import com.proyecto.mantenimiento.payload.response.LoginResRecord;
import com.proyecto.mantenimiento.repos.IUsuariosRepo;
import com.proyecto.mantenimiento.security.jwt.TokenService;
import com.proyecto.mantenimiento.security.manager.ManagerDeAutenticacion;
import com.proyecto.mantenimiento.services.impl.UsuariosServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/***
 * Mock code, codigo simulado
 * Principalmente para que las pruebas unitarias ejecutas, se hagan de forma eficiente, ya que se buscaria ejecutar
 * muchos tests en segundos, por lo que codigo que hagan llamados a base de datos, a otros sevicios o ejecuciones
 * pesadas, deben ser controladas para evitar precisamente un gran tiempo de ejecucion. Las pruebas anteriores deben
 * ser ejecutadas de forma unicas.
 *
 * Bcrpt es un algoritmo no determinista, como utiliza la funsion hash, siempre tendra valores aleatorios, algo no deseado
 * para un enfoque de pruebas unitarias
 */

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private IUsuariosRepo repo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @Mock
    private ManagerDeAutenticacion managerDeAutenticacion;

    @InjectMocks
    private UsuariosServiceImpl service;


    /***
     * Variables y constantes que necesitamos para los tokens
     */
    private static final String LLAVE = "LjWYIzO5xKCpRrWdujjxMPDT8jTm0ccO9AM7Wo6q";
    private static final String LLAVE_NUEVA = "laboratorio4";
    private static final String ISSUER = "coasth_";
    private static final String CLAIM = "email";
    private static final String DATA = "hola@hotmail.com";

    private static Algorithm algoritmo;
    private static Algorithm algoritmoConDiferenteLlave;
    private static JWTVerifier verifier;
    private static String token;
    private static String tokenNuevo;
    private static String tokenInvalido;


    @BeforeAll
    public static void setUp(){
        algoritmo = Algorithm.HMAC512(LLAVE);
        algoritmoConDiferenteLlave = Algorithm.HMAC512(LLAVE_NUEVA);

        token = JWT.create()
                .withIssuer(ISSUER)
                .withClaim(CLAIM, DATA)
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plusSeconds(60*60*2))
                .sign(Algorithm.HMAC512(LLAVE));

        tokenNuevo = JWT.create()
                .withIssuer(ISSUER)
                .withClaim(CLAIM, DATA)
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plusSeconds(60*5))
                .sign(Algorithm.HMAC512(LLAVE));

        tokenInvalido = JWT.create()
                .withIssuer(ISSUER)
                .withClaim(CLAIM, DATA)
                .withIssuedAt(Instant.now().minusSeconds(60*60*2))
                .withExpiresAt(Instant.now().minusSeconds(60*5))
                .sign(Algorithm.HMAC512(LLAVE));

        verifier = JWT.require(algoritmo)
                .withIssuer(ISSUER)
                .build();
    }

    /***
     *
     * Las pruebas unitarias deben
     */
    @Test
    public void dadoUsuarioValidoEnRegistro_returnExitoso(){
        Usuario usuarioMocked = new Usuario("hola@hotmail.com", "1234");
        LoginReqRecord usuarioNuevo =  new LoginReqRecord("hola@hotmail.com", "1234");

        when(repo.findByEmail(anyString()))
                .thenReturn(Optional.empty());
        when(repo.save(Mockito.any(Usuario.class)))
                .thenReturn(usuarioMocked);
        when(passwordEncoder.encode(anyString()))
                .thenReturn("1234");
        when(repo.guardarRol(any()))
                .thenReturn(1);
        when(tokenService.generarToken(anyString()))
                .thenReturn("token random");

        LoginResRecord usuarioSaved = service.registro(usuarioNuevo);

        //Se verifica que el usuario no sea nulo
        Assertions.assertThat(usuarioSaved).isNotNull();
    }

    @Test
    public void dadoUsuarioNoValidoEnRegistro_returnException(){
        Usuario usuarioMocked = new Usuario("hola@hotmail.com", "1234");
        LoginReqRecord usuarioNuevo =  new LoginReqRecord("hola@hotmail.com", "1234");

        when(repo.findByEmail("hola@hotmail.com"))
                .thenReturn(Optional.of(usuarioMocked));

        //se verifica que se retorne la exception creada para usuarios en uso
        assertThrows(UsuarioEnUsoException.class, () -> service.registro(usuarioNuevo));
    }

    @Test
    public void dadoUsuarioValidoEnAcceso_returnExitoso(){
        LoginReqRecord usuarioMocked = new LoginReqRecord("hola@hotmail.com", "1234");

        when(managerDeAutenticacion.authenticate(any()))
                .thenReturn(
                        new UsernamePasswordAuthenticationToken(
                                usuarioMocked.email(),
                                usuarioMocked.password(),
                                Set.of(new SimpleGrantedAuthority("user"))
                        )
                );
        when(tokenService.generarToken(anyString()))
                .thenReturn("token generado");

        LoginResRecord usuario = service.acceder(usuarioMocked);

        Assertions.assertThat(usuario).isNotNull();
    }

    @Test
    public void dadoUsuarioNoValidoEnAcceso_returnException(){
        LoginReqRecord usuarioMocked = new LoginReqRecord("hola@hotmail.com", "1234");

        when(managerDeAutenticacion.authenticate(any()))
                .thenThrow(
                        new CredencialesErroneas("credenciales no validas")
                );

        assertThrows(CredencialesErroneas.class, () -> service.acceder(usuarioMocked));
    }

    @Test
    public void dadoTokenValido_returnExitoso(){
        var tokenValidar = new ValidarTokenReqRecord("token");
        DecodedJWT decodedJWT = verifier.verify(token);

        when(tokenService.validarJwt(any()))
                .thenReturn(decodedJWT);

        LoginResRecord loginResRecord = service.validarToken(tokenValidar);

        Assertions.assertThat(loginResRecord).isNotNull();
    }


    @Test
    public void dadoTokenValido_returnNuevoToken(){
        var tokenValidar = new ValidarTokenReqRecord("token");
        DecodedJWT decodedJWT = verifier.verify(tokenNuevo);

        var tokenGenerado = JWT.create()
                .withIssuer(ISSUER)
                .withClaim(CLAIM, DATA)
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plusSeconds(60*60*2))
                .sign(Algorithm.HMAC512(LLAVE));

        when(tokenService.validarJwt(anyString()))
                .thenReturn(decodedJWT);
        when(tokenService.generarToken(anyString()))
                .thenReturn(tokenGenerado);

        LoginResRecord loginResRecord = service.validarToken(tokenValidar);

        Assertions.assertThat(loginResRecord.token())
                .isNotEqualToIgnoringCase(tokenValidar.token());
    }

    @Test
    public void dadoTokenExpirado_returnException(){
        assertThrows(TokenExpiredException.class, () -> verifier.verify(tokenInvalido));
    }

    @Test
    public void dadoTokenInvalido_returnException(){
        assertThrows(JWTDecodeException.class, () -> verifier.verify("tokenInvalido"));
    }

}
