package com.example.demo.controller;

import com.example.demo.model.Tutorial;
import com.example.demo.repository.TutorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@CrossOrigin(origins = "https://applicationlivredecaisseclientsideone.onrender.com")
@RestController
@RequestMapping("/api")
public class TutorialController {

    @Autowired
    TutorialRepository tutorialRepository;

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }

        return Sort.Direction.ASC;
    }

    @GetMapping("/sortedtutorials")
    public ResponseEntity<List<Tutorial>> getAllTutorials(@RequestParam(defaultValue = "id,desc") String[] sort) {

        try {
            List<Order> orders = new ArrayList<Order>();

            if (sort[0].contains(",")) {
                // will sort more than 2 fields
                // sortOrder="field, direction"
                for (String sortOrder : sort) {
                    String[] _sort = sortOrder.split(",");
                    orders.add(new Order(getSortDirection(_sort[1]), _sort[0]));
                }
            } else {
                // sort=[field, direction]
                orders.add(new Order(getSortDirection(sort[1]), sort[0]));
            }

            List<Tutorial> tutorials = tutorialRepository.findAll(Sort.by(orders));

            if (tutorials.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(tutorials, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/tutorials")
    public ResponseEntity<Map<String, Object>> getAllTutorialsPage(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort) {

        try {
            List<Order> orders = new ArrayList<Order>();

            if (sort[0].contains(",")) {
                // will sort more than 2 fields
                // sortOrder="field, direction"
                for (String sortOrder : sort) {
                    String[] _sort = sortOrder.split(",");
                    orders.add(new Order(getSortDirection(_sort[1]), _sort[0]));
                }
            } else {
                // sort=[field, direction]
                orders.add(new Order(getSortDirection(sort[1]), sort[0]));
            }

            List<Tutorial> tutorials = new ArrayList<Tutorial>();
            Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

            Page<Tutorial> pageTuts;
            if (title == null)
                pageTuts = tutorialRepository.findAll(pagingSort);
            else
                pageTuts = tutorialRepository.findByTitleContaining(title, pagingSort);

            tutorials = pageTuts.getContent();

            Map<String, Object> response = new HashMap<>();
            response.put("tutorials", tutorials);
            response.put("currentPage", pageTuts.getNumber());
            response.put("totalItems", pageTuts.getTotalElements());
            response.put("totalPages", pageTuts.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/tutorials/published")
    public ResponseEntity<Map<String, Object>> findByPublished(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {

        try {
            List<Tutorial> tutorials = new ArrayList<Tutorial>();
            Pageable paging = PageRequest.of(page, size);

            Page<Tutorial> pageTuts = tutorialRepository.findByPublished(true, paging);
            tutorials = pageTuts.getContent();

            Map<String, Object> response = new HashMap<>();
            response.put("tutorials", tutorials);
            response.put("currentPage", pageTuts.getNumber());
            response.put("totalItems", pageTuts.getTotalElements());
            response.put("totalPages", pageTuts.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/tutorials/{id}")
    public ResponseEntity<Tutorial> getTutorialById(@PathVariable("id") long id) {
        Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

        if (tutorialData.isPresent()) {
            return new ResponseEntity<>(tutorialData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Double getTotalRecipeToday() {
        String sql = "SELECT COALESCE(SUM(recipe_today + balance_previous_month), 0) AS total_recipes FROM tutorials";
        return jdbcTemplate.queryForObject(sql, Double.class);
    }

    @PostMapping("/tutorials")
    public ResponseEntity<Tutorial> createTutorial(@RequestBody Tutorial tutorial) {

        LocalDate currentLocalDate = LocalDate.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDateTime = currentLocalDate.format(dateTimeFormatter);

        try {

            double totalRecipeToday = tutorial.getRecipeToday() +  tutorial.getBalancePreviousMonth();
            double totalOperationTreasury = tutorial.getOperationTreasuryAnterior() + tutorial.getOperationTreasuryToday();
            double totalOperationRegulation = tutorial.getOperationPreviousRegulation() + tutorial.getOperationRegulationToday();
            double totalExpenses = totalOperationTreasury + totalOperationRegulation;
            double finalBalanceToday = totalRecipeToday - totalExpenses;

             double finalPostCurrentAccount = (tutorial.getPostCurrentAccount() + tutorial.getCreditExpected())
                     - tutorial.getRateExpected();
            double totalCash = finalBalanceToday - (tutorial.getStatesRepartition() + tutorial.getOtherValues() +
                    finalPostCurrentAccount);
            double moneyOnCashier = totalCash - tutorial.getMoneySpecies();

            // Set total recipe today in the tutorial object
            tutorial.setTotalRecipeToday(totalRecipeToday);
            Tutorial _tutorial = tutorialRepository.save(new Tutorial(
                    "Operation Date is: " + formattedDateTime,
                    tutorial.getRecipeToday(),
                    tutorial.getBalancePreviousMonth(),
                    totalRecipeToday,

                    tutorial.getOperationTreasuryAnterior(),
                    tutorial.getOperationTreasuryToday(),
                    totalOperationTreasury,

                    tutorial.getOperationPreviousRegulation(),
                    tutorial.getOperationRegulationToday(),
                    totalOperationRegulation,

                    totalExpenses,

                    finalBalanceToday,

                    tutorial.getPostCurrentAccount(),
                    tutorial.getCreditExpected(),
                    tutorial.getRateExpected(),

                    finalPostCurrentAccount,

                    tutorial.getOtherValues(),
                    tutorial.getStatesRepartition(),

                    totalCash,

                    tutorial.getMoneySpecies(),

                    moneyOnCashier,

                    tutorial.getOrganismId(),
                    tutorial.getDescription(),
                    tutorial.isPublished()));

            return new ResponseEntity<>(_tutorial, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/tutorials/{id}")
    public ResponseEntity<Tutorial> updateTutorial(@PathVariable("id") long id, @RequestBody Tutorial tutorial) {
        Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

        double totalRecipeToday = tutorial.getRecipeToday() +  tutorial.getBalancePreviousMonth();
        double totalOperationTreasury = tutorial.getOperationTreasuryAnterior() + tutorial.getOperationTreasuryToday();
        double totalOperationRegulation = tutorial.getOperationPreviousRegulation() + tutorial.getOperationRegulationToday();
        double totalExpenses = totalOperationTreasury + totalOperationRegulation;
        double finalBalanceToday = totalRecipeToday - totalExpenses;

        double finalPostCurrentAccount = (tutorial.getPostCurrentAccount() + tutorial.getCreditExpected())
                - tutorial.getRateExpected();
        double totalCash = finalBalanceToday - (tutorial.getStatesRepartition() + tutorial.getOtherValues() +
                finalPostCurrentAccount);
        double moneyOnCashier = totalCash - tutorial.getMoneySpecies();

        if (tutorialData.isPresent()) {
            Tutorial _tutorial = tutorialData.get();
            _tutorial.setTitle(tutorial.getTitle());
            _tutorial.setRecipeToday(tutorial.getRecipeToday());
            _tutorial.setBalancePreviousMonth(tutorial.getBalancePreviousMonth());
            _tutorial.setTotalRecipeToday(totalRecipeToday);

            _tutorial.setOperationTreasuryAnterior(tutorial.getOperationTreasuryAnterior());
            _tutorial.setOperationTreasuryToday(tutorial.getOperationTreasuryToday());
            _tutorial.setOperationTreasuryToday(totalOperationTreasury);

            _tutorial.setOperationPreviousRegulation(tutorial.getOperationPreviousRegulation());
            _tutorial.setOperationTreasuryToday(tutorial.getOperationRegulationToday());
            _tutorial.setTotalOperationRegulation(totalOperationRegulation);

            _tutorial.setTotalExpenses(totalExpenses);

            _tutorial.setFinalBalanceToday(finalBalanceToday);

            _tutorial.setPostCurrentAccount(tutorial.getPostCurrentAccount());
            _tutorial.setCreditExpected(tutorial.getCreditExpected());
            _tutorial.setRateExpected(tutorial.getRateExpected());
            _tutorial.setFinalPostCurrentAccount(finalPostCurrentAccount);

            _tutorial.setOtherValues(tutorial.getOtherValues());
            _tutorial.setStatesRepartition(tutorial.getStatesRepartition());
            _tutorial.setTotalCash(totalCash);

            _tutorial.setMoneySpecies(tutorial.getMoneySpecies());
            _tutorial.setMoneyOnCashier(moneyOnCashier);

            _tutorial.setOrganismId(tutorial.getOrganismId());
            _tutorial.setDescription(tutorial.getDescription());
            _tutorial.setPublished(tutorial.isPublished());
            return new ResponseEntity<>(tutorialRepository.save(_tutorial), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/tutorials/{id}")
    public ResponseEntity<HttpStatus> deleteTutorial(@PathVariable("id") long id) {
        try {
            tutorialRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/tutorials")
    public ResponseEntity<HttpStatus> deleteAllTutorials() {
        try {
            tutorialRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/tutorials/totalRecipe")
    public Double totalRecipe(){
        return tutorialRepository.totalRecipe();
    };

    @GetMapping("/tutorials/totalRecipeWithOrWithoutDate")
    public Double getTotalRecipe(@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date givenDate) {
        if (givenDate == null) {
            givenDate = new Date(); // Set default value to current date
        }
        return tutorialRepository.totalRecipeWithDate(givenDate);
    }

    @GetMapping("/tutorials/totalTreasuryOperations")
    public Double totalTreasuryOperations(){
        return tutorialRepository.totalTreasuryOperations();
    };

    @GetMapping("/tutorials/totalRegulationOperation")
    public Double totalRegulationOperation(){
        return tutorialRepository.totalRegulationOperation();
    };

    @GetMapping("/tutorials/totalExpenses")
    public Double totalExpenses(){
        return totalTreasuryOperations() + totalRegulationOperation();
    };

    @GetMapping("/tutorials/totalCurrentBalanceToday")
    public Double totalCurrentBalanceToday(){
        Double result = totalRecipe() - totalExpenses();
        return Math.round(result * 1000) / 1000.0;
    }

    @GetMapping("/tutorials/finalPostalCurrentAccount")
    public Double getFinalPostalCurrentAccount(){
        return tutorialRepository.finalPostalCurrentAccount();
    }

    @GetMapping("/tutorials/totalCash")
    public Double getTotalCash(){
        Double result = totalCurrentBalanceToday() -
                (tutorialRepository.getStatesRepartition() + tutorialRepository.getOtherValues() +
                        getFinalPostalCurrentAccount());

        return Math.round(result * 1000) / 1000.0;
    }

    @GetMapping("/tutorials/currencyCashOnCashier")
    public Double getCurrencyCashOnCashier(){
        Double result = getTotalCash() - tutorialRepository.getMoneySpices();

        return Math.round(result * 1000) / 1000.0;
    }

    @GetMapping("/tutorials/finalBalanceLastMonth")
    public Double getFinalBalanceLastMonth(){
        return tutorialRepository.getFinalBalanceLastMonth();
    };

    @GetMapping("/tutorials/treasuryOperationsLastRow")
    public double totalTreasuryOperationsLastRow(){
        return tutorialRepository.totalTreasuryOperationsLastRow();
    };

    @GetMapping("/tutorials/regulationOperationsLastRow")
    public double totalRegulationOperationsLastRow(){
        return tutorialRepository.totalRegulationOperationsLastRow();
    };

}
