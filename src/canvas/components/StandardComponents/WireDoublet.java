package canvas.components.StandardComponents;

public class WireDoublet {
	public Wire verticalWire;
	public Wire horizontalWire;
	
	public WireDoublet() {
	}
	
	public void setVerticalWire(Wire verticalWire) {
		this.verticalWire = verticalWire;
	}
	public void setHorizontalWire(Wire horizontalWire) {
		this.horizontalWire = horizontalWire;
	}
	
	public Wire getVerticalWire() {
		return verticalWire;
	}
	public Wire getHorizontalWire() {
		return horizontalWire;
	}
}
