package br.com.ufu.lsi.recsys.base;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class User implements Serializable {
    
    private static final long serialVersionUID = 1L;

    Long id;
    
    HashMap<Item,Double> items;
    
    Double averageRating;
    
    boolean selected;
    
    Double accuracy;

    public User(){
        this.items = new HashMap<Item,Double>();
        this.selected = false;
    }
    
    public Long getId() {
    
        return id;
    }

    
    public void setId( Long id ) {
    
        this.id = id;
    }

    
    public HashMap< Item, Double > getItems() {
    
        return items;
    }

    
    public void setItems( HashMap< Item, Double > items ) {
    
        this.items = items;
    }

    
    public Double getAverageRating() {
    
        return averageRating;
    }

    
    public void setAverageRating( Double averageRating ) {
    
        this.averageRating = averageRating;
    }

    
    public boolean isSelected() {
    
        return selected;
    }

    
    public void setSelected( boolean selected ) {
    
        this.selected = selected;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder("{");
        
        for ( Map.Entry< Item, Double > entryItem : this.items.entrySet() ) {
            Item item = entryItem.getKey();
            Double ratingItem = entryItem.getValue();
            if( item.isSelected() ){
                sb.append( item.getId() + "=" + ratingItem + ", " );                
            }
        }
        sb.append( "}" );
        
        return "User [id=" + id + ", items=" + sb.toString() + ", averageRating=" + averageRating + ", selected=" + selected
            + "]";
    }

    
    public Double getAccuracy() {
    
        return accuracy;
    }

    
    public void setAccuracy( Double accuracy ) {
    
        this.accuracy = accuracy;
    }

}
