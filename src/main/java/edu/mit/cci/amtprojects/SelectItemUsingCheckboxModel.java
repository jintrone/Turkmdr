package edu.mit.cci.amtprojects;

import java.util.Set;

import org.apache.wicket.extensions.model.AbstractCheckBoxModel;

import com.amazonaws.mturk.requester.HIT;

public class SelectItemUsingCheckboxModel extends AbstractCheckBoxModel {

	private HIT hit; 
    private Set selection; 

    public SelectItemUsingCheckboxModel(HIT h, Set selection) { 
        this.hit = h; 
        this.selection = selection; 
     } 

    @Override 
    public boolean isSelected() { 
        return selection.contains(hit); 
    } 

    @Override 
    public void select() { 
        selection.add(hit); 
    } 

    @Override 
    public void unselect() { 
        selection.remove(hit); 
    }

}
