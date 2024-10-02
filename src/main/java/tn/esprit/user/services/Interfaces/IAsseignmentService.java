package tn.esprit.user.services.Interfaces;


import tn.esprit.user.entities.Assignment;

import java.util.List;

public interface IAsseignmentService {
    void saveAssignment(Assignment assignment);
    void deleteAsseignment(String assignment);
    void updateAsseignment (Assignment assignment);
    Assignment getAsseignmentByID(String assignmentID);
    List<Assignment> getAsseignments();
}
