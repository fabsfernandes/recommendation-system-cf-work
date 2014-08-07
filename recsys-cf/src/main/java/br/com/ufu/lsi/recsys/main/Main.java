
package br.com.ufu.lsi.recsys.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;

import br.com.ufu.lsi.recsys.base.Item;
import br.com.ufu.lsi.recsys.base.TestBaseGenerator;
import br.com.ufu.lsi.recsys.base.User;
import br.com.ufu.lsi.recsys.model.Position;
import br.com.ufu.lsi.recsys.model.Rating;
import br.com.ufu.lsi.recsys.util.Printer;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Main {

    static final Logger logger = LogManager.getLogger( Main.class.getName() );

    private static int LATENT_FACTOR = 10;

    private static Double THRESHOLD = 0.9099;

    private static int OVERFITTING_ROUNDS = 1;

    private static String PATH_RATES;

    private static String PATH_PREDICTED_RATES;

    private static Boolean IS_BOOLEAN = false;

    static Rating matrixUM[][];

    static Rating matrixU[][];

    static Rating matrixV[][];

    static Rating matrixUV[][];

    static Rating matrixUMOriginal[][];

    static List< User > users = new ArrayList< User >();

    static List< Rating[][] > resultingUVmatrices = new ArrayList< Rating[][] >();

    
    public static void main( String... args ) throws Exception {

        // setup params
        if ( ! handleParams( args ) ) {
            Printer.printHelp();
            return;
        }
        Printer.printValues( LATENT_FACTOR, THRESHOLD, OVERFITTING_ROUNDS, PATH_RATES,
                PATH_PREDICTED_RATES );

        // load rates
        matrixUMOriginal = MatrixLoader.loadMatrix( IS_BOOLEAN, PATH_RATES );
        matrixUM = TestBaseGenerator.generateFromFile( matrixUMOriginal, users );

        // run algorithm
        decompositionWithoutOverfitting();

        // output predicted ratings
        generateRecommendationRatings();
    }

    public static void decompositionWithoutOverfitting() {

        Long init = System.currentTimeMillis();

        // pre-process UM
        MatrixLoader.setNormalizedValues( matrixUM );
        MatrixLoader.setNormalizedValues( matrixUMOriginal );

        for ( int i = 0; i < OVERFITTING_ROUNDS; i++ ) {
            decompositionUV();
            resultingUVmatrices.add( SerializationUtils.clone( matrixUV ) );
            clearDataStructures();
        }

        Double accuracy = calculateFinalAccuracy();
        Long end = ( System.currentTimeMillis() - init ) / 1000;

        // print summary
        Printer.printSummary( users, accuracy, end );

    }

    public static void decompositionUV() {

        // initialize U, V and UV
        matrixU = MatrixOperation.initializeMatrixU( matrixUM, LATENT_FACTOR );
        matrixV = MatrixOperation.initializeMatrixV( matrixUM, LATENT_FACTOR );
        matrixUV = MatrixOperation.dotProduct( matrixU, matrixV );

        // choose path for U and V
        List< Position > positions = PathChooser.choosePath( matrixU, matrixV );

        System.out.println( "#It\tRMSE\t\t\tAccuracy\t\tTime elapsed(sec)" );

        Double rmse = THRESHOLD + 1.0;
        for ( int i = 1; rmse > THRESHOLD; i++ ) {

            Long init = System.currentTimeMillis();

            // walk through chosen path optimizing all k elements
            rmse = optimizeElements( positions );

            // calculate accuracy
            Double accuracy = calculateAccuracy();

            // print results
            Long end = ( System.currentTimeMillis() - init ) / 1000;
            System.out.println( i + "\t" + rmse + "\t" + accuracy + "\t" + end );
        }
    }

    public static Double calculateFinalAccuracy() {

        for ( User user : users ) {
            if ( user.isSelected() ) {
                Double total = 0.0;
                Double correct = 0.0;

                for ( Map.Entry< Item, Double > entry : user.getItems().entrySet() ) {
                    Item item = entry.getKey();
                    if ( item.isSelected() ) {

                        for ( int i = 0; i < matrixUMOriginal.length; i++ ) {
                            for ( int j = 0; j < matrixUMOriginal[ 0 ].length; j++ ) {
                                Rating rating = matrixUMOriginal[ i ][ j ];
                                if ( rating.getUser().getId().equals( user.getId() ) ) {
                                    if ( rating.getMovie().getId().equals( item.getId() ) ) {
                                        if ( matrixUMOriginal[ i ][ j ].getValue() != null ) {
                                            total++ ;

                                            Double predictedRate = 0.0;
                                            for ( Rating[][] tempMatrixUV : resultingUVmatrices ) {
                                                predictedRate += tempMatrixUV[ i ][ j ]
                                                        .getNormalizedValue()
                                                        + matrixUMOriginal[ i ][ j ]
                                                                .getMovieAverage()
                                                        + matrixUMOriginal[ i ][ j ]
                                                                .getUserAverage();
                                            }
                                            predictedRate = ( predictedRate / resultingUVmatrices
                                                    .size() );

                                            Double roundPredictedRate = roundRate( predictedRate );

                                            if ( roundPredictedRate
                                                    .equals( matrixUMOriginal[ i ][ j ].getValue() ) ) {
                                                correct++ ;
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
                user.setAccuracy( correct / total );
            }
        }

        Double sum = 0.0;
        Double totalSelectedUsers = 0.0;
        for ( User user : users ) {
            if ( user.isSelected() ) {
                sum += user.getAccuracy();
                totalSelectedUsers++ ;
            }
        }
        return sum / totalSelectedUsers;

    }

    public static Double calculateAccuracy() {

        for ( User user : users ) {
            if ( user.isSelected() ) {
                Double total = 0.0;
                Double correct = 0.0;

                for ( Map.Entry< Item, Double > entry : user.getItems().entrySet() ) {
                    Item item = entry.getKey();
                    if ( item.isSelected() ) {

                        for ( int i = 0; i < matrixUMOriginal.length; i++ ) {
                            for ( int j = 0; j < matrixUMOriginal[ 0 ].length; j++ ) {
                                Rating rating = matrixUMOriginal[ i ][ j ];
                                if ( rating.getUser().getId().equals( user.getId() ) ) {
                                    if ( rating.getMovie().getId().equals( item.getId() ) ) {
                                        if ( matrixUMOriginal[ i ][ j ].getValue() != null ) {
                                            total++ ;
                                            Double predictedRate = matrixUV[ i ][ j ]
                                                    .getNormalizedValue()
                                                    + matrixUMOriginal[ i ][ j ].getMovieAverage()
                                                    + matrixUMOriginal[ i ][ j ].getUserAverage();

                                            Double roundPredictedRate = roundRate( predictedRate );

                                            if ( roundPredictedRate
                                                    .equals( matrixUMOriginal[ i ][ j ].getValue() ) ) {
                                                correct++ ;
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
                user.setAccuracy( correct / total );
            }
        }

        Double sum = 0.0;
        Double totalSelectedUsers = 0.0;
        for ( User user : users ) {
            if ( user.isSelected() ) {
                sum += user.getAccuracy();
                totalSelectedUsers++ ;
            }
        }
        return sum / totalSelectedUsers;
    }

    public static Double optimizeElements( List< Position > positions ) {

        Double currentRmse = calculateRMSE();

        for ( Position position : positions ) {

            if ( position.isMatrixU() ) {
                int r = position.getI();
                int s = position.getJ();

                Double externalSum = 0.0;
                Double denominator = 0.0;
                for ( int j = 0; j < matrixUM[ 0 ].length; j++ ) {
                    if ( matrixUM[ r ][ j ].getNormalizedValue() != null ) {
                        Double internalSum = 0.0;
                        for ( int k = 0; k < matrixU[ 0 ].length; k++ ) {
                            if ( k != s ) {
                                internalSum += ( matrixU[ r ][ k ].getNormalizedValue() * matrixV[ k ][ j ]
                                        .getNormalizedValue() );
                            }
                        }

                        externalSum += ( matrixV[ s ][ j ].getNormalizedValue() * ( matrixUM[ r ][ j ]
                                .getNormalizedValue() - internalSum ) );
                    }

                    denominator += ( Math.pow( matrixV[ s ][ j ].getNormalizedValue(), 2 ) );
                }

                matrixU[ r ][ s ].setNormalizedValue( externalSum / denominator );
                Double newRmse = calculateRMSE( externalSum / denominator, position );

                currentRmse = newRmse;
                /*
                 * if ( newRmse < currentRmse ) { currentRmse = newRmse; }
                 */
            }

            else {
                int r = position.getI();
                int s = position.getJ();

                Double externalSum = 0.0;
                Double denominator = 0.0;
                for ( int i = 0; i < matrixUM.length; i++ ) {
                    if ( matrixUM[ i ][ s ].getNormalizedValue() != null ) {
                        Double internalSum = 0.0;
                        for ( int k = 0; k < matrixV.length; k++ ) {
                            if ( k != r ) {
                                internalSum += ( matrixU[ i ][ k ].getNormalizedValue() * matrixV[ k ][ s ]
                                        .getNormalizedValue() );
                            }
                        }

                        externalSum += ( matrixU[ i ][ r ].getNormalizedValue() * ( matrixUM[ i ][ s ]
                                .getNormalizedValue() - internalSum ) );
                    }

                    denominator += ( Math.pow( matrixU[ i ][ r ].getNormalizedValue(), 2 ) );
                }

                matrixV[ r ][ s ].setNormalizedValue( externalSum / denominator );
                Double newRmse = calculateRMSE( externalSum / denominator, position );

                currentRmse = newRmse;
                /*
                 * if ( newRmse < currentRmse ) { currentRmse = newRmse; }
                 */
            }
        }
        return currentRmse;
    }

    public static Double calculateRMSE( Double newValue, Position position ) {

        Double oldValue;
        if ( position.isMatrixU() ) {
            oldValue = matrixU[ position.getI() ][ position.getJ() ].getNormalizedValue();
            matrixU[ position.getI() ][ position.getJ() ].setNormalizedValue( newValue );
        } else {
            oldValue = matrixV[ position.getI() ][ position.getJ() ].getNormalizedValue();
            matrixV[ position.getI() ][ position.getJ() ].setNormalizedValue( newValue );
        }
        Double rmse = calculateRMSE();

        if ( position.isMatrixU() ) {
            matrixU[ position.getI() ][ position.getJ() ].setNormalizedValue( oldValue );
        } else {
            matrixV[ position.getI() ][ position.getJ() ].setNormalizedValue( oldValue );
        }

        return rmse;
    }

    public static Double calculateRMSE() {

        matrixUV = MatrixOperation.dotProduct( matrixU, matrixV );

        Double sumDifference = 0.0;
        Double totalItens = 0.0;
        for ( int i = 0; i < matrixUM.length; i++ ) {
            for ( int j = 0; j < matrixUM[ i ].length; j++ ) {
                if ( matrixUM[ i ][ j ].getNormalizedValue() != null ) {

                    sumDifference += ( Math.pow( matrixUM[ i ][ j ].getNormalizedValue()
                            - matrixUV[ i ][ j ].getNormalizedValue(), 2 ) );
                    totalItens++ ;
                }
            }
        }

        Double rmse = Math.sqrt( sumDifference / totalItens );

        return rmse;
    }

    public static Double roundRate( Double rate ) {

        return Math.round( rate / 1.0 ) * 1.0;
    }

    public static void clearDataStructures() {
        for ( int i = 0; i < matrixUV.length; i++ ) {
            for ( int j = 0; j < matrixUV[ i ].length; j++ )
                matrixUV[ i ][ j ] = null;
        }
        for ( int i = 0; i < matrixU.length; i++ ) {
            for ( int j = 0; j < matrixU[ i ].length; j++ )
                matrixU[ i ][ j ] = null;
        }
        for ( int i = 0; i < matrixV.length; i++ ) {
            for ( int j = 0; j < matrixV[ i ].length; j++ )
                matrixV[ i ][ j ] = null;
        }
    }

    public static void generateRecommendationRatings() {

        File file = null;
        PrintWriter printWriter = null;

        try {
            file = new File( PATH_PREDICTED_RATES );
            printWriter = new PrintWriter( file );

            for ( int i = 0; i < matrixUMOriginal.length; i++ ) {
                for ( int j = 0; j < matrixUMOriginal[ 0 ].length; j++ ) {
                    Rating rating = matrixUMOriginal[ i ][ j ];
                    if ( matrixUMOriginal[ i ][ j ].getValue() == null ) {

                        Double predictedRate = 0.0;
                        for ( Rating[][] tempMatrixUV : resultingUVmatrices ) {
                            predictedRate += tempMatrixUV[ i ][ j ].getNormalizedValue()
                                    + matrixUMOriginal[ i ][ j ].getMovieAverage()
                                    + matrixUMOriginal[ i ][ j ].getUserAverage();
                        }
                        predictedRate = ( predictedRate / resultingUVmatrices.size() );

                        Double roundPredictedRate = roundRate( predictedRate );

                        printWriter.println( rating.getUser().getId() + "\t"
                                + rating.getMovie().getId() + "\t" + roundPredictedRate );
                    }

                }
            }

        } catch ( FileNotFoundException e ) {
            logger.error( e.getMessage() );
        } finally {
            printWriter.close();
        }

    }
    
    public static boolean handleParams( String... args ) {

        for ( String param : args ) {

            String paramValue = param.substring( 2 );

            switch ( param.charAt( 1 ) ) {
                case 'T':
                case 't':
                    THRESHOLD = Double.parseDouble( paramValue );
                    break;
                case 'F':
                case 'f':
                    LATENT_FACTOR = Integer.parseInt( paramValue );
                    break;
                case 'O':
                case 'o':
                    OVERFITTING_ROUNDS = Integer.parseInt( paramValue );
                    break;
                case 'R':
                case 'r':
                    PATH_RATES = paramValue;
                    break;
                case 'P':
                case 'p':
                    PATH_PREDICTED_RATES = paramValue;
                    break;
                case 'B':
                case 'b':
                    IS_BOOLEAN = true;
                    break;
                default:
                    return false;
            }
        }

        if ( PATH_RATES == null || PATH_PREDICTED_RATES == null ) {
            return false;
        }

        return true;
    }

}
