package com.pismo.techcase.transaction;

import jakarta.persistence.LockModeType;
import java.util.List;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select t
        from Transaction t
        where t.account.id = :accountId
          and t.balance < 0
        order by t.eventDate, t.id
        """)
    List<Transaction> findOutstandingDebitsForUpdate(@Param("accountId") long accountId);
}
