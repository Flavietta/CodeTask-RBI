package rbi.codingtest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import rbi.codingtest.exception.FewTransactionThisWeekException;
import rbi.codingtest.exception.NotEnoughAvailablePointsException;
import rbi.codingtest.exception.SpentLessThenException;
import rbi.codingtest.model.*;
import rbi.codingtest.repository.LoyaltyRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class LoyaltyService {

    @Autowired
    private LoyaltyRepository loyaltyRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ActivityService activityService;

    /**
     * This method calculate the pending points for the value entered in params <br>
     *     1 pending point for every euro until 5000 euro value of transaction <br>
     *     2 pending points for every euro from 5001 euro to 7500 euro value of transaction <br>
     *     3 pending points from 7501 euro value of transaction <br>
     *
     * <p>
             Example: <br>

             A transaction of 4500 euro => 4500 points <br>
             A transaction of 7800 euro -> 10900 points <br>

             5000 points for first 5000 euro <br>
             2*2500 points for 5001 -> 7500 euro <br>
             3*300 points for 7501 -> 7800 euro <br>
        </p>
     * @param value
     * @return points
     */
    public static Integer calculatePendingPoints(float value){
        float points = 0;
        if(value<=5000) points=value;
        else if (value>5000 && value<=7500) {
            points = (value-5000)*2 +5000;
        }else if(value>7500) points = (value-7500)*3 + 10000;
        return (int) points;
    }

    public void addPendingPoints(Transaction t){
        addPendingPoints(t.getCustomer(),calculatePendingPoints(t.getAmount()));
    }

    /**
     * Add Pending Points to the balance of Customer c, if the Customer has no balance yet, a new one
     * will be created. And then create Activity for the points allocation
     * @param c
     * @param amount
     */
    public void addPendingPoints(Customer c,Integer amount){
        LoyaltyPoints lp = new LoyaltyPoints();
        lp.setCustomer(c);
        Optional<LoyaltyPoints> newLPOptional = loyaltyRepository.findOne(Example.of(lp));
        if (!newLPOptional.isPresent()){
            lp.setPendingPoints(amount);
            lp.setAvailablePoints(0);
            createLoyaltyPoints(lp);
        }else {
            LoyaltyPoints newLp = newLPOptional.get();
            int pendingPoints = newLp.getPendingPoints();
            newLp.setPendingPoints(pendingPoints+amount);
            loyaltyRepository.save(newLp);
        }
        activityService.save(new Activity(amount+" Pending Points Added to your balance",
                ActivityType.PointsAllocation,
                c));
    }

    /**
     * transfer all the pending points of every customer to available points
     * this method is called every end of the week
     */
    public void transferPendingToAvailablePoints(){
        //Get All Loyalty Points
        loyaltyRepository.findAll().forEach(
                e-> {
                    try {
                        transferPendingToAvailablePoints(e);
                    } catch (SpentLessThenException spentLessThenException) {
                        System.out.println("Customer: " + e.getCustomer().getId());
                        spentLessThenException.printStackTrace();
                    } catch (FewTransactionThisWeekException fewTransactionThisWeekException) {
                        System.out.println("Customer: " + e.getCustomer().getId());
                        fewTransactionThisWeekException.printStackTrace();
                    }
                }
        );
    }

    /**
     * transfer the pending points of a certain account to available points
     * @param lp
     * @throws SpentLessThenException
     * @throws FewTransactionThisWeekException
     */
    public void transferPendingToAvailablePoints(LoyaltyPoints lp) throws SpentLessThenException, FewTransactionThisWeekException {
    //If Not end of the week throw new NotEndOfTheWeekException

        float spent = transactionService.spentThisWeek(lp.getCustomer());
        if (spent < 500)  throw new SpentLessThenException(spent,(float)500);
        if(!transactionService.makeTransactionEveryDayOfTheWeek(lp.getCustomer()))
            throw new FewTransactionThisWeekException("Transaction Every Day of the week");

        lp.setAvailablePoints(lp.getAvailablePoints()+lp.getPendingPoints());
        lp.setPendingPoints(0);
        loyaltyRepository.save(lp);

        activityService.save(new Activity(lp.getAvailablePoints()+" Pending Points Has been transferred to Available Points",
                ActivityType.PointsAllocation,
                lp.getCustomer()));
    }
    public void createLoyaltyPoints(LoyaltyPoints lp){
        loyaltyRepository.save(lp);
    }

    /**
     * use some or all available points. Every point is worth 1 euro cent
     * @throws NotEnoughAvailablePointsException
     * @param customer object to use the points
     * @param amount the amount of points to use
     *
     */
    public void useAvailablePoints(Customer customer,Integer amount) throws NotEnoughAvailablePointsException {
        LoyaltyPoints lp = loyaltyRepository.getByCustomer(customer);
        //Check if does not exist
        if(lp.getAvailablePoints()<(amount*100)) throw new NotEnoughAvailablePointsException(lp.getAvailablePoints());
        lp.setAvailablePoints(lp.getAvailablePoints()-(amount*100));
        loyaltyRepository.save(lp);

        activityService.save(new Activity("Usage of "+amount*100+" Points.",
                ActivityType.PointsUsage,
                customer));
    }

    public Optional<LoyaltyPoints> get(Customer customer){
        LoyaltyPoints lp = new LoyaltyPoints();
        lp.setCustomer(customer);
        return loyaltyRepository.findOne(Example.of(lp));
    }

    public void deleteByCustomerId(Integer customerId){
        Customer c = new Customer();
        c.setId(customerId);
        LoyaltyPoints lp = get(c).get();
        lp.setPendingPoints(0);
        lp.setAvailablePoints(0);
        loyaltyRepository.save(lp);
        // loose all points => change them to 0
    }
}
