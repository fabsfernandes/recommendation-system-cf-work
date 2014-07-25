package br.com.ufu.lsi.recsys.model;

import java.io.Serializable;


public class Position implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    int i;
    
    int j;
    
    boolean matrixU;
    
    public Position( int i, int j, boolean matrixU ){
        this.i = i;
        this.j = j;
        this.matrixU = matrixU;
    }

    
    public int getI() {
    
        return i;
    }

    
    public void setI( int i ) {
    
        this.i = i;
    }

    
    public int getJ() {
    
        return j;
    }

    
    public void setJ( int j ) {
    
        this.j = j;
    }

    
    public boolean isMatrixU() {
    
        return matrixU;
    }

    
    public void setMatrixU( boolean matrixU ) {
    
        this.matrixU = matrixU;
    }

}
