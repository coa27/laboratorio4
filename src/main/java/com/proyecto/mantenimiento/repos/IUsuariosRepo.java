package com.proyecto.mantenimiento.repos;

import com.proyecto.mantenimiento.entities.Usuario;
import com.proyecto.mantenimiento.entities.projections.Projections;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface IUsuariosRepo extends IGenericRepo<Usuario, Long> {

    Optional<Usuario>findByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO usuarios_roles (id_usuario, id_rol) VALUES (?, 2)", nativeQuery = true)
    Integer guardarRol(Long idUsuario);

    @Query(nativeQuery = true)
    Optional<Projections.UsuariosRoles> obtenerRoles(@Param("id") Long id);
}
