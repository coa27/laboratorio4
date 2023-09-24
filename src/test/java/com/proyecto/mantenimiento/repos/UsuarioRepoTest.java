package com.proyecto.mantenimiento.repos;

import com.proyecto.mantenimiento.entities.Usuario;
import com.proyecto.mantenimiento.entities.projections.Projections;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UsuarioRepoTest {

    @Autowired
    private IUsuariosRepo repo;

    @Test
    public void testGuardarUsuarios(){
        //arrange
        //contrasenia  12345
        Usuario usuario = new Usuario("hola@hotmail.com", "1234");

        //act
        Usuario usuarioSaved = repo.save(usuario);


        //assertions
        //se verifica que el usuario guardado no sea nulo
        Assertions.assertThat(usuarioSaved).isNotNull();
        //se verifica que el usuario guardado tenga un identificador
        Assertions.assertThat(usuarioSaved.getIdUsuario()).isGreaterThan(0);
    }

    @Test
    public void testEncontrarUsuarioPorEmail(){
        //contrasenia  12345
        Usuario usuario = new Usuario("hola@hotmail.com", "1234");

        Usuario usuarioSaved = repo.save(usuario);

        //se verifica que ya existe un usuario con el mismo correo
        Assertions.assertThat(repo.findByEmail(usuario.getEmail())).isPresent();
        //se verifica que no existe usuario con un correo nuevo
        Assertions.assertThat(repo.findByEmail(usuario.getEmail()).equals("hola1@hotmail.com")).isFalse();
    }

    @Test
    public void testGuardarRolUsuario(){
        Usuario usuario = new Usuario("hola@hotmail.com", "1234");
        Usuario usuarioSaved = repo.save(usuario);

        repo.guardarRol(usuarioSaved.getIdUsuario());

        Projections.UsuariosRoles roles = repo.obtenerRoles(usuarioSaved.getIdUsuario()).get();

        //se verifica que se ha agregado el rol
        Assertions.assertThat(roles).isNotNull();
        //se verifica que el rol que se le ha agregado es el 2, por ser el id de user... el id de admin es 1
        Assertions.assertThat(roles.idRol()).isEqualTo(2);


    }

}
