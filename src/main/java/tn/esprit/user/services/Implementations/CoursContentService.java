package tn.esprit.user.services.Implementations;

import tn.esprit.user.entities.CourseContent;
import tn.esprit.user.repositories.CoursContentRepository;
import tn.esprit.user.services.Interfaces.ICoursContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoursContentService implements ICoursContentService {

    @Autowired
    CoursContentRepository coursContentRepository ;

    @Override
    public CourseContent saveCoursContent(CourseContent courseContent) {
        return coursContentRepository.save(courseContent);
    }

    @Override
    public void deleteCoursContent(String courseContent) {
        coursContentRepository.deleteById(courseContent);
    }

    @Override
    public CourseContent updateCoursContent(CourseContent courseContent) {
        return coursContentRepository.save(courseContent);
    }

    @Override
    public List<CourseContent> getCoursContentByID(String courseContentID) {
        return coursContentRepository.findAllByCoursId(courseContentID);
    }

    @Override
    public List<CourseContent> getCoursContents() {
        return coursContentRepository.findAll();
    }

    public CourseContent getContentById(String courseContentID) {
        return coursContentRepository.findById(courseContentID).get();
    }
}