import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transation {
    LocalDateTime date;
    double cashMoved = 0;
    String cashAccountKey;
    DateTimeFormatter f = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy 'ore:' HH:mm:ss");
    

    public Transation(double cashMoved, String cashAccountkey){
        this.cashMoved = cashMoved;
        this.cashAccountKey = cashAccountkey;
        date =LocalDateTime.now();
        //Memorizzo in una HasMap
    }

    
    public String getTKey(){
        return cashAccountKey;
    }

    public String toString(){
        String trans = "ultima transazione di: " + cashAccountKey + 
        "\n Ammonta a: "+ cashMoved + 
        "\n Avvenuta: " + date.format(f);
        return trans;
    }
}

