package com.proyecto.mantenimiento.repos;

import com.proyecto.mantenimiento.entities.Usuario;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface IUsuariosRepo extends IGenericRepo<Usuario, Long> {

    Optional<Usuario>findByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO usuarios_roles (id_usuario, id_rol) VALUES (?, 2)", nativeQuery = true)
    Integer guardarRol(Long idUsuario);

}
