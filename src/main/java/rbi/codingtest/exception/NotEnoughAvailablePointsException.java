package rbi.codingtest.exception;

public class NotEnoughAvailablePointsException extends Exception {
    public NotEnoughAvailablePointsException(int availablePoints){
        super("Not Enough Available Points to use\nAvailable points : "+availablePoints);
    }
    public NotEnoughAvailablePointsException(String message){
        super(message);
    }
}
