enum Color {
	RED, GREEN, BLUE, YELLOw, NONE
}

enum Numbers {
	ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, NONE
}
public class MainCard {
	private Color color;
	private Numbers num;
	
	public MainCard(Color color, Numbers num) {
		this.color = color;
		this.num = num;
	}
	
	public Color getColor() {
		return color;
	}
	
	public Numbers getNum() {
		return num;
	}
	
	public String toString() {
		return(color.name() + " " + num.name());
	}
	
	public boolean matches(MainCard other) {
		if (other.getColor() == color || other.getNum() == num) {
			return true;
		} else {
			return false;
		}
	}
}
