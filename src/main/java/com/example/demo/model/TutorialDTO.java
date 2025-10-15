package com.example.demo.model;

import java.io.Serializable;

public class TutorialDTO  implements Serializable {
    private Long id;
    private String title;
    private String description;
    private Double totalRecipeToday;
    private Double totalExpenses;
    private Double moneyOnCashier;
    private String organismCode; // <-- Add this field

    public TutorialDTO(Tutorial tutorial) {
        this.id = tutorial.getId();
        this.title = tutorial.getTitle();
        this.description = tutorial.getDescription();
        this.totalRecipeToday = tutorial.getTotalRecipeToday();
        this.totalExpenses = tutorial.getTotalExpenses();
        this.moneyOnCashier = tutorial.getMoneyOnCashier();
        this.organismCode = (tutorial.getOrganism() != null) ? tutorial.getOrganism().getCode() : null;
    }
}
