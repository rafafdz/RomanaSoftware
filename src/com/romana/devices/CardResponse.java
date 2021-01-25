/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.devices;

/**
 *
 * @author rafael
 */
public class CardResponse {

    public CardCode code;
    public int moneyLeft;
    public String cardUsed;

    public CardResponse(CardCode newCode) {
        code = newCode;
    }

    public CardResponse(CardCode newCode, int moneyLeft) {
        code = newCode;
        this.moneyLeft = moneyLeft;
    }

    public enum CardCode {
        NO_CARD_ENTERED,
        NO_MONEY,
        OPERATION_OK,
        ID_ALREADY_USED,
        CARD_NOT_REGISTERED,
        BAD,
        PROCESS_NOT_FOUND,
        SECOND_STAGE,
        FIRST_STAGE,
        OTHER_PROCESS_FOUND,
        REAL_DISCONNECTION
    }

}
