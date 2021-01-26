package com.romana.devices.card;

import com.romana.database.Database;
import com.romana.database.Database;
import com.romana.database.DatabaseException;
import com.romana.database.DatabaseException;
import com.romana.devices.card.CardResponse;
import static com.romana.devices.card.CardResponse.CardCode.*;

/**
 * @author santiago
 */
public class CardDatabase extends Database{
    
    private static final String DB_FILEPATH = "db/cards_balance.json"; // From config!
    
    public CardDatabase() throws DatabaseException {
        super(DB_FILEPATH);
    }

    public Integer readCardBalance(String id) throws DatabaseException {
        reload();
        if (!isRegistered(id)){
            return null;
        }
        return (Integer) ((Double) get(id)).intValue();
    }

    public CardResponse discountMoney(String id, int amount) throws DatabaseException {

        Integer balance = readCardBalance(id);
        if (!isRegistered(id)) {
            return new CardResponse(CARD_NOT_REGISTERED);
        }
        if (balance < amount) {
            return new CardResponse(NO_MONEY, balance);
        }
        int newBalance = balance - amount;
        put(id, newBalance);
        save();
        return new CardResponse(OPERATION_OK, newBalance);
    }
    
    public CardResponse addMoney(String id, int amount) throws DatabaseException {
        Integer balance = readCardBalance(id);
        if (!isRegistered(id)) {
            return new CardResponse(CARD_NOT_REGISTERED);
        }
        int newBalance = balance + amount;
        put(id, newBalance);
        save();
        return new CardResponse(OPERATION_OK, newBalance);
    }
    
    public boolean isRegistered(String id) throws DatabaseException{
        reload();
        return get(id) != null;
    }

    public CardResponse registerCard(String id) throws DatabaseException {
        if (isRegistered(id)) {
            return new CardResponse(ID_ALREADY_USED);
        } else {
            put(id, 0);
        }
        save();
        return new CardResponse(OPERATION_OK);
    }
    
    public boolean hasMoney(String id, int amount) throws DatabaseException{
        Integer balance = readCardBalance(id);
        if (balance == null){
            return false;
        }
        return balance >= amount;
    }
     
    public static void main(String[] args) throws DatabaseException{
        CardDatabase cardDB = new CardDatabase();
        String id = "433F161E";
        cardDB.registerCard(id);
        cardDB.addMoney(id, 100000);
        //while(true){
//            String hour = CommonUtils.formattedDateNow();
//            System.out.println(hour + " " + cardDB.registerCard(hour).toString());
//            CommonUtils.sleep(1000);


        //}
    }
}
