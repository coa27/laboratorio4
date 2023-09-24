package com.proyecto.mantenimiento.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.proyecto.mantenimiento.exceptions.customs.TokenNoValidoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenService {

    @Value("${security.jwt.secret-key}")
    private String secret;

    Logger logger = LoggerFactory.getLogger(TokenService.class);


    public String generarToken(String email){
        try {
            String token = JWT.create()
                    .withIssuer("coasth_")
                    .withClaim("email", email)
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(Instant.now().plusSeconds(60*60*2))
                    .sign(Algorithm.HMAC512(secret));

            return token;
        }catch (JWTCreationException e){
            throw new JWTCreationException(e.getMessage(), e.getCause());
        }
    }

    /***
     *
     * Metodo para validar un token. Se crea un verifier, para verificar si el token es valido o no, con su metodo verify,
     * el propio metodo verify tambien verifica si el token esta expirado o no (ya que a la instancia del jwtVerifier
     * no le hemos puesto que acepte tokens expirados).
     *
     * @param token
     * @return
     */
    public DecodedJWT validarJwt(String token) {
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC512(secret))
                    .withIssuer("coasth_")
                    .withClaimPresence("email")
                    .build();

            DecodedJWT decodedJWT = jwtVerifier.verify(token);

            return decodedJWT;
        }catch (TokenExpiredException e){
            throw new TokenExpiredException(e.getMessage(), Instant.now());
        }catch (SignatureVerificationException e){
            throw new TokenNoValidoException(e.getMessage());
        }catch (JWTDecodeException e){
            throw new TokenNoValidoException(e.getMessage());
        }
    }
}
