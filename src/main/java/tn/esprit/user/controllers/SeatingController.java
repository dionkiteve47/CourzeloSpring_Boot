package tn.esprit.user.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.user.entities.Reservation;
import tn.esprit.user.entities.ReservationRequest;
import tn.esprit.user.entities.Seat;
import tn.esprit.user.services.Implementations.SeatingService;

import java.util.List;
@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600, allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/seating")
public class SeatingController {

    private final SeatingService seatingService;

    @Autowired
    public SeatingController(SeatingService seatingService) {
        this.seatingService = seatingService;
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<List<Seat>> getSeatingLayout(@PathVariable String activityId) {
        List<Seat> seats = seatingService.getSeatsForActivity(activityId);
        return ResponseEntity.ok(seats);
    }

    @PostMapping("/reserve")
    public ResponseEntity<List<Reservation>> reserveSeats(@RequestBody ReservationRequest request) {
        List<Reservation> reservations = seatingService.reserveSeats(request.getUserId(), request.getSeatIds());
        return ResponseEntity.ok(reservations);
    }
}
