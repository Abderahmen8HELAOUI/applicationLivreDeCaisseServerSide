package com.example.demo.controller;

import com.example.demo.model.Organism;
import com.example.demo.model.Tutorial;
import com.example.demo.repository.OrganismRepository;
import com.example.demo.repository.TutorialRepository;
import com.example.demo.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

//@CrossOrigin(origins = "https://applicationlivredecaisseclient-c45a7f748dd6.herokuapp.com")
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TutorialController {

    private final TutorialRepository tutorialRepository;

    private final OrganismRepository organismRepository;

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

    @GetMapping("/organisms/{organismCode}/tutorials")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllTutorialsPage(
            @PathVariable String organismCode,
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
                //pageTuts = tutorialRepository.findAll(pagingSort);
                pageTuts = tutorialRepository.findByOrganismCode(organismCode, pagingSort);
            else
                //pageTuts = tutorialRepository.findByTitleContaining(title, pagingSort);
                pageTuts = tutorialRepository.findByTitleContainingAndOrganismCode(title, organismCode, pagingSort);

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

//    @GetMapping("/organisms/{organismId}/tutorials")
//    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
//    public ResponseEntity<List<Tutorial>> getAllCommentsByTutorialId(@PathVariable(value = "organismId") UUID organismId) {
//        if (!organismRepository.existsById(organismId)) {
//            throw new ResourceNotFoundException("Not found Tutorial with id = " + organismId);
//        }
//
//        List<Tutorial> tutorials = tutorialRepository.findByOrganismId(organismId);
//        return new ResponseEntity<>(tutorials, HttpStatus.OK);
//    }

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
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Tutorial> getTutorialById(@PathVariable("id") long id) {
        Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

        if (tutorialData.isPresent()) {
            return new ResponseEntity<>(tutorialData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/organisms/{organismCode}/tutorials")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<?> createTutorialByOrganismCode(
            @PathVariable(value = "organismCode") String organismCode,
            @RequestBody Tutorial tutorialRequest) {

        LocalDate currentLocalDate = LocalDate.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDateTime = currentLocalDate.format(dateTimeFormatter);

        try {
            double totalRecipeToday = Math.round((tutorialRequest.getRecipeToday() + tutorialRequest.getBalancePreviousMonth())* 1000) / 1000.0;

            double totalOperationTreasury = Math.round((tutorialRequest.getOperationTreasuryAnterior() + tutorialRequest.getOperationTreasuryToday())* 1000) / 1000.0;

            double totalOperationRegulation = Math.round((tutorialRequest.getOperationPreviousRegulation() + tutorialRequest.getOperationRegulationToday())* 1000) / 1000.0;

            double totalExpenses = Math.round((totalOperationTreasury + totalOperationRegulation)* 1000) / 1000.0;

            double finalBalanceToday = Math.round((totalRecipeToday - totalExpenses)* 1000) / 1000.0;

            double finalPostCurrentAccount = Math.round(((tutorialRequest.getPostCurrentAccount() + tutorialRequest.getCreditExpected())
                    - tutorialRequest.getRateExpected())* 1000) / 1000.0;

            double totalCash = Math.round((finalBalanceToday - (tutorialRequest.getStatesRepartition() + tutorialRequest.getOtherValues() +
                    finalPostCurrentAccount))* 1000) / 1000.0;

            double calculatedMoneyOnCashier = Math.round((totalCash - tutorialRequest.getMoneySpecies())*1000)/1000.0;

            Optional<Organism> organismData = organismRepository.findByCode(organismCode);

            if (organismData.isPresent()) {
                Organism organism = organismData.get();
                tutorialRequest.setOrganism(organism);
                tutorialRequest.setTitle("Operation Date is: " + formattedDateTime);
                tutorialRequest.setTotalRecipeToday(totalRecipeToday);
                tutorialRequest.setTotalOperationTreasury(totalOperationTreasury);
                tutorialRequest.setTotalOperationRegulation(totalOperationRegulation);
                tutorialRequest.setTotalExpenses(totalExpenses);
                tutorialRequest.setFinalBalanceToday(finalBalanceToday);
                tutorialRequest.setFinalPostCurrentAccount(finalPostCurrentAccount);
                tutorialRequest.setTotalCash(totalCash);
                tutorialRequest.setMoneyOnCashier(calculatedMoneyOnCashier);

                /*
                * Double result = totalRecipe() - totalExpenses();
        return Math.round(result * 1000) / 1000.0;
                * */

                // Comparaison entre la valeur fournie et la valeur calculée
                double providedMoneyOnCashier = tutorialRequest.getProvidedMoneyOnCashier();
                String message;
                double difference = Math.abs(Math.round((calculatedMoneyOnCashier - providedMoneyOnCashier)*1000)/1000.0);

                if (calculatedMoneyOnCashier == providedMoneyOnCashier) {
                    message = "✅ Tout va bien, les montants correspondent.";
                } else if (calculatedMoneyOnCashier > providedMoneyOnCashier) {
                    message = "❌ Déficit de " + difference + " dinars.";
                    tutorialRequest.setDeficit(difference);
                } else {
                    message = "⚠️ Excédent de " + difference + " dinars.";
                    tutorialRequest.setSurplus(difference);
                }

                Tutorial tutorial = tutorialRepository.save(tutorialRequest);

                // Retourner la réponse avec le message de comparaison
                return new ResponseEntity<>(new CashCheckResponse(tutorial, message, calculatedMoneyOnCashier,
                        providedMoneyOnCashier, difference), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Classe pour structurer la réponse JSON
    static class CashCheckResponse {
        public Tutorial tutorial;
        public String message;
        public Double calculatedMoneyOnCashier;
        public Double providedMoneyOnCashier;
        public Double difference;

        public CashCheckResponse(Tutorial tutorial, String message, Double calculatedMoneyOnCashier, Double providedMoneyOnCashier, Double difference) {
            this.tutorial = tutorial;
            this.message = message;
            this.calculatedMoneyOnCashier = calculatedMoneyOnCashier;
            this.providedMoneyOnCashier = providedMoneyOnCashier;
            this.difference = difference;
        }
    }

    @PutMapping("/tutorials/{id}")
    @PreAuthorize("hasRole('MODERATOR')")
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
            _tutorial.setTotalOperationTreasury(totalOperationTreasury);

            _tutorial.setOperationPreviousRegulation(tutorial.getOperationPreviousRegulation());
            _tutorial.setOperationRegulationToday(tutorial.getOperationRegulationToday());
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

            //_tutorial.setOrganismId(tutorial.getOrganismId());
            _tutorial.setDescription(tutorial.getDescription());
            _tutorial.setPublished(tutorial.isPublished());
            return new ResponseEntity<>(tutorialRepository.save(_tutorial), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/tutorials/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HttpStatus> deleteTutorial(@PathVariable("id") long id) {
        try {
            tutorialRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/tutorials")
    @PreAuthorize("hasRole('ADMIN')")
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
        Double result = tutorialRepository.getFinalBalanceLastMonth();
        return Math.round(result * 1000) / 1000.0;
    }

    @GetMapping("/tutorials/treasuryOperationsLastRow")
    public double totalTreasuryOperationsLastRow(){
        return tutorialRepository.totalTreasuryOperationsLastRow();
    }

    @GetMapping("/tutorials/regulationOperationsLastRow")
    public double totalRegulationOperationsLastRow(){
        Double result = tutorialRepository.totalRegulationOperationsLastRow();
        return Math.round(result * 1000) / 1000.0;
    }

    @GetMapping("/tutorials/postalCurrentAccountLastRow")
    public double getPostalCurrentAccountLastRow(){
        return tutorialRepository.postalCurrentAccountLastRow();
    }

    @GetMapping("/tutorials/statesRepartitionLastRow")
    public double getStatesRepartitionLastRow(){
        return tutorialRepository.statesRepartition();
     }

    @GetMapping("/tutorials/expectedFlowLastRow")
    public double expectedFlowLastRow() {
        return tutorialRepository.expectedFlowLastRow();
    }

    @GetMapping("/tutorials/creditExpectedLastRow")
    public double creditExpectedLastRow() {
        return tutorialRepository.creditExpectedLastRow();
    }



}
