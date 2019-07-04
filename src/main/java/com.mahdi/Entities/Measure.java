/***********************************************************************************************************************
 Copyright (c) Damak Mahdi.
 Github.com/damakmahdi
 damakmahdi2012@gmail.com
 linkedin.com/in/mahdi-damak-400a3b14a/
 **********************************************************************************************************************/

package com.mahdi.Entities;

/*
Implementation of a Measure Class that has Timestamp and Value(of measure) as characteristics
 */

public class Measure {
    public Long TIMESTAMP;
    public double VALUE;


    public Long getTIMESTAMP() {
        return TIMESTAMP;
    }

    public void setTIMESTAMP(Long TIMESTAMP) {
        this.TIMESTAMP = TIMESTAMP;
    }

    public double getVALUE() {
        return VALUE;
    }

    public void setVALUE(double VALUE) {
        this.VALUE = VALUE;
    }

    @Override
    public String toString() {
        return "Measure{" +
                "TIMESTAMP=" + TIMESTAMP +
                ", VALUE=" + VALUE +

                '}';
    }

}
