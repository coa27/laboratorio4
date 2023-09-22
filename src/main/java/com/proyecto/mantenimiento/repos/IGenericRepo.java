package com.proyecto.mantenimiento.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface IGenericRepo<T, Id> extends JpaRepository<T, Id> {
}
