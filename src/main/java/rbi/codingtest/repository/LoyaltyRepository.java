package rbi.codingtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rbi.codingtest.model.Customer;
import rbi.codingtest.model.LoyaltyPoints;

public interface LoyaltyRepository extends JpaRepository<LoyaltyPoints,Integer> {
    public LoyaltyPoints getByCustomer_Id(Integer id);
    public LoyaltyPoints getByCustomer(Customer c);
    public LoyaltyPoints deleteByCustomer_Id(Integer customerId);
}
