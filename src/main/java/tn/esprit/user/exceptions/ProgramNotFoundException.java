package tn.esprit.user.exceptions;

public class ProgramNotFoundException extends RuntimeException {
    public ProgramNotFoundException(String message) {
        super(message);
    }
}
