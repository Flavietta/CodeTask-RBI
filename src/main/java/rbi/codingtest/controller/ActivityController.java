package rbi.codingtest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rbi.codingtest.model.Activity;
import rbi.codingtest.model.ActivityType;
import rbi.codingtest.model.Customer;
import rbi.codingtest.service.ActivityService;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @GetMapping
    public ResponseEntity get(
            @RequestParam(value = "type",required=false) ActivityType activityType,
            @RequestParam(value = "customer",required = false) Integer customerId
            ){
        List<Activity> activities = Arrays.asList();
        if (customerId!=null){
            Customer c = new Customer();
            c.setId(customerId);
            if(activityType!=null){
                activities = activityService.get(c,activityType);
            }else {
                activities = activityService.get(c);
            }
        }else if(activityType!= null) {
                activities = activityService.get(activityType);
        }

        return ResponseEntity.ok(activities);
    }

    @PostMapping
    public ResponseEntity save(@RequestBody Activity activity){
        try {
            activityService.save(activity);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.ok().build();
    }
}
