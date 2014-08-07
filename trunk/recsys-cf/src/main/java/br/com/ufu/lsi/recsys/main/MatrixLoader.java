package br.com.ufu.lsi.recsys.main;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import br.com.ufu.lsi.recsys.model.Movie;
import br.com.ufu.lsi.recsys.model.Rating;
import br.com.ufu.lsi.recsys.model.User;
import br.com.ufu.lsi.recsys.model.UtilityMatrix;


public class MatrixLoader {

    //private static final String PATH_RATES = "/home/fabiola/Desktop/Doutorado/DataMining/Projeto-Recomendacao/avaliacoes.csv";
	//private static final String PATH_RATES = "/Users/fabiola/Doutorado/Cricia/avaliacoesprefrecsocial.txt";


    public static Rating[][] loadMatrix( boolean booleanRates, String PATH_RATES ) {
       
        UtilityMatrix utilityMatrix = new UtilityMatrix();

        BufferedReader br = null;

        try {
            String sCurrentLine;

            br = new BufferedReader( new FileReader( PATH_RATES ) );

            while ( ( sCurrentLine = br.readLine() ) != null ) {
                handleLine( sCurrentLine, utilityMatrix, booleanRates );
            }
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        finally {
            try {
                if ( br != null )
                    br.close();
            }
            catch ( IOException ex ) {
                ex.printStackTrace();
            }
        }

        Rating matrixUM[][] = getMatrix( utilityMatrix );

        return matrixUM;

    }


    public static void handleLine( String currentLine, UtilityMatrix utilityMatrix, boolean booleanRates ) {

        HashMap< Long, User > users = utilityMatrix.getUsers();
        HashMap< Long, Movie > movies = utilityMatrix.getMovies();

        String tokens[] = currentLine.split( "\t" );
        Long userId = Long.parseLong( tokens[ 0 ] );
        Long movieId = Long.parseLong( tokens[ 1 ] );
        Double rating = Double.parseDouble( tokens[ 2 ] );

        if ( booleanRates ) {
            if ( rating >= 3.0 )
                rating = 1.0;
            else
                rating = 0.0;
        }

        User user = users.get( userId );
        if ( user == null ) {
            User u = new User( userId );
            users.put( userId, u );
            user = u;
        }

        Movie movie = movies.get( movieId );
        if ( movie == null ) {
            Movie m = new Movie( movieId );
            movies.put( movieId, m );
            movie = m;
        }

        user.getRatings().put( movieId, rating );
        movie.getRatings().put( userId, rating );

        utilityMatrix.getRatings().add( rating );
    }


    public static Rating[][] getMatrix( UtilityMatrix utilityMatrix ) {

        HashMap< Long, Movie > movies = utilityMatrix.getMovies();
        HashMap< Long, User > users = utilityMatrix.getUsers();

        System.out.println( "#users = " + users.size() );
        System.out.println( "#movies = " + movies.size() );

        Rating matrixUM[][] = new Rating[ users.size() ][ movies.size() ];
        int i = 0;        
        for ( Map.Entry< Long, User > userMap : users.entrySet() ) {
            Long userId = userMap.getKey();
            int j = 0;
            for ( Map.Entry< Long, Movie > movieMap : movies.entrySet() ) {
                Rating rating = new Rating();
                rating.setUser( userMap.getValue() );
                rating.setMovie( movieMap.getValue() );

                Movie movie = movieMap.getValue();
                HashMap< Long, Double > movieRatings = movie.getRatings();
                Double ratingValue = movieRatings.get( userId );
                rating.setValue( ratingValue );

                matrixUM[ i ][ j ] = rating;
                j++;
            }
            i++;
            utilityMatrix.getUsersSparsity().add( userMap.getValue() );
        }

        // set sparsity order
        Collections.sort( utilityMatrix.getUsersSparsity(), new Comparator< User >() {

            public int compare( User u1, User u2 ) {

                return u2.getSparsityDegree().compareTo( u1.getSparsityDegree() );
            }
        } );
        
        
        return matrixUM;
    }
    
    
    public static void printMatrix( Rating[][] matrixUM ){
        
        for( int i=0; i<matrixUM.length; i++ ){
            for( int j=0; j<matrixUM[i].length; j++ ){
                if( matrixUM[i][j].getValue() == null )
                    System.out.print( matrixUM[i][j].getValue() + "\t\t\t\t");
                else
                    System.out.format( "%.2f (%.2f + %.2f + %.2f)\t", matrixUM[i][j].getValue(), matrixUM[i][j].getNormalizedValue(), matrixUM[i][j].getUserAverage(), matrixUM[i][j].getMovieAverage() );
            }
            System.out.println();
        }
    }
    
    public static void setNormalizedValues( Rating[][] matrixUM ){
        
        /*for( int i = 0; i<matrixUM.length; i++ ){
            for( int j = 0; j<matrixUM[i].length; j++ ){
                if( matrixUM[i][j].getValue() != null ){
                    matrixUM[i][j].setNormalizedValue( matrixUM[i][j].getValue() );                    
                }
            }           
        }*/
        
        Double usersAverage[] = new Double[matrixUM.length];
        Double itensAverage[] = new Double[matrixUM[0].length];
        
        for( int i = 0; i<matrixUM.length; i++ ){
            Double total = 0.0;
            double totalItens = 0.0;
            for( int j = 0; j<matrixUM[i].length; j++ ){
                if( matrixUM[i][j].getValue() != null ){
                    total += matrixUM[i][j].getValue();
                    totalItens++;
                }
            }
            usersAverage[i] = total/totalItens;            
        }
        
        for( int i = 0; i<matrixUM.length; i++ ){
            for( int j = 0; j<matrixUM[i].length; j++ ){
                if( matrixUM[i][j].getValue() != null ){
                    Double normalizedValue = matrixUM[i][j].getValue() - usersAverage[i];
                    matrixUM[i][j].setNormalizedValue( normalizedValue );
                    matrixUM[i][j].setUserAverage( usersAverage[i] );
                } else {
                	matrixUM[i][j].setUserAverage( usersAverage[i] );
                }
            }           
        }
        
        for( int i = 0; i<matrixUM[0].length; i++ ){
            Double total = 0.0;
            Double totalUsers = 0.0;
            for( int j = 0; j<matrixUM.length; j++ ){
                if( matrixUM[j][i].getNormalizedValue() != null ){
                    total += matrixUM[j][i].getNormalizedValue();
                    totalUsers++;
                }
            }
            itensAverage[i] = total/totalUsers;            
        }
        
        for( int i = 0; i<matrixUM.length; i++ ){
            for( int j = 0; j<matrixUM[i].length; j++ ){
                if( matrixUM[i][j].getValue() != null ){
                    Double normalizedValue = matrixUM[i][j].getNormalizedValue() - itensAverage[j];
                    matrixUM[i][j].setNormalizedValue( normalizedValue );
                    matrixUM[i][j].setMovieAverage( itensAverage[j] );
                } else {
                	matrixUM[i][j].setMovieAverage( itensAverage[j] );
                }
            }           
        }
        
    }

}
