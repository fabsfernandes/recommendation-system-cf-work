package br.com.ufu.lsi.recsys.model;

import java.io.Serializable;
import java.util.HashMap;


public class Movie implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    Long id;
    
    HashMap<Long,Double> ratings = new HashMap<Long,Double>();
    
    String gender;
    
    String director;
    
    String starring;
    
    String language;
    
    public Movie( Long id ){
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
        Movie other = (Movie) obj;
        if ( id == null ) {
            if ( other.id != null )
                return false;
        }
        else if ( !id.equals( other.id ) )
            return false;
        return true;
    }

    
    public String getGender() {
    
        return gender;
    }


    
    public void setGender( String gender ) {
    
        this.gender = gender;
    }


    
    public String getDirector() {
    
        return director;
    }


    
    public void setDirector( String director ) {
    
        this.director = director;
    }


    
    public String getStarring() {
    
        return starring;
    }


    
    public void setStarring( String starring ) {
    
        this.starring = starring;
    }


    
    public String getLanguage() {
    
        return language;
    }


    
    public void setLanguage( String language ) {
    
        this.language = language;
    }
    
    
    public String toStringFull() {

        return "Movie [id=" + id + ", gender=" + gender + ", director=" + director
            + ", starring=" + starring + ", language=" + language + "]";
    }


    @Override
    public String toString() {

        return "Movie [id=" + id + ", ratings=" + ratings + "]";
    }
    
    

}
