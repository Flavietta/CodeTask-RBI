package rbi.codingtest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rbi.codingtest.model.Customer;
import rbi.codingtest.repository.CustomerRepository;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    public void create(Customer c){
        customerRepository.save(c);
    }

    public List<Customer> getAll(){
        return customerRepository.findAll();
    }
}
