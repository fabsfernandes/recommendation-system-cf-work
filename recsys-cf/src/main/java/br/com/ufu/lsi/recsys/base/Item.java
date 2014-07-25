package br.com.ufu.lsi.recsys.base;

import java.io.Serializable;


public class Item implements Serializable {
    
    private static final long serialVersionUID = 1L;

    Long id;
    
    boolean selected;

    public Item( Long id ){
        this.id = id;
    }
    
    public Long getId() {
    
        return id;
    }

    
    public void setId( Long id ) {
    
        this.id = id;
    }

    
    public boolean isSelected() {
    
        return selected;
    }

    
    public void setSelected( boolean selected ) {
    
        this.selected = selected;
    }

    @Override
    public String toString() {

        return "Item [id=" + id + ", selected=" + selected + "]";
    }


}
