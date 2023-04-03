package canvas.components;

import java.util.ArrayList;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class SingleCanvasComponent extends CanvasComponent {


	protected ArrayList<Short> connected_Components;
	
	protected State state = new State(State.ERROR_MODE, State.OFF_UNSET);
	
	public SingleCanvasComponent(int NewWidth, int NewHeight) {
		super(NewWidth, NewHeight);
	}
	
	public void setState(State newState) {
		state = newState;
		change();
	}
	public State getState() {
		return state;
	}
	protected Color getColor() {
		return state.getColor();
	}
	
	public static SingleCanvasComponent initImage(String url) {
		Image temp_img = new Image(url);
		SingleCanvasComponent component = new SingleCanvasComponent((int) temp_img.getWidth(), (int) temp_img.getHeight());
		component.setImage(temp_img);
		return component;
	}

	protected void change() {
		//Override in higher Classes
	}

	public boolean checkEnd(int x, int y) {
		if(rotation == HORIZONTAL) {
			return (x==getXPoint()&&y==getYPoint())||(x==(getXPoint()+getHeightPoint())&&y==getYPoint());
		}else {
			return (x==getXPoint()&&y==getYPoint())||(x==getXPoint()&&y==(getYPoint()+getHeightPoint()));
		}
	}
	
	public void addComponent(short ID) {
		connected_Components.add(ID);
	}
}
