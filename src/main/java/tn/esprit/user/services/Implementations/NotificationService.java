package tn.esprit.user.services.Implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.user.entities.Notification;
import tn.esprit.user.repositories.NotificationRepository;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    public void sendNotification(String userId, String title, String message) {
        // Send email notification to specified email address
        String toEmail = "arbi.ferchichi@esprit.tn"; // Specify the email address here
        emailSenderService.sendSimpleEmail(toEmail, title, message);
    }
    public List<Notification> getNotifications(String userId) {
        return notificationRepository.findByUserId(userId);
    }

}