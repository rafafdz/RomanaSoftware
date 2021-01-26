/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.romana.devices.scale;

import com.romana.devices.WeightInfo;

/**
 *
 * @author rafael
 */
public class ScaleResponse {
    
    public ScaleCode code;
    public WeightInfo info;
    
    public ScaleResponse(ScaleCode code){
        this.code = code;
    }
    
    public ScaleResponse(ScaleCode code, WeightInfo info){
        this.code = code;
        this.info = info;
    }
    
    public enum ScaleCode {
        OPERATION_OK,
        PLATE_PROCESS_ONGOING,
        NO_FIRST_WEIGHT_FOUND,
        WEIGHT_TOO_LOW,
        NOT_ENOUGH_DIFFERENCE
    }
    
    @Override
    public String toString(){
        return code.toString();
    }
}
