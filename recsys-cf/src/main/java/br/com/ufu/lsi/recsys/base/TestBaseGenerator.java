package br.com.ufu.lsi.recsys.base;


import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;

import br.com.ufu.lsi.recsys.main.MatrixLoader;
import br.com.ufu.lsi.recsys.model.Rating;
import br.com.ufu.lsi.recsys.util.Printer;


public class TestBaseGenerator {

    private static final Double PERCENTAGE = 0.1;

    private static final Double ITEM_PERCENTAGE = 0.1;

    private static final int LEVEL_NUMBER = 4;
    
    private static String RATINGS_TEST_PATH;

    private static String RATINGS_TRAINING_PATH;
    
    private static String USERS_SERIALIZED_FILE;
    
    private static Boolean IS_BOOLEAN = false;
    
    private static String PATH_RATES;

    private static Rating matrixUM[][];

    private static Rating newMatrixUM[][];

    private static List< User > users;

    private static List< Level > levels;

    
    private static boolean handleParams( String... args ) {

        for ( String param : args ) {

            String paramValue = param.substring( 2 );

            switch ( param.charAt( 1 ) ) {
                case 'T':
                case 't':
                    paramValue = !paramValue.endsWith( "/" ) ? paramValue.concat( "/" ) : paramValue;                    
                    RATINGS_TEST_PATH = paramValue + "test/";
                    new File( RATINGS_TEST_PATH ).mkdirs();
                    RATINGS_TEST_PATH = RATINGS_TEST_PATH.concat( "ratings_testbase.txt" );
                    RATINGS_TRAINING_PATH = paramValue + "training/";
                    new File( RATINGS_TRAINING_PATH ).mkdirs();
                    RATINGS_TRAINING_PATH = RATINGS_TRAINING_PATH.concat( "ratings_trainingbase.txt" );
                    USERS_SERIALIZED_FILE = paramValue + "serializedInputs.txt";
                    break;
                case 'R':
                case 'r':
                    PATH_RATES = paramValue;
                    break;
                case 'B':
                case 'b':
                    IS_BOOLEAN = true;
                    break;
                default:
                    return false;
            }
        }

        if ( PATH_RATES == null || RATINGS_TEST_PATH == null || RATINGS_TRAINING_PATH == null || USERS_SERIALIZED_FILE == null ) {
            return false;
        }

        return true;
    }

    public static void main( String... args ) throws Exception {
        
        // setup params
        if ( ! handleParams( args ) ) {
            Printer.printHelp();
            return;
        }
        Printer.printValues( PATH_RATES, RATINGS_TEST_PATH, RATINGS_TRAINING_PATH, USERS_SERIALIZED_FILE );
        
        
        // load input
        matrixUM = MatrixLoader.loadMatrix( IS_BOOLEAN, PATH_RATES );
        users = new ArrayList< User >();
        generate( matrixUM, users );  
    }
    
    public static Rating[][] generateFromFile( Rating[][] matrixUM, List<User> users ) throws Exception {

        TestBaseGenerator.matrixUM = matrixUM;
        TestBaseGenerator.users = users;
        
        loadTestBase();
        buildNewMatrixUM();
        writeRatingFiles();
        
        return newMatrixUM;
    }
    
    public static Rating[][] generate( Rating[][] matrixUM, List<User> users ) throws FileNotFoundException, UnsupportedEncodingException {

        TestBaseGenerator.matrixUM = matrixUM;
        TestBaseGenerator.users = users;
        
        buildUserTestBase();
        buildItemTestBase();
        serializeTestBase();
        printRatings();
        buildNewMatrixUM();
        writeRatingFiles();
        
        return newMatrixUM;
    }
    
    public static void buildNewMatrixUM(){
        
        newMatrixUM = SerializationUtils.clone( matrixUM );
        
        for( User user : users ){
            if( user.isSelected() ){
                for ( Map.Entry< Item, Double > entry : user.getItems().entrySet() ) {
                    Item item = entry.getKey();
                    if( item.isSelected() ){
                        
                        for( int i = 0; i<newMatrixUM.length; i++ ){
                            for( int j = 0; j<newMatrixUM[0].length; j++ ){
                                Rating rating = newMatrixUM[i][j];
                                if( rating.getUser().getId().equals( user.getId() ) ){
                                    if( rating.getMovie().getId().equals( item.getId() ) ){
                                        newMatrixUM[i][j].setValue( null );
                                        break;
                                    }
                                }
                            }
                        }                        
                    }                    
                }
            }
        }
        
        //MatrixOperation.printMatrix( newMatrixUM, true );
        //MatrixOperation.printMatrix( matrixUM, true );
    }

    public static void writeRatingFiles() throws FileNotFoundException, UnsupportedEncodingException{
        
        PrintWriter testWriter = new PrintWriter( RATINGS_TEST_PATH, "UTF-8");
        PrintWriter trainingWriter = new PrintWriter( RATINGS_TRAINING_PATH, "UTF-8");
        
        for( User user : users ){
            for ( Map.Entry< Item, Double > entry : user.getItems().entrySet() ) {
                Item item = entry.getKey();
                Double rating = entry.getValue();
                                
                if( user.isSelected() ){
                    if( item.isSelected() )
                        testWriter.print( user.getId() + "\t" + item.getId() + "\t" + rating + "\n" );
                } else {
                    trainingWriter.print( user.getId() + "\t" + item.getId() + "\t" + rating + "\n" );
                }
            }
        }        
        
        testWriter.close();
        trainingWriter.close();
    }
    
    /**
     * Build users test base
     */
    public static void buildUserTestBase() {

        Double minAverage = 10000.0;
        Double maxAverage = 0.0;

        // load users
        //users = new ArrayList< User >();
        for ( int i = 0; i < matrixUM.length; i++ ) {

            User user = new User();
            Double totalRating = 0.0;
            Double totalItens = 0.0;
            for ( int j = 0; j < matrixUM[ i ].length; j++ ) {

                user.setId( matrixUM[ i ][ j ].getUser().getId() );

                Long itemId = matrixUM[ i ][ j ].getMovie().getId();
                Double rating = matrixUM[ i ][ j ].getValue();

                if ( rating != null ) {
                    Item item = new Item( itemId );
                    user.getItems().put( item, rating );
                    totalRating += rating;
                    totalItens++;
                }
            }
            Double averageRating = totalRating / totalItens;
            user.setAverageRating( averageRating );
            users.add( user );

            if ( averageRating < minAverage ) {
                minAverage = averageRating;
            }
            if ( averageRating > maxAverage ) {
                maxAverage = averageRating;
            }
        }

        // load user levels
        Double interval = ( maxAverage - minAverage ) / LEVEL_NUMBER;
        levels = new ArrayList< Level >();
        for ( int i = 0; i < LEVEL_NUMBER; i++ ) {
            Level level = new Level();
            level.setMinAverage( minAverage );
            if ( i == LEVEL_NUMBER - 1 )
                level.setMaxAverage( maxAverage );
            else
                level.setMaxAverage( minAverage + interval );
            minAverage = ( minAverage + interval );
            levels.add( level );
        }

        for ( int i = 0; i < levels.size(); i++ ) {
            Level level = levels.get( i );
            double usersInLevel = 0;
            for ( User user : users ) {
                if ( isInLevel( user, level, i ) )
                    usersInLevel++;
            }
            level.setPercent( usersInLevel / users.size() );
        }

        // select users
        int totalUsers = (int) Math.ceil( PERCENTAGE * users.size() );
        int accumulativeUsers = totalUsers;
        for ( int i = 0; i < levels.size(); i++ ) {
            Level level = levels.get( i );

            int totalLevelUsers = (int) Math.ceil( level.getPercent() * totalUsers );
            int total = 0;
            for ( User user : users ) {
                if ( total >= totalLevelUsers || accumulativeUsers == 0 )
                    break;
                if ( !user.isSelected() && isInLevel( user, level, i ) ) {
                    user.setSelected( true );
                    total++;
                    accumulativeUsers--;
                }
            }
        }
    }


    /**
     * Build items test base
     */
    public static void buildItemTestBase() {

        HashMap< Double, Integer > itensLevels = new HashMap< Double, Integer >();

        for ( User user : users ) {
            if ( user.isSelected() ) {
                for ( Map.Entry< Item, Double > entry : user.getItems().entrySet() ) {
                    Double rating = entry.getValue();

                    if ( itensLevels.get( rating ) == null ) {
                        itensLevels.put( rating, 1 );
                    }
                    else {
                        int total = itensLevels.get( rating );
                        itensLevels.put( rating, total+1 );
                    }
                }

                int totalItensToBeSelected = (int) Math.ceil( ITEM_PERCENTAGE * user.getItems().size() );

                for ( Map.Entry< Double, Integer > entry : itensLevels.entrySet() ) {
                    Double rating = entry.getKey();
                    Integer quantity = entry.getValue();
                    int totalItensToBeSelectedInThisLevel = (int) Math.ceil( (quantity.doubleValue() / user.getItems().size() )*totalItensToBeSelected);
                    int total = 0;
                    for ( Map.Entry< Item, Double > entryItem : user.getItems().entrySet() ) {
                        Item item = entryItem.getKey();
                        Double ratingItem = entryItem.getValue();
                        if ( total >= totalItensToBeSelectedInThisLevel )
                            break;
                        if ( !item.isSelected() && ratingItem.equals( rating ) ) {
                            item.setSelected( true );
                            total++;
                        }
                    }
                }

                itensLevels.clear();
            }
        }

    }


    public static void printRatings() {

        int total = 0;
        for ( User user : users ) {
            if ( user.isSelected() ){
                total++;
                System.out.println(user);
            }                
        }
        System.out.println( "#total users = " + users.size() );
        System.out.println( "#total selected = " + total );

    }
    
    public static void serializeTestBase() {
        
        try {
            File f = new File( USERS_SERIALIZED_FILE );
    
            FileOutputStream out= new FileOutputStream( f );
    
            ObjectOutputStream stream = new ObjectOutputStream( out );
            
            for( User user : users )
                stream.writeObject( user );    
            
            stream.close();
            out.close();

        } catch( Exception e ){  
            
            e.printStackTrace();
        }
       
        
    }
    
    public static void loadTestBase() throws Exception {
        
        FileInputStream in = null;
        ObjectInputStream stream = null;
        
        try {
            File f = new File( USERS_SERIALIZED_FILE );
    
            in = new FileInputStream( f );
    
            stream = new ObjectInputStream( in );
            
            while (true){
                User user = (User) stream.readObject(); 
                users.add( user ); 
            }            
            
        } catch( EOFException e ){
            stream.close();
            in.close();
        }
        catch( Exception e ){
            e.printStackTrace();
        }        
    }


    private static boolean isInLevel( User user, Level level, int levelNumber ) {

        if ( levelNumber == 0 ) {
            if ( level.getMinAverage() <= user.getAverageRating() && user.getAverageRating() <= level.getMaxAverage() ) {
                return true;
            }
        }
        else {
            if ( level.getMinAverage() < user.getAverageRating() && user.getAverageRating() <= level.getMaxAverage() ) {
                return true;
            }
        }
        return false;
    }

}
