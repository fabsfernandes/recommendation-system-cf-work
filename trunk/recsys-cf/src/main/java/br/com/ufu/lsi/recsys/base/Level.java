package br.com.ufu.lsi.recsys.base;

import java.io.Serializable;


public class Level implements Serializable {
    
    private static final long serialVersionUID = 1L;

    Double minAverage;
    
    Double maxAverage;
    
    Double percent;

    
    public Double getMinAverage() {
    
        return minAverage;
    }

    
    public void setMinAverage( Double minAverage ) {
    
        this.minAverage = minAverage;
    }

    
    public Double getMaxAverage() {
    
        return maxAverage;
    }

    
    public void setMaxAverage( Double maxAverage ) {
    
        this.maxAverage = maxAverage;
    }

    
    public Double getPercent() {
    
        return percent;
    }

    
    public void setPercent( Double percent ) {
    
        this.percent = percent;
    }


    @Override
    public String toString() {

        return "Level [minAverage=" + minAverage + ", maxAverage=" + maxAverage + ", percent=" + percent + "]";
    }

}
