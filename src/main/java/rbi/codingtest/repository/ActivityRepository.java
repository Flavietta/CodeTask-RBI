package rbi.codingtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rbi.codingtest.model.Activity;
import rbi.codingtest.model.ActivityType;
import rbi.codingtest.model.Customer;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity,Integer> {
    public List<Activity> findAllByType(ActivityType type);
    public List<Activity> findAllByCustomer(Customer customer);
    public List<Activity> findAllByCustomerAndType(Customer customer,ActivityType type);
}
