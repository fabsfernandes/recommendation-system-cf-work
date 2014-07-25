package br.com.ufu.lsi.recsys.model;

import java.io.Serializable;


public class Rating implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private User user;
    
    private Movie movie;
    
    private Double value;
    
    private Double normalizedValue;
    
    private Double userAverage;
    
    private Double movieAverage;

    
    public User getUser() {
    
        return user;
    }

    
    public void setUser( User user ) {
    
        this.user = user;
    }

    
    public Movie getMovie() {
    
        return movie;
    }

    
    public void setMovie( Movie movie ) {
    
        this.movie = movie;
    }

    
    public Double getValue() {
    
        return value;
    }

    
    public void setValue( Double value ) {
    
        this.value = value;
    }


    
    public Double getNormalizedValue() {
    
        return normalizedValue;
    }


    
    public void setNormalizedValue( Double normalizedValue ) {
    
        this.normalizedValue = normalizedValue;
    }


    
    public Double getUserAverage() {
    
        return userAverage;
    }


    
    public void setUserAverage( Double userAverage ) {
    
        this.userAverage = userAverage;
    }


    
    public Double getMovieAverage() {
    
        return movieAverage;
    }


    
    public void setMovieAverage( Double movieAverage ) {
    
        this.movieAverage = movieAverage;
    }

}
