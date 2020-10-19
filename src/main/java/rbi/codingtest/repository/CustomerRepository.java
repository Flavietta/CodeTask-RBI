package rbi.codingtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rbi.codingtest.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer,Integer> {
}
