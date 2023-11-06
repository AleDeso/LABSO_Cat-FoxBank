import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transation {
    LocalDateTime date;
    double cashMoved = 0;
    String cashAccountKey;
    DateTimeFormatter f = DateTimeFormatter.ofPattern("EE, dd/MM/yyyy, 'ore:' HH:mm:ss");
    

    public Transation(){
        this.cashMoved = 0;
        this.cashAccountKey = null;
        date =LocalDateTime.now();
    }
    public void moveTransation(double cashMoved, String cashAccountkey){
        this.cashMoved = cashMoved;
        this.cashAccountKey = cashAccountkey;
        date =LocalDateTime.now();
    }

    
    public String getTKey(){
        return cashAccountKey;
    }

    public String toString(){

        String trans = null;
        if(cashAccountKey != null){
            trans = "    #:# last transation: "+ cashMoved + 
            "\tat: " + date.format(f);
        }else{
            trans = " #:# no transation --> creation date: " + date.format(f);
        }
        return trans;
    }
}

