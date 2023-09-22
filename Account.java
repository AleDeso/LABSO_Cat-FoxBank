public class Account {
    
    private String name = null;
    private double money = 0;
    private boolean isLocked = false;
    //private String lastTransationKey;

    public Account(String name, double money){
        this.name = name;
        this.money = money;
    }
     public Account(String name){
        this.name = name;
        this.money = 0;
    }

    public String getName(){
        return name;
    }

    public double getMoney(){
        return money;
    }

    public void InFlow(double cash){
        money = money + cash;
    }

    public void OutFlow(double cash){
        money = money - cash;
    }

    public String toString(){
        return "name: " + name + "\nmoney: " + money + "\n";
    }

    // lock
    public synchronized void lock() {
        isLocked = true;
    }

    public synchronized void unlock() {
        isLocked = false;
        notifyAll(); // Notifica eventuali thread in attesa
    }

    public synchronized boolean isLocked() {
        return isLocked;
    }

}
