package tn.esprit.user.controllers;

//import com.example.springback.Entity.Activity;
//import com.example.springback.Entity.Club;
//import com.example.springback.Service.ClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.user.entities.Club;
import tn.esprit.user.services.Implementations.ClubService;

import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600, allowedHeaders = "*", allowCredentials = "true")

@RequestMapping("api/v1/club")
public class ClubController {

    @Autowired
    private ClubService clubService;

    @GetMapping(value="/getAll")
    public Iterable <Club> getClubs()
    {

        return clubService.listAll();
    }


    @PostMapping(value="/save")
    private String saveActivity(@RequestBody Club clubs)
    {
        clubService.saveorUpdate(clubs);
        return clubs.get_id();
    }

    @PutMapping(value = "/edit/{id}")
    private Club updateClub(@RequestBody Club club, @PathVariable(name = "id") String id) {
        club.set_id(id);
        clubService.saveorUpdate(club);
        return club;
    }

    @DeleteMapping("/delete/{id}")
    private void deleteClub(@PathVariable("id") String _id) {

        clubService.deleteClub(_id);
    }

    @RequestMapping("/search/{id}")
    private Club getClubs(@PathVariable(name = "id") String clubId) {
        return clubService.getActivityByID(clubId);
    }

}
