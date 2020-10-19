package rbi.codingtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rbi.codingtest.model.Transaction;

import java.util.Date;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Integer> {

    @Query(value = "from Transaction t where t.createdAt BETWEEN :startDate AND :endDate")
    public List<Transaction> getAllBetweenDates(@Param("startDate") Date startDate, @Param("endDate")Date endDate);

}
