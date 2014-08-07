package br.com.ufu.lsi.recsys.util;

import java.util.List;

import br.com.ufu.lsi.recsys.base.User;

public class Printer {
    
    public static void printHelp(){
        System.out.println( "\n===== Recommendation System - Matrix Factorization Colaborative Filtering ===== ");
        System.out.println( "-T threshold" );
        System.out.println( "-F latent factors" );
        System.out.println( "-O overfitting rounds" );
        System.out.println( "-R path to ratings file" );
        System.out.println( "-P path to output predicted ratings file" );
        System.out.println( "-B if is boolean matrix" );
        System.out.println();
    }
    
    public static void printValues( int LATENT_FACTOR, Double THRESHOLD, int OVERFITTING_ROUNDS, String PATH_RATES, String PATH_PREDICTED_RATES ){
        System.out.println( "\n========================== PARAMETERS ================================");
        System.out.println( "# Latent factors = " + LATENT_FACTOR );
        System.out.println( "# Overfitting rounds = " + OVERFITTING_ROUNDS );
        System.out.println( "# Threshold = " + THRESHOLD );
        System.out.println( "# Path rates: " + PATH_RATES );
        System.out.println( "# Path predicted rates: " + PATH_PREDICTED_RATES );
        System.out.println( "======================================================================\n");
    }
    
    public static void printValues( String PATH_RATES, String RATINGS_TEST_PATH, String RATINGS_TRAINING_PATH, String USERS_SERIALIZED_FILE ){
        System.out.println( "\n========================== PARAMETERS ================================");
        System.out.println( "# Path rates: " + PATH_RATES );
        System.out.println( "# Path testbase file: " + RATINGS_TEST_PATH );
        System.out.println( "# Path trainingbase file: " + RATINGS_TRAINING_PATH );
        System.out.println( "# Path users tmp file: " + USERS_SERIALIZED_FILE );
        System.out.println( "======================================================================\n");
    }
    
    public static void printSummary( List< User > users, Double accuracy, Long end ){
        System.out.println( "\n======== Summary =========" );
        /*System.out.println( "User\tAccuracy" );
        for ( User user : users ) {
            if ( user.isSelected() )
                System.out.println( user.getId() + "\t" + user.getAccuracy() );
        }*/
        System.out.println( "Accuracy = " + accuracy );
        System.out.println( "Time elapsed = " + end + " (sec)" );
        System.out.println( "============================\n");
    }

}
