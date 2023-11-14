import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Transation {
    LocalDateTime date;
    double cashMoved = 0;
    String cashAccountKey;
    DateTimeFormatter f = DateTimeFormatter.ofPattern("EE-dd/MM/yyyy HH:mm:ss",Locale.ITALIAN);
    

    public Transation(){
        this.cashMoved = 0;
        this.cashAccountKey = null;
        date =LocalDateTime.now();
    }
    public Transation(String cashAccountkey, LocalDateTime d, double cashMoved){
        this.cashMoved = cashMoved;
        this.cashAccountKey = cashAccountkey;
        date = d;
    }
    public void moveTransation(double cashMoved, String cashAccountkey){
        this.cashMoved = cashMoved;
        this.cashAccountKey = cashAccountkey;
        date =LocalDateTime.now();
    }
    
    
    public String getKey(){
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
    public String getValueTransf(){

        String trans = null;
        if(cashAccountKey != null){
            trans = date.format(f) + ";" + cashMoved ;
        }else{
            trans =date.format(f) + ";" + 0;
        }
        return trans;
    }
}

