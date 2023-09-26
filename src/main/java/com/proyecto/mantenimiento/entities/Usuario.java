package com.proyecto.mantenimiento.entities;

import com.proyecto.mantenimiento.entities.projections.Projections;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@SqlResultSetMapping(
        name = "usuariosRoles",
        classes = {
                @ConstructorResult(
                        targetClass = Projections.UsuariosRoles.class,
                        columns = {
                                @ColumnResult(name = "id_usuario", type = Long.class),
                                @ColumnResult(name = "id_rol", type = Long.class)
                        }
                )
        }
)
@NamedNativeQuery(
        name = "Usuario.obtenerRoles",
        query = "SELECT * FROM usuarios_roles WHERE id_usuario = :id",
        resultSetMapping = "usuariosRoles"
)
public class Usuario {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_usuario")
    private Long idUsuario;

    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    private String contrasenia;

    @Column(name = "created_at")
    private LocalDate createdAt = LocalDate.now();


    @Column(name = "updated_at")
    private LocalDate updatedAt = LocalDate.now();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuarios_roles",
            joinColumns = @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_rol", referencedColumnName = "id_rol"))
    private Set<Rol> roles = new HashSet<>();

    public Usuario() {
    }

    public Usuario(String email, String contrasenia) {
        this.email = email;
        this.contrasenia = contrasenia;
    }

    public Usuario(Long idUsuario, String email, String contrasenia, LocalDate createdAt, LocalDate updatedAt, Set<Rol> roles) {
        this.idUsuario = idUsuario;
        this.email = email;
        this.contrasenia = contrasenia;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.roles = roles;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<Rol> getRoles() {
        return roles;
    }

    public void setRoles(Set<Rol> roles) {
        this.roles = roles;
    }
}
