package dh0023.example.springbatchreal.jobs.mysql.repository;


import dh0023.example.springbatchreal.jobs.mysql.entity.Naccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface NaccountRepository extends JpaRepository<Naccount, Long> {


    @Query("SELECT MAX(n.accountId) " +
            "FROM Naccount n " +
            "WHERE n.lastStatementDate BETWEEN :startDate AND :endDate")
    Long findMaxId(@Param("startDate") LocalDate startDate,
                   @Param("endDate") LocalDate endDate);

    @Query("SELECT MIN(n.accountId) " +
            "FROM Naccount n " +
            "WHERE n.lastStatementDate BETWEEN :startDate AND :endDate")
    Long findMinId(@Param("startDate") LocalDate startDate,
                   @Param("endDate") LocalDate endDate);
}
