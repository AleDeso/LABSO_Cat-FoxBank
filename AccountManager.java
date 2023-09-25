import java.util.HashMap;
import java.util.Map;

/*
 * Resource implementa una semplice HashMap con associazioni String->String. Ogni chiave ha una sola stringa associata.
 * 
 * La risorsa è accessibile da diversi thread, che possono inserire elementi o estrarli dalla mappa.
 * Se viene richiesto di inserire una chiave già esistente, il thread viene messo in attesa;
 * se viene richiesto di estrarre una chiave non esistente, il thread viene messo in attesa.
 * 
 */
public class AccountManager {
    private HashMap<String, Account> serchAccount;

    public AccountManager() {
        this.serchAccount = new HashMap<>(); // usa nome account come chiave e account come parametro
    }

    /*
     * Inserimento di un elemento nella mappa.
     * 
     * Se la chiave esiste già, rimaniamo in attesa, rilasciando il lock
     * sull'oggetto.
     * 
     * Una volta sbloccati e inserito il nuovo elemento, svegliamo eventuali thread
     * in attesa.
     */

    public synchronized void add(String key, Account bankAccount) throws InterruptedException {
        if (this.serchAccount.get(key) == null) {
             // Non esiste un account con la stessa chiave, quindi possiamo aggiungerlo
            this.serchAccount.put(key,bankAccount );
        }else{
            // Esiste già un account con la stessa chiave
            throw new IllegalArgumentException("Account: " + key + " gia esistente.");
        }
        notifyAll();
    }

    /*
     * Estrazione di una valore dalla mappa; una volta ottenuto il valore, la chiave
     * viene rimossa.
     * 
     * Duale di add(), rimaniamo in attesa finché la chiave non ha un valore
     * associato.
     */
    public synchronized Account extract(String key) throws InterruptedException {
        Account found = this.serchAccount.get(key);
        if(found == null) {
            // L'account con la chiave specificata non esiste
            throw new IllegalArgumentException("Account " + key + " non trovato");
        }
        notifyAll();
        return found;
    }

    /*public synchronized String extractAll() throws InterruptedException {
        String listAccount = "";
        for (Map.Entry<String, Account> entry : serchAccount.entrySet()) {
            String indice = entry.getKey();
            Account valueAccount = entry.getValue();

            listAccount = listAccount + "Codice: " + indice + " " +  valueAccount + "\n";
        }
        notifyAll();
        return listAccount; 
    }*/
    public synchronized String extractAll() throws InterruptedException {
        StringBuilder listAccountBuilder = new StringBuilder(); // Crea un oggetto StringBuilder
    
        int c = 0;
        for (Map.Entry<String, Account> entry : serchAccount.entrySet()) {
            //String indice = entry.getKey();
            Account valueAccount = entry.getValue();
///////////////////////7 Transation last = lastTransation.get(indice);
            c++;
            // Utilizza append() per aggiungere le informazioni dell'account al StringBuilder
            listAccountBuilder.append(c + ". ");
 ////           listAccountBuilder.append("Codice: ").append(indice).append("\t");
            listAccountBuilder.append(valueAccount).append("\n");
        }
    
        notifyAll();
        
        // Converti il contenuto di StringBuilder in una stringa
        String listAccount = listAccountBuilder.toString();
        
        return listAccount;
    }
    
}
