package rbi.codingtest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rbi.codingtest.model.Activity;
import rbi.codingtest.model.ActivityType;
import rbi.codingtest.model.Customer;
import rbi.codingtest.repository.ActivityRepository;

import java.util.List;

@Service
public class ActivityService {
    @Autowired
    ActivityRepository activityRepository;

    public List<Activity> get(ActivityType type){
        return activityRepository.findAllByType(type);
    }
    public List<Activity> get(Customer c){
        return activityRepository.findAllByCustomer(c);
    }
    public List<Activity> get(Customer c,ActivityType type){
        return activityRepository.findAllByCustomerAndType(c,type);
    }
    public void save(Activity a){
        activityRepository.save(a);
    }
}
