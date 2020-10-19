package rbi.codingtest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import rbi.codingtest.exception.NotEnoughAvailablePointsException;
import rbi.codingtest.model.Customer;
import rbi.codingtest.model.Transaction;
import rbi.codingtest.service.LoyaltyService;

@Controller
@RequestMapping("loyalty")
public class LoyaltyController {

    @Autowired
    private LoyaltyService loyaltyService;

    @GetMapping("")
    public ResponseEntity<?> getLoyaltyPoints(
            @RequestParam(value = "customer_id",required = true,defaultValue = "0") Integer customerId,
            @RequestParam(value = "use",required = false) Integer amount){

        Customer customer = new Customer();
        customer.setId(customerId);
        if (amount!=null){
            try {
                loyaltyService.useAvailablePoints(customer,amount);
            } catch (NotEnoughAvailablePointsException e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body("Not Enough Available Points");
            }
        }
        return ResponseEntity.ok(loyaltyService.get(customer));
    }
}
