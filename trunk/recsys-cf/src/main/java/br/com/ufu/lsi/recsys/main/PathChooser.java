package br.com.ufu.lsi.recsys.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.ufu.lsi.recsys.model.Position;
import br.com.ufu.lsi.recsys.model.Rating;


public class PathChooser {
    
    public static List<Position> choosePath( Rating[][] matrixU, Rating[][] matrixV ){
        List<Position> positions = new ArrayList<Position>();
        
        for( int i = 0; i<matrixU.length; i++ ){
            for( int j = 0; j<matrixU[i].length; j++ ){
                Position p = new Position( i, j, true );
                positions.add( p );
            }
        }
        
        for( int i = 0; i<matrixV.length; i++ ){
            for( int j = 0; j<matrixV[i].length; j++ ){
                Position p = new Position( i, j, false );
                positions.add( p );
            }
        }
        
        Collections.shuffle( positions );
        
        return positions;        
    }

}
