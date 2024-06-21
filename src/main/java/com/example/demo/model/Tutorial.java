package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
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

    @Column(name = "description")
    private String description;

    @Column(name = "published")
    private boolean published;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organism_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Organism organism;

    @CreatedDate
    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createDate;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModified;


    @CreatedBy
    @Column(
            nullable = false,
            updatable = false
    )
    private String createdBy;

    @LastModifiedBy
    @Column(insertable = false)
    private String lastModifiedBy;

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
                   String description,
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

        this.description = description;
        this.published = published;
    }

    @Override
    public String toString() {
        return "Tutorial [id=" + id + ", title=" + title + ", desc=" + description + ", published=" + published + "]";
    }

}
