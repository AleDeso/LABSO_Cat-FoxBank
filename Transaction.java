import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Transaction {
    LocalDateTime date;
    double cashMoved = 0;
    String cashAccountKey;
    DateTimeFormatter f = DateTimeFormatter.ofPattern("EE-dd/MM/yyyy HH:mm:ss",Locale.ITALIAN);
    

    public Transaction(){
        this.cashMoved = 0;
        this.cashAccountKey = null;
        date =LocalDateTime.now();
    }
    public Transaction(String cashAccountkey, String d, double cashMoved){
        this.cashMoved = cashMoved;
        this.cashAccountKey = cashAccountkey;
        date = LocalDateTime.parse(d,f);
    }
    public Transaction(String cashAccountkey, LocalDateTime d, double cashMoved){
        this.cashMoved = cashMoved;
        this.cashAccountKey = cashAccountkey;
        date = d;
    }
    public void moveTransaction(double cashMoved, String cashAccountkey){
        this.cashMoved = cashMoved;
        this.cashAccountKey = cashAccountkey;
        date =LocalDateTime.now();
    }
    

    public String toString(){

        String trans = null;
        if(cashAccountKey != null){
            trans = "    #:# last transaction: "+ cashMoved + 
            "\tat: " + date.format(f);
        }else{
            trans = " #:# no transaction --> creation date: " + date.format(f);
        }
        return trans;
    }
    // Metodo utilizzato per scrivere sul file csv
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

