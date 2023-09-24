package com.proyecto.mantenimiento.security.service;

import com.proyecto.mantenimiento.entities.Usuario;
import com.proyecto.mantenimiento.exceptions.customs.CredencialesErroneas;
import com.proyecto.mantenimiento.repos.IUsuariosRepo;
import com.proyecto.mantenimiento.security.models.UsuarioUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private IUsuariosRepo repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = repo.findByEmail(username).orElseThrow(() -> new CredencialesErroneas("Credenciales erroneas"));

        return new UsuarioUserDetails(usuario);
    }

}
