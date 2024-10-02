package tn.esprit.user.services.Interfaces;

import tn.esprit.user.entities.CourseContent;

import java.util.List;

public interface ICoursContentService {


    CourseContent saveCoursContent(CourseContent courseContent);
    void deleteCoursContent(String courseContent);
    CourseContent updateCoursContent (CourseContent courseContent);
    List<CourseContent> getCoursContentByID(String courseContentID);
    List<CourseContent> getCoursContents();
}