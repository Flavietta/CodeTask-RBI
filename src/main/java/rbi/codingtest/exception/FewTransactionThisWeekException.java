package rbi.codingtest.exception;

public class FewTransactionThisWeekException extends Exception{

    public FewTransactionThisWeekException(int nbTransactionsMade,int minimumNbTransactions){
        super("You made less transactions then the minimum number this week : \n"
                +"Minimum transactions : "+minimumNbTransactions
                +"\nNumber of transaction :"+nbTransactionsMade);
    }

    public FewTransactionThisWeekException(String message){
        super(message);
    }
}
