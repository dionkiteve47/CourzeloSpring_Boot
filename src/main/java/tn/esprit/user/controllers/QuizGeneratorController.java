//package tn.esprit.user.controllers;
//
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import tn.esprit.pidev.model.Question;
//import tn.esprit.pidev.service.ChatGPTService;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//@RestController
//public class QuizGeneratorController {
//
//
//    @Autowired
//    private ChatGPTService questionGeneratorService;
//
//    @GetMapping("/generate")
//    public ResponseEntity<String> generateQuestion(
//            @RequestParam(name = "topic") String topic,
//            @RequestParam(name = "category") String category,
//            @RequestParam(name = "difficultyLevel") String difficultyLevel
//    ) {
//        // Assuming you have a method to fetch a question based on category and difficulty level
//        Question question = fetchQuestion(category, difficultyLevel);
//        if (question == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Question not found for the specified criteria.");
//        }
//
//        // Generate question based on the fetched question and provided topic
//        String generatedQuestion = questionGeneratorService.generateQuestion(question, topic);
//        return ResponseEntity.ok(generatedQuestion);
//}
//
//    private Question fetchQuestion(String category, String difficultyLevel) {
//        // Assuming you have a list of questions hardcoded or fetched from some source
//        List<Question> questions = getSampleQuestions(); // You need to implement this method
//
//        // Loop through the list of questions and find the first question that matches the criteria
//        for (Question question : questions) {
//            if (question.getCategory().equals(category) && question.getDifficultyLevel().equals(difficultyLevel)) {
//                return question;
//            }
//        }
//
//        // Return null if no matching question is found
//        return null;
//    }
//
//    // Method to provide sample questions (Replace with your actual method to fetch questions)
//    private List<Question> getSampleQuestions() {
//        // Create a list of sample questions (replace with your actual logic to fetch questions)
//        List<Question> questions = new ArrayList<>();
//
//        // Add some sample questions
//        questions.add(new Question("What is the capital of France?", "Berlin", "Madrid", "Paris", "London", "Paris", "Easy", "Geography"));
//        questions.add(new Question("What is the largest planet in our solar system?", "Mars", "Jupiter", "Saturn", "Venus", "Jupiter", "Medium", "Science"));
//        // Add more questions as needed
//
//        return questions;
//    }
//
//
//}
//
