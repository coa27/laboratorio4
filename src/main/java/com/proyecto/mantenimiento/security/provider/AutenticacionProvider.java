package com.proyecto.mantenimiento.security.provider;

import com.proyecto.mantenimiento.exceptions.customs.CredencialesErroneas;
import com.proyecto.mantenimiento.security.models.UsuarioUserDetails;
import com.proyecto.mantenimiento.security.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AutenticacionProvider implements AuthenticationProvider {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    /***
     *
     * Se intenta validar al usuario por medio del correo y constrasenia proporcionada, de ser exitosa, se devuelve
     * una instancia del ModeloDeAutenticacion, donde podriamos agregarle mas detalles (y no solo el id del usuario),
     * por ahora solo se quisiera agregar la direccion IP del usuario.
     *
     * @param authentication
     * @return ModeloDeAutenticacion para obtener detalles (id) del usuario autenticado
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        UsuarioUserDetails userDetails = (UsuarioUserDetails) userDetailsService.loadUserByUsername(email);

        if (passwordEncoder.matches(password, userDetails.getPassword())){
            return new UsernamePasswordAuthenticationToken(
                    email,
                    password,
                    userDetails.getAuthorities());
        }

        throw new CredencialesErroneas("Credenciales erroneas");
    }

    /***
     *
     * Si se intenta validar con la clase UsernamePasswordAuthenticationToken, se realizara con este Provider.
     *
     * @param authentication
     * @return
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
