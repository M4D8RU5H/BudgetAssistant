package pl.project.budgetassistant.exceptions;

public class ZeroBalanceDifferenceException extends Exception {
    public ZeroBalanceDifferenceException(String text) {
        super(text);
    }
}
