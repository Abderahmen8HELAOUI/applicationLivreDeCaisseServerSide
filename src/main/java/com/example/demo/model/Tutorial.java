package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tutorials")
public class Tutorial {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "title")
    private String title;

    @Column(name = "recipe_Today")
    private double recipeToday;

    @Column(name = "balance_Previous_Month")
    private double balancePreviousMonth;

    @Column(name = "totalRecipeToday")
    private double totalRecipeToday;

    @Column(name = "operation_Treasury_Anterior")
    private double operationTreasuryAnterior;

    @Column(name = "operation_Treasury_Today")
    private double operationTreasuryToday;

    @Column(name = "totalOperationTreasury")
    private double totalOperationTreasury;

    @Column(name = "operation_Regulation_Prior")
    private double operationPreviousRegulation;

    @Column(name = "operation_Regulation_Today")
    private double operationRegulationToday;

    @Column(name = "totalOperationRegulation")
    private double totalOperationRegulation;

    @Column(name = "totalExpenses")
    private double totalExpenses;

    @Column(name = "finalBalanceToday")
    private double finalBalanceToday;

    @Column(name = "postal_Current_Account")
    private double postCurrentAccount;

    @Column(name = "credit_Expected")
    private double creditExpected;

    @Column(name = "expected_Flow")
    private double rateExpected;

    @Column(name = "other_Values")
    private double otherValues;

    @Column(name = "finalPostCurrentAccount")
    private double finalPostCurrentAccount;

    @Column(name = "states_Repartition")
    private double statesRepartition;

    @Column(name = "totalCash")
    private double totalCash;

    @Column(name = "money_Species")
    private double moneySpecies;

    @Column(name = "moneyOnCashier")
    private double moneyOnCashier;

    @Column(name = "organism_id")
    private double organismId;

    @Column(name = "description")
    private String description;

    @Column(name = "published")
    private boolean published;

    public Tutorial() {

    }

    public Tutorial(String title,
                    double recipeToday, double balancePreviousMonth, double totalRecipeToday,
                    double operationTreasuryAnterior, double operationTreasuryToday, double totalOperationTreasury,
                    double operationPreviousRegulation, double operationRegulationToday, double totalOperationRegulation,
                    double totalExpenses, double finalBalanceToday,
                    double postCurrentAccount, double creditExpected, double rateExpected, double finalPostCurrentAccount,
                    double otherValues,
                    double statesRepartition,
                    double totalCash,
                    double moneySpecies,
                    double moneyOnCashier,
                    double organismId, String description,
                    boolean published) {
        this.title = title;
        this.recipeToday = recipeToday;
        this.balancePreviousMonth = balancePreviousMonth;
        this.totalRecipeToday = totalRecipeToday;

        this.operationTreasuryAnterior = operationTreasuryAnterior;
        this.operationTreasuryToday = operationTreasuryToday;
        this.totalOperationTreasury = totalOperationTreasury;

        this.operationPreviousRegulation = operationPreviousRegulation;
        this.operationRegulationToday = operationRegulationToday;
        this.totalOperationRegulation = totalOperationRegulation;

        this.totalExpenses = totalExpenses;

        this.finalBalanceToday = finalBalanceToday;

        this.postCurrentAccount = postCurrentAccount;
        this.creditExpected = creditExpected;
        this.rateExpected = rateExpected;

        this.finalPostCurrentAccount = finalPostCurrentAccount;

        this.otherValues = otherValues;
        this.statesRepartition = statesRepartition;

        this.totalCash = totalCash;

        this.moneySpecies = moneySpecies;

        this.moneyOnCashier = moneyOnCashier;
        this.organismId = organismId;
        this.description = description;
        this.published = published;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getRecipeToday() {
        return recipeToday;
    }

    public void setRecipeToday(double recipeToday) {
        this.recipeToday = recipeToday;
    }

    public double getBalancePreviousMonth() {
        return balancePreviousMonth;
    }

    public void setBalancePreviousMonth(double balancePreviousMonth) {
        this.balancePreviousMonth = balancePreviousMonth;
    }

    public double getTotalRecipeToday() {
        return totalRecipeToday;
    }

    public void setTotalRecipeToday(double totalRecipeToday) {
        this.totalRecipeToday = totalRecipeToday;
    }

    public double getOperationTreasuryAnterior() {
        return operationTreasuryAnterior;
    }

    public void setOperationTreasuryAnterior(double operationTreasuryAnterior) {
        this.operationTreasuryAnterior = operationTreasuryAnterior;
    }

    public double getOperationTreasuryToday() {
        return operationTreasuryToday;
    }

    public void setOperationTreasuryToday(double operationTreasuryToday) {
        this.operationTreasuryToday = operationTreasuryToday;
    }

    public double getTotalOperationTreasury() {
        return totalOperationTreasury;
    }

    public void setTotalOperationTreasury(double totalOperationTreasury) {
        this.totalOperationTreasury = totalOperationTreasury;
    }

    public double getOperationPreviousRegulation() {
        return operationPreviousRegulation;
    }

    public void setOperationPreviousRegulation(double operationPreviousRegulation) {
        this.operationPreviousRegulation = operationPreviousRegulation;
    }

    public double getOperationRegulationToday() {
        return operationRegulationToday;
    }

    public void setOperationRegulationToday(double operationRegulationToday) {
        this.operationRegulationToday = operationRegulationToday;
    }

    public double getTotalOperationRegulation() {
        return totalOperationRegulation;
    }

    public void setTotalOperationRegulation(double totalOperationRegulation) {
        this.totalOperationRegulation = totalOperationRegulation;
    }

    public double getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(double totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public double getFinalBalanceToday() {
        return finalBalanceToday;
    }

    public void setFinalBalanceToday(double finalBalanceToday) {
        this.finalBalanceToday = finalBalanceToday;
    }

    public double getPostCurrentAccount() {
        return postCurrentAccount;
    }

    public void setPostCurrentAccount(double postCurrentAccount) {
        this.postCurrentAccount = postCurrentAccount;
    }

    public double getCreditExpected() {
        return creditExpected;
    }

    public void setCreditExpected(double creditExpected) {
        this.creditExpected = creditExpected;
    }

    public double getRateExpected() {
        return rateExpected;
    }

    public void setRateExpected(double rateExpected) {
        this.rateExpected = rateExpected;
    }

    public double getOtherValues() {
        return otherValues;
    }

    public void setOtherValues(double otherValues) {
        this.otherValues = otherValues;
    }

    public double getFinalPostCurrentAccount() {
        return finalPostCurrentAccount;
    }

    public void setFinalPostCurrentAccount(double finalPostCurrentAccount) {
        this.finalPostCurrentAccount = finalPostCurrentAccount;
    }

    public double getStatesRepartition() {
        return statesRepartition;
    }

    public void setStatesRepartition(double statesRepartition) {
        this.statesRepartition = statesRepartition;
    }

    public double getTotalCash() {
        return totalCash;
    }

    public void setTotalCash(double totalCash) {
        this.totalCash = totalCash;
    }

    public double getMoneySpecies() {
        return moneySpecies;
    }

    public void setMoneySpecies(double moneySpecies) {
        this.moneySpecies = moneySpecies;
    }

    public double getMoneyOnCashier() {
        return moneyOnCashier;
    }

    public void setMoneyOnCashier(double moneyOnCashier) {
        this.moneyOnCashier = moneyOnCashier;
    }

    public double getOrganismId() {
        return organismId;
    }

    public void setOrganismId(double organismId) {
        this.organismId = organismId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    @Override
    public String toString() {
        return "Tutorial [id=" + id + ", title=" + title + ", desc=" + description + ", published=" + published + "]";
    }

}
