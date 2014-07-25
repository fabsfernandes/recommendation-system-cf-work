package br.com.ufu.lsi.test;

import java.util.Random;

import org.junit.Test;


public class UtilsTest {
    
    //@Test
    public void roundRate(){
        Double rate = 3.74;
        Double roundedRate = Math.round(rate / 0.5) * 0.5;
        
        System.out.println(roundedRate);
    }
    
    @Test
    public void generateRandomPositiveNegativeValue() {
        Double max = 1.0;
        Double min = -1.0;
        for( int i = 0; i<100; i++ ){
        Random random = new Random();
        Double ii = random.nextDouble() * 2 - 1;
        System.out.println( ii );}
    }

}
