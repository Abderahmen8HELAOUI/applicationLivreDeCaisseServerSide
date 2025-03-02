package com.example.demo.controller;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Organism;
import com.example.demo.model.Tutorial;
import com.example.demo.repository.OrganismRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@CrossOrigin(origins = "https://dailyaccountingapp-963922cd8770.herokuapp.com")
//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class OrganismController {

    @Autowired
    OrganismRepository organismRepository;

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }

        return Sort.Direction.ASC;
    }

    @GetMapping("/organisms/withPage")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllTutorialsPage(
            @RequestParam(required = false) String code,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort) {

        try {
            List<Sort.Order> orders = new ArrayList<Sort.Order>();

            if (sort[0].contains(",")) {
                // will sort more than 2 fields
                // sortOrder="field, direction"
                for (String sortOrder : sort) {
                    String[] _sort = sortOrder.split(",");
                    orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
                }
            } else {
                // sort=[field, direction]
                orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
            }

            List<Organism> organisms = new ArrayList<Organism>();
            Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

            Page<Organism> pageTuts;
            if (code == null)
                pageTuts = organismRepository.findAll(pagingSort);
            else
                pageTuts = organismRepository.findByCodeContaining(code, pagingSort);

            organisms = pageTuts.getContent();

            Map<String, Object> response = new HashMap<>();
            response.put("tutorials", organisms);
            response.put("currentPage", pageTuts.getNumber());
            response.put("totalItems", pageTuts.getTotalElements());
            response.put("totalPages", pageTuts.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/organisms")
    public ResponseEntity<List<Organism>> getAllOrganisms(@RequestParam(required = false) String code) {
        try {
            List<Organism> organismList = new ArrayList<Organism>();

            if (code == null)
                organismRepository.findAll().forEach(organismList::add);
            else
                organismRepository.findByCodeContaining(code).forEach(organismList::add);

            if (organismList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(organismList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/organisms/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Organism> createTutorial(@RequestBody Organism organism) {
        Organism _organism = organismRepository.save(new Organism(
                organism.getType(),
                organism.getName(),
                organism.getCode()));
        return new ResponseEntity<>(_organism, HttpStatus.CREATED);
    }

    @GetMapping("/organisms/code/{organismCode}/id")
    public ResponseEntity<UUID> getOrganismIdByCode(@PathVariable("organismCode") String organismCode) {
        Organism organism = organismRepository.findByCode(organismCode)
                .orElseThrow(() -> new ResourceNotFoundException("Organism not found with code = " + organismCode));
        return new ResponseEntity<>(organism.getId(), HttpStatus.OK);
    }
}
