package rbi.codingtest.model;

import javax.persistence.*;

@Entity
public class LoyaltyPoints {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer pendingPoints;

    private Integer availablePoints;

    @OneToOne
    private Customer customer;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPendingPoints() {
        return pendingPoints;
    }

    public void setPendingPoints(Integer pendingPoints) {
        this.pendingPoints = pendingPoints;
    }

    public Integer getAvailablePoints() {
        return availablePoints;
    }

    public void setAvailablePoints(Integer availablePoints) {
        this.availablePoints = availablePoints;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
