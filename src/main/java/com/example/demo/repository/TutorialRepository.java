package com.example.demo.repository;

import com.example.demo.model.Tutorial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TutorialRepository extends JpaRepository<Tutorial, Long> {
    Page<Tutorial> findByPublished(boolean published, Pageable pageable);

    Page<Tutorial> findByTitleContaining(String title, Pageable pageable);

    List<Tutorial> findByOrganismId(UUID organismId);

    List<Tutorial> findByTitleContaining(String title, Sort sort);

    Page<Tutorial> findByTitleContainingAndOrganismCode(String title, String organismCode, Pageable pageable);
    Page<Tutorial> findByOrganismCode(String organismCode, Pageable pageable);


    @Query(value = "SELECT CAST(SUM(da.recipe_today + da.balance_previous_month ) as decimal(10,3)) AS total_recettes\n" +
            "            FROM public.tutorials da\n" +
            "            WHERE TO_CHAR(current_date, 'dd-mm-yyyy') = SUBSTRING(da.title, 20, 10);",
            nativeQuery = true)
    public Double totalRecipe();

    @Query(value = "SELECT CAST(SUM(da.recipe_today + da.balance_previous_month) AS decimal(10,3)) AS total_recettes\n" +
            "FROM public.tutorials da\n" +
            "WHERE TO_CHAR(CAST(:givenDate AS date), 'dd-mm-yyyy') = SUBSTRING(da.title, POSITION('Operation Date is: ' IN da.title) + LENGTH('Operation Date is: '), POSITION('Operation Date is: ' IN da.title) + LENGTH('Operation Date is: ') + 10)",
            nativeQuery = true)
    Double totalRecipeWithDate(@Param("givenDate") Date givenDate);


    @Query(value = "SELECT CAST(SUM(da.operation_treasury_anterior + da.operation_treasury_today ) as decimal(10,3)) AS total_recettes\n" +
            "    FROM public.tutorials da\n" +
            "    WHERE TO_CHAR(current_date, 'dd-mm-yyyy') = SUBSTRING(da.title, 20, 10);",
            nativeQuery = true)
    public Double totalTreasuryOperations();
    @Query(value = "SELECT CAST(SUM(da.operation_regulation_prior + da.operation_regulation_today ) as decimal(10,3)) AS total_recettes\n" +
            "    FROM public.tutorials da\n" +
            "    WHERE TO_CHAR(current_date, 'dd-mm-yyyy') = SUBSTRING(da.title, 20, 10);",
            nativeQuery = true)
    public Double totalRegulationOperation();

    @Query(value = "SELECT CAST(SUM(da.postal_current_account + da.credit_expected - da.expected_flow ) as decimal(10,3)) AS total_recettes\n" +
            "            FROM public.tutorials da\n" +
            "            WHERE TO_CHAR(current_date, 'dd-mm-yyyy') = SUBSTRING(da.title, 20, 10);",
            nativeQuery = true)
    public Double finalPostalCurrentAccount();

    @Query(value = "SELECT CAST(da.other_values as decimal(10,3)) AS total_recettes\n" +
            "            FROM public.tutorials da\n" +
            "            WHERE TO_CHAR(current_date, 'dd-mm-yyyy') = SUBSTRING(da.title, 20, 10);", nativeQuery = true)
    public Double getOtherValues();

    @Query(value = "SELECT CAST(da.states_repartition as decimal(10,3)) AS total_recettes\n" +
            "            FROM public.tutorials da\n" +
            "            WHERE TO_CHAR(current_date, 'dd-mm-yyyy') = SUBSTRING(da.title, 20, 10);", nativeQuery = true)
    public Double getStatesRepartition();

    @Query(value = "SELECT CAST(da.money_species as decimal(10,3)) AS total_recettes\n" +
            "            FROM public.tutorials da\n" +
            "            WHERE TO_CHAR(current_date, 'dd-mm-yyyy') = SUBSTRING(da.title, 20, 10);", nativeQuery = true)
    public Double getMoneySpices();

    @Query(value = "SELECT\n" +
            "    (da.balance_previous_month + da.recipe_today) -\n" +
            "    (da.operation_treasury_anterior + da.operation_treasury_today + da.operation_regulation_prior + da.operation_regulation_today) AS recette_total\n" +
            "FROM\n" +
            "    public.tutorials da\n" +
            "WHERE\n" +
            "    TO_DATE(SUBSTRING(da.title, 20, 10), 'DD-MM-YYYY') = (\n" +
            "        SELECT\n" +
            "            CASE\n" +
            "                WHEN EXTRACT(ISODOW FROM last_day) IN (6, 7) THEN last_day - INTERVAL '1 day'\n" +
            "                ELSE last_day\n" +
            "                END AS last_weekday_in_month\n" +
            "        FROM (\n" +
            "                 SELECT\n" +
            "                     DATE_TRUNC('month', CURRENT_DATE) - INTERVAL '1 day' AS last_day\n" +
            "             ) subquery\n" +
            "    );", nativeQuery = true)
    public Double getFinalBalanceLastMonth();

    @Query(value = "WITH filtered_data AS (\n" +
            "    SELECT\n" +
            "        da.operation_treasury_anterior,\n" +
            "        da.operation_treasury_today,\n" +
            "        TO_DATE(SUBSTRING(da.title, 20, 10), 'DD-MM-YYYY') AS operation_date\n" +
            "    FROM\n" +
            "        public.tutorials da\n" +
            "    WHERE\n" +
            "        TO_DATE(SUBSTRING(da.title, 20, 10), 'DD-MM-YYYY') >= DATE_TRUNC('month', CURRENT_DATE)\n" +
            "      AND TO_DATE(SUBSTRING(da.title, 20, 10), 'DD-MM-YYYY') < DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 month'\n" +
            ")\n" +
            "SELECT\n" +
            "    (fd.operation_treasury_anterior + fd.operation_treasury_today) AS prev_operation_treasury_sum\n" +
            "FROM\n" +
            "    filtered_data fd\n" +
            "WHERE\n" +
            "    fd.operation_date = (\n" +
            "        SELECT MAX(operation_date)\n" +
            "        FROM filtered_data\n" +
            "        WHERE operation_date < CURRENT_DATE\n" +
            "    );", nativeQuery = true)
    public double totalTreasuryOperationsLastRow();

    @Query(value = "WITH filtered_data AS (\n" +
            "    SELECT\n" +
            "        da.operation_regulation_prior,\n" +
            "        da.operation_regulation_today,\n" +
            "        TO_DATE(SUBSTRING(da.title, 20, 10), 'DD-MM-YYYY') AS operation_date\n" +
            "    FROM\n" +
            "        public.tutorials da\n" +
            "    WHERE\n" +
            "        TO_DATE(SUBSTRING(da.title, 20, 10), 'DD-MM-YYYY') >= DATE_TRUNC('month', CURRENT_DATE)\n" +
            "      AND TO_DATE(SUBSTRING(da.title, 20, 10), 'DD-MM-YYYY') < DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 month'\n" +
            ")\n" +
            "SELECT\n" +
            "    (fd.operation_regulation_prior + fd.operation_regulation_today) AS prev_operation_regulation_sum\n" +
            "FROM\n" +
            "    filtered_data fd\n" +
            "WHERE\n" +
            "    fd.operation_date = (\n" +
            "        SELECT MAX(operation_date)\n" +
            "        FROM filtered_data\n" +
            "        WHERE operation_date < CURRENT_DATE\n" +
            "    );", nativeQuery = true)
    public double totalRegulationOperationsLastRow();

    @Query(value = "WITH filtered_data AS (\n" +
            "                SELECT\n" +
            "                    da.postal_current_account,\n" +
            "                    TO_DATE(SUBSTRING(da.title, 20, 10), 'DD-MM-YYYY') AS operation_date\n" +
            "                FROM\n" +
            "                    public.tutorials da\n" +
            "                WHERE\n" +
            "                    TO_DATE(SUBSTRING(da.title, 20, 10), 'DD-MM-YYYY') >= DATE_TRUNC('month', CURRENT_DATE)\n" +
            "                  AND TO_DATE(SUBSTRING(da.title, 20, 10), 'DD-MM-YYYY') < DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 month'\n" +
            "            )\n" +
            "            SELECT\n" +
            "                (fd.postal_current_account ) AS prev_operation_treasury_sum\n" +
            "            FROM\n" +
            "                filtered_data fd\n" +
            "            WHERE\n" +
            "                fd.operation_date = (\n" +
            "                    SELECT MAX(operation_date)\n" +
            "                    FROM filtered_data\n" +
            "                    WHERE operation_date < CURRENT_DATE\n" +
            "                );", nativeQuery = true)
    public double postalCurrentAccountLastRow();

    @Query(value = "WITH filtered_data AS (\n" +
            "                SELECT\n" +
            "                    da.states_repartition,\n" +
            "                    TO_DATE(SUBSTRING(da.title, 20, 10), 'DD-MM-YYYY') AS operation_date\n" +
            "                FROM\n" +
            "                    public.tutorials da\n" +
            "                WHERE\n" +
            "                    TO_DATE(SUBSTRING(da.title, 20, 10), 'DD-MM-YYYY') >= DATE_TRUNC('month', CURRENT_DATE)\n" +
            "                  AND TO_DATE(SUBSTRING(da.title, 20, 10), 'DD-MM-YYYY') < DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 month'\n" +
            "            )\n" +
            "            SELECT\n" +
            "                (fd.states_repartition ) AS prev_operation_treasury_sum\n" +
            "            FROM\n" +
            "                filtered_data fd\n" +
            "            WHERE\n" +
            "                fd.operation_date = (\n" +
            "                    SELECT MAX(operation_date)\n" +
            "                    FROM filtered_data\n" +
            "                    WHERE operation_date < CURRENT_DATE\n" +
            "                );", nativeQuery = true)
    public double statesRepartition();
    /////////////

    @Query(value = "WITH filtered_data AS (\n" +
            "                SELECT\n" +
            "                    da.credit_expected,\n" +
            "                    TO_DATE(SUBSTRING(da.title, 20, 10), 'DD-MM-YYYY') AS operation_date\n" +
            "                FROM\n" +
            "                    public.tutorials da\n" +
            "                WHERE\n" +
            "                    TO_DATE(SUBSTRING(da.title, 20, 10), 'DD-MM-YYYY') >= DATE_TRUNC('month', CURRENT_DATE)\n" +
            "                  AND TO_DATE(SUBSTRING(da.title, 20, 10), 'DD-MM-YYYY') < DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 month'\n" +
            "            )\n" +
            "            SELECT\n" +
            "                (fd.credit_expected ) AS prev_operation_treasury_sum\n" +
            "            FROM\n" +
            "                filtered_data fd\n" +
            "            WHERE\n" +
            "                fd.operation_date = (\n" +
            "                    SELECT MAX(operation_date)\n" +
            "                    FROM filtered_data\n" +
            "                    WHERE operation_date < CURRENT_DATE\n" +
            "                );", nativeQuery = true)
    public double creditExpectedLastRow();
    ////////////////////

    @Query(value = "WITH filtered_data AS (\n" +
            "                SELECT\n" +
            "                    da.expected_flow,\n" +
            "                    TO_DATE(SUBSTRING(da.title, 20, 10), 'DD-MM-YYYY') AS operation_date\n" +
            "                FROM\n" +
            "                    public.tutorials da\n" +
            "                WHERE\n" +
            "                    TO_DATE(SUBSTRING(da.title, 20, 10), 'DD-MM-YYYY') >= DATE_TRUNC('month', CURRENT_DATE)\n" +
            "                  AND TO_DATE(SUBSTRING(da.title, 20, 10), 'DD-MM-YYYY') < DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '1 month'\n" +
            "            )\n" +
            "            SELECT\n" +
            "                (fd.expected_flow ) AS prev_operation_treasury_sum\n" +
            "            FROM\n" +
            "                filtered_data fd\n" +
            "            WHERE\n" +
            "                fd.operation_date = (\n" +
            "                    SELECT MAX(operation_date)\n" +
            "                    FROM filtered_data\n" +
            "                    WHERE operation_date < CURRENT_DATE\n" +
            "                );", nativeQuery = true)
    public double expectedFlowLastRow();


        @Query("SELECT t FROM Tutorial t JOIN FETCH t.organism WHERE t.id = :id")
        Optional<Tutorial> findByIdWithOrganism(@Param("id") Long id);



}
