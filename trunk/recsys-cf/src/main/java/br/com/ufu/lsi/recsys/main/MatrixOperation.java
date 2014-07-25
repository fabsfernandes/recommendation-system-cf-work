package br.com.ufu.lsi.recsys.main;

import java.util.Random;

import br.com.ufu.lsi.recsys.model.Rating;


public class MatrixOperation {
    
    public static Rating[][] initializeMatrixU( Rating matrixUM[][], int latentFactor ){
        
        Rating matrixU[][] = new Rating[matrixUM.length][latentFactor];
        fillMatrix( matrixU, matrixUM );
        return matrixU;
    }
    
    
    public static Rating[][] initializeMatrixV( Rating matrixUM[][], int latentFactor ){
        
        Rating matrixV[][] = new Rating[latentFactor][matrixUM[0].length];
        fillMatrix( matrixV, matrixUM );
        return matrixV;
    }
    
    private static void fillMatrix( Rating matrix[][], Rating matrixUM[][] ){
        
        Double totalItens = 0.0;
        Double totalValue = 0.0;
        for( int i = 0; i<matrixUM.length; i++ ){
            for( int j = 0; j<matrixUM[i].length; j++ ){
                if( matrixUM[i][j].getNormalizedValue() != null ){
                    totalItens++;
                    totalValue += matrixUM[i][j].getNormalizedValue();
                }
            }
        }
        Double ratingAverage = totalValue / totalItens;
        
        for( int i = 0; i<matrix.length; i++ ){
            for( int j = 0; j<matrix[i].length; j++ ){
                matrix[i][j] = new Rating();
                Double x = generateRandomPositiveNegativeValue();
                matrix[i][j].setNormalizedValue( ratingAverage + x );
            }
        }
    }
    
    public static void printMatrix( Rating[][] matrix ){
        printMatrix( matrix, false );
    }
    
    public static void printMatrix( Rating[][] matrix, boolean realValue ){
        for( int i = 0; i<matrix.length; i++ ){
            for( int j=0; j<matrix[i].length; j++ ){
                System.out.format( "%.2f\t", realValue ? matrix[i][j].getValue() : matrix[i][j].getNormalizedValue() );
            }
            System.out.println();
        }
    }
    
    public static Rating[][] dotProduct( Rating[][] matrixU, Rating[][] matrixV ){
        
        Rating matrixUV[][] = new Rating[matrixU.length][matrixV[0].length];
        
        for (int i = 0; i < matrixU.length; i++) {
           for (int j = 0; j < matrixV[0].length; j++)  {
              matrixUV[i][j] = new Rating();
              for (int k = 0; k < matrixV.length; k++)  {
                  Double normalizedValue = matrixU[i][k].getNormalizedValue() * matrixV[k][j].getNormalizedValue();
                  Double newValue = matrixUV[i][j].getNormalizedValue() != null ? (matrixUV[i][j].getNormalizedValue() + normalizedValue) : normalizedValue;
                  matrixUV[i][j].setNormalizedValue( newValue );
              }
           }
        }
        /*
        
        double[][] c = new double[N][N];
        for (int i = 0; i < N; i++) {
           for (int j = 0; j < N; j++)  {
              for (int k = 0; k < N; k++)  {
                 c[i][j] += a[i][k]*b[k][j];
              }
           }
        }*/
        
        return matrixUV;
    }
    
    public static Double generateRandomPositiveNegativeValue() {
        Random random = new Random();
        Double number = random.nextDouble() * 2 - 1;
        return number;
    }
        
}
