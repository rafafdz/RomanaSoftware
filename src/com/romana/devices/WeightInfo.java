/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.devices;

import com.romana.devices.SystemOperations.WeightType;
import static com.romana.devices.SystemOperations.WeightType.*;
import com.romana.utilities.CommonUtils;
import java.util.ArrayList;
import java.util.Date;

/**
 * Serializable class in order to store the weight process
 *
 * @author rafael
 */
public class WeightInfo {

    private WeightType type;
    private final Date date;
    private String plate;
    private final ArrayList<DatedWeight> weights = new ArrayList<>();
    private int weightResume; // Based on the type. Can be seen as a final weight.
    private int totalPrice;
    private String cardId;
    private Integer axisNumber;
    public String url; // To do: set to private !
    private transient Integer nextAxis = 0; // Transient so it doesnt get serialized!
    private transient boolean finished = false;

    public WeightInfo(WeightType type) {
        this.type = type;
        this.date = new Date();
    }

    public WeightType getType() {
        return type;
    }

    public void setType(WeightType type) {
        this.type = type;
    }

    public Date getCreationDate() {
        return date;
    }
    
    public int getFirstWeight(){
        return weights.get(0).weight;
    }
    
    public Date getFirstWeightDate(){
        return weights.get(0).date;
    }
    
    public int getSecondWeight(){
        return weights.get(1).weight;
    }
    
    public Date getSecondWeightDate(){
        return weights.get(1).date;
    }
    
    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public ArrayList<DatedWeight> getWeights() {
        return weights;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public int getSalePrice() {
        if (isSecondPhase()) {
            return 0;
        } else {
            return totalPrice;
        }
    }

    public void setTotalPrice(int price) {
        totalPrice = price;
    }

    public boolean isFinished() {
        return finished;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public Integer getAxisNumber() {
        return axisNumber;
    }

    public double getWeightResume() {
        return weightResume;
    }

    public void setAxisNumber(Integer axis) {
        axisNumber = axis;
    }

    public Integer getNextAxis() {
        return nextAxis;
    }

    public String getUrl() {
        return url;
    }

    public boolean isSecondPhase() {
        boolean secondCondition = (!finished && weights.size() == 1 || finished);
        return type == WeightType.TWO_PHASE && secondCondition;
    }
    
    public boolean readyForSecondPhase() {
        // TO DO: Is this necessary?
        boolean secondCondition = (!finished && weights.size() == 1);
        return type == WeightType.TWO_PHASE && secondCondition;
    }

    public void addWeight(int weight) {
        weights.add(new DatedWeight(weight, new Date()));
        if (type == AXIS) {
            nextAxis++;
        }
    }

    public int getNumberOfWeights() {
        return weights.size();
    }
    
    
    public int getWeightSum(){
        int sum = 0;
        for (DatedWeight weight : weights) {
            sum += weight.weight;
        }
        return sum;
    }
    
    public int getPriceToPay(){
        if (type == TWO_PHASE && getNumberOfWeights() == 1){
            return 0;
        }
        return getTotalPrice();
    }

    public void finishProcess() {
        switch (type) {
            case SIMPLE:
                weightResume = weights.get(0).weight;
                break;

            case TWO_PHASE:

                weightResume = Math.abs(weights.get(1).weight - weights.get(0).weight);
                break;

            case AXIS:
                int weightSum = 0;
                for (DatedWeight datedWeight : weights) {
                    weightSum += datedWeight.weight;
                }
                weightResume = weightSum;
                break;
        }

        // To do: Add price based on Project constants
        url = CommonUtils.webUrlFromDate(date);
        finished = true;
    }

    public class DatedWeight {

        public int weight;
        public Date date;

        public DatedWeight(int weight, Date date) {
            this.weight = weight;
            this.date = date;
        }
    }
}
