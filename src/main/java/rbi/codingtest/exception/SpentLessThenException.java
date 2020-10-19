package rbi.codingtest.exception;

public class SpentLessThenException extends Exception {


    public SpentLessThenException(String message){
        super(message);
    }

    public SpentLessThenException(float amountSpent,float minimumAmount){
        super("You have to spend more than "+ minimumAmount +" this week, you only have spent "+amountSpent);
    }
}
