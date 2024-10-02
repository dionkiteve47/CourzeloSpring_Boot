package tn.esprit.user.services.Implementations;

//import com.example.springback.Entity.Reservation;
//import com.example.springback.Entity.Seat;
//import com.example.springback.Repo.ReservationRepository;
//import com.example.springback.Repo.SeatRepository;
//import com.example.springback.Repo.UserHamzaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.user.entities.Reservation;
import tn.esprit.user.entities.Seat;
import tn.esprit.user.repositories.ReservationRepository;
import tn.esprit.user.repositories.SeatRepository;
import tn.esprit.user.repositories.UserHamzaRepo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SeatingService {

    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final UserHamzaRepo userHamzaRepo;

    @Autowired
    public SeatingService(SeatRepository seatRepository, ReservationRepository reservationRepository, UserHamzaRepo userHamzaRepo) {
        this.seatRepository = seatRepository;
        this.reservationRepository = reservationRepository;
        this.userHamzaRepo = userHamzaRepo;
    }

    public List<Seat> getSeatsForActivity(String activityId) {
        return seatRepository.findByActivityId(activityId);
    }

    @Transactional //locki el seat during reservation
    public List<Reservation> reserveSeats(String userId, List<String> seatIds) {
        // Check if seats are already reserved
        for (String seatId : seatIds) {
            Optional<Seat> seatOptional = seatRepository.findById(seatId);
            if (!seatOptional.isPresent() || seatOptional.get().getReserved()) {
                throw new IllegalStateException("Seat is already reserved or does not exist");
            }
        }

        // Reserve the seats
        List<Seat> seatsToReserve = seatRepository.findAllById(seatIds);
        seatsToReserve.forEach(seat -> seat.setReserved(true));
        seatRepository.saveAll(seatsToReserve);

        // Create reservations
        return seatsToReserve.stream().map(seat -> {
            Reservation reservation = new Reservation();
            reservation.setSeat(seat);
            reservation.setUser(userHamzaRepo.findById(userId).orElse(null));
            return reservationRepository.save(reservation);
        }).collect(Collectors.toList());
    }
}
