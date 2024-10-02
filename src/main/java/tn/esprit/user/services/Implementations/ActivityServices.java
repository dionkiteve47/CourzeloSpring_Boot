package tn.esprit.user.services.Implementations;
//import com.example.springback.Entity.Activity;
//import com.example.springback.Entity.ActivityStats;
//import com.example.springback.Repo.ActivityRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile; // For MultipartFile
import org.bson.types.Binary; // For Binary
import tn.esprit.user.entities.Activity;
import tn.esprit.user.entities.ActivityStats;
import tn.esprit.user.repositories.ActivityRepo;


import java.io.File;
import java.io.IOException; // For IOException
import java.util.List;
import java.util.Optional;
//import com.twilio.Twilio;
//import com.twilio.rest.api.v2010.account.Message;
//import com.twilio.type.PhoneNumber;

@Service
public class ActivityServices {
    @Autowired
    private ActivityRepo repo;
    String imageUrl = "images/";
    /*public void saveOrUpdate(Activity activity, MultipartFile image) {
        try {
            if (!image.isEmpty()) {
                String imageUrl = "images/" + image.getOriginalFilename();
                File uploadDirectory = new File("C:\\Users\\hamza\\OneDrive\\Bureau\\KRAYA\\4SAE\\launchIt\\src\\main\\resources\\static\\images\\");
                image.transferTo(new File(uploadDirectory, image.getOriginalFilename()));
                activity.setImage(imageUrl);
            }
            // Save or update activity in the database
// Your code to save or update activity goes here
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception
        }
    }*/


    /*public void saveOrUpdate(Activity activity, MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                Binary imageBinary = new Binary(imageFile.getBytes());
                activity.setImage(imageBinary);
            } catch (IOException e) {
                // Handle the error scenario
                e.printStackTrace();
            }
        }
        // No changes needed for the date as it should already be set in the Activity object
        repo.save(activity);
    }*/

    //twilio DONE
    /*
    public void sendSms(String to, String message) {
        String ACCOUNT_SID = System.getenv("AC3b21d23b8d2a2b69b3da427c1b76d1ac");
        String AUTH_TOKEN = System.getenv("ed7d1b731e8d3e119898a8cdca453808");
        String FROM_NUMBER = System.getenv("+15076974179");

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message.creator(
                new PhoneNumber(to),  // To number
                new PhoneNumber(FROM_NUMBER),  // From number
                message  // SMS body
        ).create();
    }
*/
    public void saveorUpdate(Activity activities) {

        repo.save(activities);
    }
    public Activity save(Activity activities) {

       return repo.save(activities);
    }
    public Iterable<Activity> listAll() {
        return this.repo.findAll();
    }

    public void deleteActivity(String id) {
        this.repo.deleteById(id);
    }

    public Activity getActivityByID(String activityId) {

        return this.repo.findById(activityId).get();
    }

    public List<Activity> searchByName(String name) {
        return this.repo.findByActivityNameContainingIgnoreCase(name);
    }
    public Optional<Activity> getActivitytById2(String id) {
        return repo.findById(id);
    }

    public List<ActivityStats> countTotalActivitiesByClub() {
        return repo.countTotalActivitiesByClub();
    }
}