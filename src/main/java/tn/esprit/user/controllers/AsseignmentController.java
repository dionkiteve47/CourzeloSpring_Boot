package tn.esprit.user.controllers;

import tn.esprit.user.entities.Assignment;
import tn.esprit.user.services.Implementations.AssignmentService;
import tn.esprit.user.services.Implementations.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/asseignment")
@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600, allowedHeaders = "*", allowCredentials = "true")
public class AsseignmentController {

    @Autowired
    AssignmentService assignmentService;


    @PostMapping()
    public void saveAssignment(@RequestBody Assignment assignment){
        assignmentService.saveAssignment(assignment);
    }
    @PutMapping()
    public void updateAssignment(@RequestBody Assignment question){
        assignmentService.updateAsseignment(question);
    }
    @GetMapping("/{id}")
    public Assignment getAssignmentByID(@PathVariable String id){
        return assignmentService.getAsseignmentByID(id);
    }
    @GetMapping()
    public List<Assignment> getAssignments(){
        return  assignmentService.getAsseignments();
    }
    @DeleteMapping("/{id}")
    public String  DeleteAssignments(@PathVariable String id){
        assignmentService.deleteAsseignment(id);
        return  "delete";
    }

    @GetMapping("/Bycourse/{id}")
    public List<Assignment>  getAssignmentByCours(@PathVariable String id){
        return assignmentService.getAsseignmentByIDCours(id);
    }
}
