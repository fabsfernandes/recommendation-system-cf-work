package br.com.ufu.lsi.recsys.model;

import java.io.Serializable;
import java.util.HashMap;


public class User implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    Long id;
    
    HashMap<Long,Double> ratings = new HashMap<Long,Double>();
    
    Double sparsityDegree;
    
    
    public User( Long id ){
        
        this.id = id;
    }

    
    public Long getId() {
    
        return id;
    }

    
    public void setId( Long id ) {
    
        this.id = id;
    }

    
    public HashMap< Long, Double > getRatings() {
    
        return ratings;
    }

    
    public void setRatings( HashMap< Long, Double > ratings ) {
    
        this.ratings = ratings;
    }


    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( id == null ) ? 0 : id.hashCode() );
        return result;
    }


    @Override
    public boolean equals( Object obj ) {

        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        User other = (User) obj;
        if ( id == null ) {
            if ( other.id != null )
                return false;
        }
        else if ( !id.equals( other.id ) )
            return false;
        return true;
    }


    @Override
    public String toString() {

        return "User [id=" + id + ", ratings=" + ratings + "]";
    }


    
    public Double getSparsityDegree() {
    
        return (this.ratings.size())/262.0;
    }


}
