package com.proyecto.mantenimiento.services;

import java.util.List;

public interface ICRUD<T, Id>{

    T registrar(T t);
    T modificar(T t);
    List<T> listar();
    T listarPorId(Id id);
    void eliminar(Id id);

}
