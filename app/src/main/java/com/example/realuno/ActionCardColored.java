enum Action {
	DRAW2, SKIP, REVERSE, NONE
}
public class ActionCardColored extends MainCard {
	Action ability;
	Color color;
    public ActionCardColored(Action ability, Color color) {
    	super(color, Numbers.NONE);
    	this.color = color;
    	this.ability = ability;
    }
    
    public ActionCardColored(MainCard other) {
    	super(other.getColor(), other.getNum());
    	this.color = other.getColor();
    }
    
    public Action getAbility() {
    	return ability;
    }
    
    public String toString() {
    	return(color.name() + " " + ability.name());
    }
    
    public boolean matches(MainCard other) {
    	ActionCardColored temp = new ActionCardColored(other);
    	if (temp.getColor() != color) {
    		return false;
    	} else if (temp.getNum() != this.getNum()) {
    		return false;
    	} else {
    		return actionMatch(temp);
    	}
    	
    }
    
    public boolean actionMatch(ActionCardColored other) {
    	if (ability == other.getAbility()) {
    		return true;
    	} else {
    		return false;
    	}
    }
}