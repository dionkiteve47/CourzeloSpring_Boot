package tn.esprit.user.entities;

import java.util.List;
 //DTO (Data Transfer Object) that will carry the reservation details from the client to the server.
public class ReservationRequest {
    private String userId;
    private List<String> seatIds;

    // Constructors, getters, and setters
    // ...

     public ReservationRequest(String userId, List<String> seatIds) {
         this.userId = userId;
         this.seatIds = seatIds;
     }

     public String getUserId() {
         return userId;
     }

     public void setUserId(String userId) {
         this.userId = userId;
     }

     public List<String> getSeatIds() {
         return seatIds;
     }

     public void setSeatIds(List<String> seatIds) {
         this.seatIds = seatIds;
     }
 }
