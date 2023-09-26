package com.proyecto.mantenimiento.repos;

import com.proyecto.mantenimiento.entities.Usuario;
import com.proyecto.mantenimiento.entities.projections.Projections;
import com.proyecto.mantenimiento.exceptions.customs.UsuarioEnUsoException;
import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;


@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UsuarioRepoTest {

    @Autowired
    private IUsuariosRepo repo;

    @Test
    public void dadoUnUsuarioValidoEnBaseDeDatos_returnExitoso(){
        //arrange
        Usuario usuario = new Usuario("hola@hotmail.com", "1234");

        //act
        Usuario usuarioSaved = repo.save(usuario);


        //assertions
        //se verifica que el usuario guardado exista
        Assertions.assertThat(usuarioSaved).isNotNull();
        //se verifica que el usuario guardado tenga un identificador
        Assertions.assertThat(usuarioSaved.getIdUsuario()).isGreaterThan(0);
    }

    @Test
    public void dadoUnUsuarioExistenteEnBaseDeDatos_returnException(){
        //arrange
        Usuario usuario = new Usuario("hola@hotmail.com", "1234");
        Usuario usuario2 = new Usuario("hola@hotmail.com", "1234");

        //act
        Usuario usuarioSaved = repo.save(usuario);


        //assertions
        //se verifica que la exception devuelta, sea la deseada
        assertThrows(DataIntegrityViolationException.class, () -> repo.save(usuario2));
    }


//    @Test
//    public void dadoUnUsuarioNoValidoPorContrasenaEnBaseDeDatos_returnExitoso(){
//        //creamos un usuario cuya contrasena sea menos de los 4 caracteres minimos
//        Usuario usuario = new Usuario("hola@hotmail.com", "12");
//
//        //nos aseguramos que el error sea el deseado
//        assertThrows(ConstraintViolationException.class, () -> repo.save(usuario));
//    }

    @Test
    public void encontrarUsuarioEnBaseDeDatos_returnExitoso(){
        Usuario usuario = new Usuario("hola@hotmail.com", "1234");

        Usuario usuarioSaved = repo.save(usuario);

        //se verifica que ya existe un usuario con el mismo correo
        Assertions.assertThat(repo.findByEmail(usuario.getEmail())).isPresent();
        //se verifica que el usuario retornado de base de datos, sea el mismo pasado por parametro
        Assertions.assertThat(repo.findByEmail(usuario.getEmail()).equals(usuarioSaved.getEmail()));
    }


    @Test
    public void guardarRolParaUsuario_returnExitoso(){
        Usuario usuario = new Usuario("hola@hotmail.com", "1234");
        Usuario usuarioSaved = repo.save(usuario);

        repo.guardarRol(usuarioSaved.getIdUsuario());

        //interfaz para la obtencion del objeto desde la base de datos
        Projections.UsuariosRoles roles = repo.obtenerRoles(usuarioSaved.getIdUsuario()).get();

        //se verifica que se ha agregado el rol
        Assertions.assertThat(roles).isNotNull();
        //se verifica que el rol que se le ha agregado es el 2, por ser el id de user... el id de admin es 1
        Assertions.assertThat(roles.idRol()).isEqualTo(2);
    }

}
