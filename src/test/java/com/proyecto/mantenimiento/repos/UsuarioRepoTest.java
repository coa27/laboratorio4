package com.proyecto.mantenimiento.repos;

import com.proyecto.mantenimiento.entities.Usuario;
import org.junit.jupiter.api.Assertions;
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
        //contrasenia  12345
        Usuario usuario = new Usuario("hola@hotmail.com", "$2a$10$cT39I/fNjupCT6DRfvQA8OjY8b9j7AvSwOBc0hCFTItdalGNMhS6i");

        Usuario usuarioSaved = repo.save(usuario);

        //se verifica que el usuario guardado no sea nulo
        Assertions.assertNotNull(usuarioSaved);
        //se verifica que el usuario guardado tenga un identificador
        Assertions.assertTrue(usuarioSaved.getIdUsuario() > 0);
    }

    @Test
    public void testExisteUsuario(){
        //contrasenia  12345
        Usuario usuario = new Usuario("hola@hotmail.com", "1234");

        Usuario usuarioSaved = repo.save(usuario);

        //se verifica que ya existe un usuario con el mismo correo
        Assertions.assertNotNull(repo.findByEmail(usuario.getEmail()));
        //se verifica que no existe usuario con un correo nuevo
        Assertions.assertFalse(repo.findByEmail(usuario.getEmail()).equals("hola1@hotmail.com") );
    }

}
