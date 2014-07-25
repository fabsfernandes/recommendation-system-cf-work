package br.com.ufu.lsi.recsys.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class UtilityMatrix {

    HashMap< Long,User > users = new HashMap< Long,User >();

    HashMap< Long,Movie > movies = new HashMap< Long,Movie >();
    
    Set<Double> ratings = new HashSet<Double>();
    
    List<User> usersSparsity = new ArrayList< User >();

    
    public HashMap< Long,User > getUsers() {
    
        return users;
    }

    
    public void setUsers( HashMap< Long,User > users ) {
    
        this.users = users;
    }

    
    public HashMap< Long,Movie > getMovies() {
    
        return movies;
    }

    
    public void setMovies( HashMap< Long,Movie > movies ) {
    
        this.movies = movies;
    }


    
    public Set< Double > getRatings() {
    
        return ratings;
    }


    
    public void setRatings( Set< Double > ratings ) {
    
        this.ratings = ratings;
    }


    
    public List< User > getUsersSparsity() {
    
        return usersSparsity;
    }


    
    public void setUsersSparsity( List< User > usersSparsity ) {
    
        this.usersSparsity = usersSparsity;
    }

}
