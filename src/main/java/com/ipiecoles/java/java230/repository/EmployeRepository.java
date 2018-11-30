package com.ipiecoles.java.java230.repository;

//import org.springframework.data.*;
import com.ipiecoles.java.java230.model.*;

import org.joda.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeRepository extends PagingAndSortingRepository<Employe, Long> {
    Employe findByMatricule(String matricule);

    Page<Employe> findByNomIgnoreCase(String nom, Pageable pageable);

    List<Employe> findByNomAndPrenom(String nom, String prenom);
    List<Employe> findByNomIgnoreCase(String nom);
    List<Employe> findByDateEmbaucheBefore(LocalDate date);
    List<Employe> findByDateEmbaucheAfter(LocalDate date);
    List<Employe> findBySalaireGreaterThanOrderBySalaireDesc(Double salaire);

    @Query("select e from Employe e where lower(e.prenom) = lower(:nomOuPrenom) or lower(e.nom) = lower(:nomOuPrenom)")
    List<Employe> findByNomOrPrenomAllIgnoreCase(@Param("nomOuPrenom") String nomOuPrenom);

    @Query(value = "select e from Employe e where salaire > (select avg(e2.salaire) from Employe e2)", nativeQuery = true)
    List<Employe> findEmployePlusRiches();

}
