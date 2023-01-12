package pl.project.budgetassistant.exceptions;

public class EmptyStringException extends Exception {
    public EmptyStringException(String text) {
        super(text);
    }
}
