package canvas.components;

import java.util.HashMap;

import canvas.LogicSubScene;
import javafx.scene.image.ImageView;

public abstract class FunctionalCanvasComponent extends CanvasComponent{
	public static final byte  SIZE_BIG = 2;
	public static final byte SIZE_MIDDLE = 1;
	public static final byte SIZE_SMALL = 0;
	
	protected static int StandardWidth_big = 400, StandardHeight_big = 400; 
	
	protected static int StandardWidth_middle = 200, StandardHeight_middle = 200;
	
	protected static int StandardWidth_small = 100, StandardHeight_small = 100; 
	
	protected byte Size;
	
	protected HashMap<State[], State[]> truth_table;
	
	protected int input_count;
	protected int output_count;
	
	public Dot[] inputs;
	public Dot[] outputs;
	
	public FunctionalCanvasComponent(byte size,int width, int height,int input_count, int output_count) {
		super(width, height);
		
		this.Size = size;
		
		this.input_count = input_count;
		this.output_count = output_count;
		
		inputs = new Dot[input_count];
		outputs = new Dot[output_count];
	}

	public static FunctionalCanvasComponent initImage(String url, int inputs, int outputs, int[] inputs_x, int[] inputs_y, int[] outputs_x, int[] outputs_y ) {
		//Override in higher classes
		/*Image temp_img = new Image(url);
		FunctionalCanvasComponent component = new FunctionalCanvasComponent((int)temp_img.getWidth(),(int)temp_img.getHeight(), inputs, outputs, inputs_x, inputs_y, outputs_x, outputs_y);
		ImageView temp_view = new ImageView(temp_img);
		temp_view.snapshot(null, component);
		temp_img = null;
		temp_view = null;
		System.gc();
		return component;*/
		return null;
	}
	public static FunctionalCanvasComponent initImage(ImageView image, int input_count, int output_count, int[] inputs_x, int[] inputs_y, int[] outputs_x, int[] outputs_y) {
		//Override in higher classes
		/*FunctionalCanvasComponent component = new FunctionalCanvasComponent(
				(int)image.getImage().getWidth(), (int) image.getImage().getHeight(), input_count, output_count, inputs_x, inputs_y, outputs_x, outputs_y );
		image.snapshot(null, component);
		return component;*/
		return null;
	}
	
	protected void setStandardDotLocations() {
		int standard_y_distance = height/(input_count+1);
		int y_location = standard_y_distance+getY();
		int x_location = getX();
		for(Dot d : inputs) {
			d.setY(LogicSubScene.getNearesDot(y_location));
			d.setX(LogicSubScene.getNearesDot(x_location));
			y_location+=standard_y_distance;
		}
		
		standard_y_distance = height/(output_count+1);
		y_location = standard_y_distance+getY();
		x_location = getX()+width;
		for(Dot d : outputs) {
			d.setY(LogicSubScene.getNearesDot(y_location));
			d.setX(LogicSubScene.getNearesDot(x_location));
			y_location+=standard_y_distance;
		}
	}
	
	public abstract void simulate();
	
	public abstract FunctionalCanvasComponent getClone(byte size);
	
	protected State[] getInputStates() {
		State[] states = new State[inputs.length];
		for(int i = 0; i < inputs.length; i++) {
			states[i] = inputs[i].getState();
		}
		return states;
	}
	
	protected void setOutputStates(State[] states) {
		if(states.length != outputs.length) {
			throw new IllegalArgumentException();
		}else {
			for(int i = 0; i < states.length; i++) {
				outputs[i].setState(states[i]);
			}
		}
	}
}
