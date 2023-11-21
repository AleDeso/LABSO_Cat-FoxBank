import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Account {
    
    private String name = null;
    private double money = 0;
    private Transaction lastT;
    DateTimeFormatter f = DateTimeFormatter.ofPattern("EE-dd/MM/yyyy HH:mm:ss", Locale.ITALIAN);
    
    public Account(String name, double money){
        this.name = name;
        this.money = money;
        lastT = new Transaction();
    }
    // richiamo per caricare dati da database
    public Account(String name, double money,String t, double m){
        this.name = name;
        this.money = money;
        lastT = new Transaction(name, LocalDateTime.parse(t,f), m);
    }

    public String getName(){
        return name;
    }
    public double getMoney(){
        return money;
    }
    public Transaction getTransaction(){
        return lastT;
    }
    public String getTransfer(){
        return lastT.getValueTransf();
    }
    public void setTransaction(double m, String n){
        lastT.moveTransaction(m, n);
    }

    public void InFlow(double cash){
        money = money + cash;
    }
    public void OutFlow(double cash){
        money = money - cash;
    }

    public String toString(){
        return "name: " + name + "  balance: " + money + lastT;
    }

}
