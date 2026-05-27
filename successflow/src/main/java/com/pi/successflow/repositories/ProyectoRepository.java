package com.pi.successflow.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pi.successflow.entity.Proyecto;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Integer> {

    List<Proyecto> findByEstado(String estado);
}