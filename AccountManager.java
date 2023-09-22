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
    private HashMap<String, Transation> lastTransation;

    public AccountManager() {
        this.serchAccount = new HashMap<>();
        this.lastTransation = new HashMap<>();
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

    public synchronized String extractAll() throws InterruptedException {
        String listAccount = "";
        for (Map.Entry<String, Account> entry : serchAccount.entrySet()) {
            String indice = entry.getKey();
            Account valueAccount = entry.getValue();

            listAccount = listAccount + entry + "Codice: " + indice + 
            "\n" + valueAccount;
        }
        notifyAll();
        return listAccount; 
    }







    /*
     * gestisco l'HASHMAP delle TRANSAZIONI
     * 
     */



    public synchronized void add(String Tkey, Transation lastT) throws InterruptedException {
        if (this.lastTransation.get(Tkey) == null) {
             // Non esiste un TRANSAZIONE con la stessa chiave, quindi possiamo aggiungerla
            this.lastTransation.put(Tkey,lastT);
        }else{
            // Esiste già una TRANSAZIONE con la stessa chiave
            this.lastTransation.remove(Tkey);
            this.lastTransation.put(Tkey,lastT);
        } 
        notifyAll();
    }

    /*  implemento l'estrazione dell'ultima transazione [nel momento che mi serve per il comando "LIST"]
    *   quindi estraggo transazione data la chiave (ovvero il nome di un account univoco)
    *    e poi chiamo su quell'oggetto che mi viene restituito dall'HashMap(ogg. tipo Transation)
    *   il comando toString per leggere l'ultima transazione
    */

    /*
        public synchronized Transation extractTransation(String Tkey) throws InterruptedException {
        Transation last = this.lastTransation.get(Tkey);
        if(last == null) {
            // La Transazione con la chiave specificata non esiste
            throw new IllegalArgumentException("Account " + Tkey + " non presenta transazioni");
        }else

        notifyAll();
        return ;
    }
     */


}
