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

public interface BaseEmployeRepository<T extends Employe> extends PagingAndSortingRepository<T, Long> {
    T findByMatricule(String matricule);

    Page<T> findByNomIgnoreCase(String nom, Pageable pageable);

    List<T> findByNomAndPrenom(String nom, String prenom);
    List<T> findByNomIgnoreCase(String nom);
    List<T> findByDateEmbaucheBefore(LocalDate date);
    List<T> findByDateEmbaucheAfter(LocalDate date);
    List<T> findBySalaireGreaterThanOrderBySalaireDesc(Double salaire);

    @Query("select e from Employe e where lower(e.prenom) = lower(:nomOuPrenom) or lower(e.nom) = lower(:nomOuPrenom)")
    List<T> findByNomOrPrenomAllIgnoreCase(@Param("nomOuPrenom") String nomOuPrenom);

    @Query(value = "select e.* from Employe e where salaire > (select avg(e2.salaire) from Employe e2)", nativeQuery = true)
    List<T> findEmployePlusRiches();

    @Query(value = "select count(e.id) from Employe e where salaire > (select avg(e2.salaire) from Employe e2)", nativeQuery = true)
    Long countEmployePlusRiches();

}
