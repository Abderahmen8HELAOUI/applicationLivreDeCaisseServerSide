package com.example.demo.repository;

import com.example.demo.model.Organism;
import com.example.demo.model.Tutorial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganismRepository extends JpaRepository<Organism, UUID> {
    Page<Organism> findByCodeContaining(String code, Pageable pageable);

    List<Organism> findByCodeContaining(String code);

    Optional<Organism> findByCode(String code);
}
