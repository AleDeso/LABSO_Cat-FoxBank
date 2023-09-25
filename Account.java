public class Account {
    
    private String name = null;
    private double money = 0;
    private boolean isLocked = false;
    private Transation lastT;

    public Account(String name, double money){
        this.name = name;
        this.money = money;
        lastT = new Transation();
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
    public Transation getTransation(){
        return lastT;
    }
    public void setTransation(double m, String n){
        lastT.moveTransation(m, n);
    }

    public void InFlow(double cash){
        money = money + cash;
    }

    public void OutFlow(double cash){
        money = money - cash;
    }

    public String toString(){
        return "name: " + name + "  \tbalance: " + money + lastT;
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
