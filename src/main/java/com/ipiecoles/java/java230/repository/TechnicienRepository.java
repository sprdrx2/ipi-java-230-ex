package com.ipiecoles.java.java230.repository;

import com.ipiecoles.java.java230.model.Technicien;

import java.util.List;

public interface TechnicienRepository extends BaseEmployeRepository<Technicien> {
    List<Technicien> findByGradeg(Integer grade);
}
