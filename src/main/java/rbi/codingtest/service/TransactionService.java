package rbi.codingtest.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rbi.codingtest.model.Customer;
import rbi.codingtest.model.Transaction;
import rbi.codingtest.repository.TransactionRepository;
import rbi.codingtest.utils.DateTimeUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private LoyaltyService loyaltyService;


    private static HashMap<Customer,ScheduledFuture> tasksReferenceMap = new HashMap<>();

    public List<Transaction> getAll(){
        return transactionRepository.findAll();
    }
    public List<Transaction> getByCustomer(Customer customer){return null;}
    public List<Transaction> getByDate(Date from, Date to){
        return transactionRepository.getAllBetweenDates(from,to);
    }
    public List<Transaction> getByCustomerAndDate(Customer customer,Date from, Date to){return null;}

    /**
     * This method create a transaction and then schedule a task to remove all the points after 5 weeks
     * Everytime a transaction a created, the delay is reset.
     * @param t
     */
    public void createTransaction(Transaction t){
        transactionRepository.save(t);

        logger.debug("Transaction ["+t.getId()+"] Created on : " + LocalDateTime.now().toString());
        //This task timer is "refreshed" everytime a transaction is made
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        //Schedule a task to remove all the points after 5 weeks
        ScheduledFuture<?> result = scheduler.schedule(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                loyaltyService.deleteByCustomerId(t.getCustomer().getId());
                return true;
            }
        }, 7 * 5, TimeUnit.DAYS);


        if (tasksReferenceMap.containsKey(t.getCustomer())){
            tasksReferenceMap.get(t.getCustomer()).cancel(true);
            tasksReferenceMap.replace(t.getCustomer(),result);
        }else {
            tasksReferenceMap.put(t.getCustomer(),result);
        }

        //Create Loyalty points for this transaction
        loyaltyService.addPendingPoints(t);
    }

    /**
     * this method will check whether a Customer c made a transaction everyday of the week or not
     * @param c
     * @return
     */
    public boolean makeTransactionEveryDayOfTheWeek(Customer c){
        //Change it to this week and not last week
        Date today = new Date();
        Date lastWeek = DateTimeUtils.getLastWeek(today);
        List<Transaction> listTransaction = this.getByCustomerAndDate(c,lastWeek,today);

        listTransaction.sort(Comparator.comparing(Transaction::getCreatedAt));
        if(!listTransaction.isEmpty() && (DateTimeUtils.getDayOfTheWeek(listTransaction.get(0).getCreatedAt())!=1
                || DateTimeUtils.getDayOfTheWeek(listTransaction.get(listTransaction.toArray().length-1).getCreatedAt())!=7))
            return false;

        for (int i=1;i<listTransaction.toArray().length;i++){
            if(listTransaction.get(i).getCreatedAt().getDay()-listTransaction.get(i+1).getCreatedAt().getDay()>1){
                return false;
            };
        }
        return true;
    }


    public float spentThisWeek(Customer c){
        Date today = new Date();
        Date lastWeek = DateTimeUtils.getLastWeek(today);
        float res = this.getByCustomerAndDate(c,lastWeek,today).stream().map(e->e.getAmount()).reduce(0f,(a1, a2) -> a1+=a2 );
        return res;
    }



}