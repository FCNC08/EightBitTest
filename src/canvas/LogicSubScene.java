package canvas;


import java.util.HashMap;
import java.util.Random;

import canvas.components.CanvasComponent;
import canvas.components.FunctionalCanvasComponent;
import canvas.components.SingleCanvasComponent;
import canvas.components.StandardComponents.Wire;
import javafx.event.EventHandler;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Translate;

public class LogicSubScene extends SubScene{
	
	public static Random random = new Random();
	
	public static int wire_height = 7;
	public static int dot_radius = 7;
	public static int maxed_dot_radius = 20;
	public static int cross_distance = wire_height*3;
	
	protected static Color black = new Color(0.0,0.0,0.0, 1.0);
	
	protected int width; 
	protected int height;
	
	protected int Start_Width;
	protected int Start_Height;
	
	protected int max_zoom;
	
	protected double X = 0;
	protected double Y = 0;
	
	private int pressed_x;
	private int pressed_y;
	private double moves_x;
	private double moves_y;
	
	private boolean adding;
	private boolean primary;
	private boolean secondary;
	
	protected Camera camera;
	protected Translate camera_position;
	
	public static boolean actual_set_state = true;
	
	public static HashMap<Short, SingleCanvasComponent> single_canvas_components;
	protected short[][] used;
	
	private Group root;
	
	public LogicSubScene(Group Mainroot,int StartWidth, int StartHeight, int multiplier) {
		super(Mainroot, StartWidth, StartHeight);
		
		max_zoom = (int) ((StartWidth*-0.5*multiplier)+(cross_distance));
		
		this.Start_Width = StartWidth;
		this.Start_Height = StartHeight;
		
		int Newwidth = StartWidth*multiplier;
		int Newheight = StartHeight*multiplier;
		
		if(Newwidth%cross_distance != 0||Newheight%cross_distance!=0) {
			throw new IllegalArgumentException("The size have to fit with the distance between the crosses");
		}
		
		
		
		width = Newwidth;
		height = Newheight;
		
		used = new short[Newwidth/cross_distance][Newheight/cross_distance];
		
		
		WritableImage Test_Background = generateBackgroundImage();
		
		Mainroot.getChildren().add(new ImageView(Test_Background));
		
		camera_position = new Translate(0,0,0);
		camera = new PerspectiveCamera();
		camera.getTransforms().add(camera_position);
		
		camera_position.setX(width/4-cross_distance/2);
		camera_position.setY(height/4-cross_distance/2);
		
		setCamera(camera);
		
		addZTranslate(multiplier);
		
		
		single_canvas_components = new HashMap<>();
		
		root = Mainroot;
		
		
		EventHandler<MouseEvent> move_Event_Handler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				if(me.isSecondaryButtonDown())
				{
					addXTranslate(moves_x-(me.getSceneX()-X));
					addYTranslate(moves_y-(me.getSceneY()-Y-25));
					moves_x = me.getSceneX()-X;
					moves_y = me.getSceneY()-Y-25;
				}
			}
		};
		EventHandler<MouseEvent> press_Event_Handler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				//Checks which Mousebutton is pressed to figure out which action to perform( try to build new Wire/moves Object or moves scene)
				primary = me.isPrimaryButtonDown();
				secondary = me.isSecondaryButtonDown();
				
				if(primary) {
					adding = true;
					pressed_x = (int) (me.getSceneX()-X+getXTranslate());
					pressed_y = (int) (me.getSceneY()-Y-25+getYTranslate());
				
				}else if(secondary) {
					moves_x = me.getSceneX()-X;
					moves_y = me.getSceneY()-Y-25;
				}
			}
		};
		EventHandler<MouseEvent> released_Mouse_Handler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				if(adding) {
					try {
						short id = used[roundToNextDot((int) (me.getSceneX()-X+getXTranslate()))/cross_distance][ roundToNextDot((int) (me.getSceneY()-Y-25+getYTranslate()))/cross_distance];
						
						int new_pressed_x = (int) (me.getSceneX()-X+getXTranslate());
						int new_pressed_y = (int) (me.getSceneY()-Y-25+getYTranslate());
						
						if(pressed_x == new_pressed_x && pressed_y == new_pressed_y) {
							//Focus Canvas Component with ID id;
						}else {
							addWire(pressed_x, pressed_y, new_pressed_x, new_pressed_y );
						}
						} catch (Exception e) {}
					primary = false;
				}
			}
		};
		
		addEventFilter(MouseEvent.MOUSE_PRESSED, press_Event_Handler);
		addEventFilter(MouseEvent.MOUSE_RELEASED, released_Mouse_Handler);
		addEventFilter(MouseEvent.MOUSE_DRAGGED, move_Event_Handler);
		
		EventHandler<ScrollEvent> zoom_Event_Handler = new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent se) {
				addZTranslate(se.getDeltaY());
			}
		};
		
		addEventFilter(ScrollEvent.SCROLL, zoom_Event_Handler);
		
		
	}
	public void add(FunctionalCanvasComponent component) {
		
	}
	
	public void add(Wire wire) {
		short ID = generateRandomID();		
		short loc_ID;
		try {
			if(wire.rotation == CanvasComponent.HORIZONTAL) {
				for(int x = wire.getXPoint()+1; x < wire.getXPoint()+wire.getWidthPoint(); x++) {
					loc_ID = used[x][wire.getYPoint()];
					if(loc_ID == 0) {
						used[x][wire.getYPoint()] = ID;
					}else if(loc_ID == 1){
						throw new OcupationExeption();
					}else {
						if(getCanvasComponent(loc_ID).checkEnd(x, wire.getYPoint())) {
							getCanvasComponent(loc_ID).addComponent(ID);
							wire.addComponent(loc_ID);
						}
					}
				}
				
				loc_ID = used[wire.getXPoint()][wire.getYPoint()];
				if(loc_ID==0) {
					used[wire.getXPoint()][wire.getYPoint()] = ID;
				}else if(loc_ID==1) {
					throw new OcupationExeption();
				}else {
					getCanvasComponent(loc_ID).addComponent(ID);
					wire.addComponent(loc_ID);
				}
				
				//Sets End/Start Point
				loc_ID = used[wire.getXPoint()+wire.getHeightPoint()][wire.getYPoint()];
				if(loc_ID==0) {
					used[wire.getXPoint()+wire.getHeightPoint()][wire.getYPoint()] = ID;
				}else if(loc_ID==1) {
					throw new OcupationExeption();
				}else {
					getCanvasComponent(loc_ID).addComponent(ID);
					wire.addComponent(loc_ID);
				}
				
			}else {
				for(int y = wire.getYPoint()+1; y < wire.getYPoint()+wire.getHeightPoint(); y++) {
					loc_ID = used[wire.getXPoint()][y];
					if(loc_ID == 0) {
						used[wire.getXPoint()][y] = ID;
					}else if(loc_ID == 1){
						throw new OcupationExeption();
					}else {
						if(getCanvasComponent(loc_ID).checkEnd(wire.getXPoint(), y)) {
							getCanvasComponent(loc_ID).addComponent(ID);
							wire.addComponent(loc_ID);
						}
					}
				}
				

				//Sets End/Start Point
				loc_ID = used[wire.getXPoint()][wire.getYPoint()];
				if(loc_ID==0) {
					used[wire.getXPoint()][wire.getYPoint()] = ID;
				}else if(loc_ID==1) {
					throw new OcupationExeption();
				}else {
					getCanvasComponent(loc_ID).addComponent(ID);
					wire.addComponent(loc_ID);
				}
				
				loc_ID = used[wire.getXPoint()][wire.getYPoint()+wire.getHeightPoint()];
				if(loc_ID==0) {
					used[wire.getXPoint()][wire.getYPoint()+wire.getHeightPoint()] = ID;
				}else if(loc_ID==1) {
					throw new OcupationExeption();
				}else {
					getCanvasComponent(loc_ID).addComponent(ID);
					wire.addComponent(loc_ID);
				}
				
			}
			wire.setId(ID);
			root.getChildren().add(wire.getImageView());
		}catch (OcupationExeption oe) {
			System.out.println("ERROR");
		}
		single_canvas_components.put(ID, wire); 
	}
	
	public static short generateRandomID() {
		short ID = (short) random.nextInt(1 << 15);
		if(ID <= 1) {
			return generateRandomID();
		}
		if(single_canvas_components.getOrDefault(ID, null) != null) {
			return generateRandomID();
		}else {
			return ID;
		}
	}
	
	public static LogicSubScene init(int start_width, int start_height, int multiplier) {
		return new LogicSubScene(new Group(),start_width, start_height, multiplier);
	}
	
	protected WritableImage generateBackgroundImage() {
		WritableImage image = new WritableImage(width, height);
		PixelWriter writer = image.getPixelWriter();
		for(int x = 0; x <=width-1; x+=cross_distance) {
			for(int y = 0; y <= height-1; y+=cross_distance) {
				writer.setColor(x+1, y, black);
				writer.setColor(x, y+1, black);
				writer.setColor(x, y, black);
				
				if(x>=1) {
				writer.setColor(x-1, y, black);
				}
				if(y>=1) {
					writer.setColor(x, y-1, black);
				}
			}
		}
		return image;
	}
	
	protected static int roundToNextDot(int coordinat) throws IllegalArgumentException{
		
		int overflow = coordinat % cross_distance;
		if(overflow<=cross_distance/3) {
			return coordinat-overflow;
		}else if(cross_distance-overflow<=cross_distance/3) {
			return coordinat+cross_distance-overflow;
		}else {
			throw new IllegalArgumentException();
		}
	}
	protected static int getNearesDot(int coordinat) {
		int overflow = coordinat% cross_distance;
		if(overflow<=cross_distance/2) {
			return coordinat-overflow;
		}else {
			return coordinat+cross_distance-overflow;
		}
	}
	
	public void addWire(int start_X, int start_Y, int end_X, int end_Y) {
		int round_start_x = roundToNextDot(start_X);
		int round_start_y = roundToNextDot(start_Y);
		int round_end_x = getNearesDot(end_X);
		int round_end_y = getNearesDot(end_Y);
		
		if(round_start_x!=round_end_x) {
			Wire wire_horizontal = new Wire(Math.abs(round_start_x-round_end_x)+wire_height);
			if(round_start_x >= round_end_x) {
				wire_horizontal.setX(round_end_x-wire_height/2);
			}else {
				wire_horizontal.setX(round_start_x-wire_height/2);
			}
			wire_horizontal.setY(round_start_y-wire_height/2);
			
			wire_horizontal.setState(CanvasComponent.OFF);
			
			add(wire_horizontal);
		}
		if(round_start_y!=round_end_y) {
			Wire wire_vertical = new Wire(Math.abs(round_start_y-round_end_y)+wire_height);
			wire_vertical.setRotation(CanvasComponent.VERTICAL);
			if(round_start_y>=round_end_y) {
				wire_vertical.setY(round_end_y-wire_height/2);
			}else {
				wire_vertical.setY(round_start_y-wire_height/2);
			}
			wire_vertical.setX(round_end_x-wire_height/2);
			
			wire_vertical.setState(CanvasComponent.OFF);
			
			add(wire_vertical);
		}
		
	}
	
	public static SingleCanvasComponent getCanvasComponent(short id) {
		return single_canvas_components.get(id); 
	}
	
	public void addX(int ADD_X) {
		X = getLayoutX()+ADD_X;
		setLayoutX(X);
	}
	public void addY(int ADD_Y) {
		Y = getLayoutY()+ADD_Y;
		setLayoutY(Y);
	}
	
	public void addZTranslate(double z) {
		double Z_Postion = camera_position.getZ()+z;
		if(Z_Postion <= max_zoom) {		
		}else {
			camera_position.setZ(Z_Postion);
		}
		checkXYTanslate();
	}
	public void addXTranslate(double x) {
		double X_Postion = camera_position.getX()+x;
			if(X_Postion<0||X_Postion>(width-Start_Width)) {
			}else {	
				camera_position.setX(X_Postion);
			}
	}
	public void addYTranslate(double y) {
		double Y_Position = camera_position.getY()+y;
		if(Y_Position<0||Y_Position>(height-Start_Height)) {
		}else {
			camera_position.setY(Y_Position);
		}
	}
	
	public void checkXYTanslate(){
		if(getZTranslate()/-2 > getXTranslate()) {
			camera_position.setX(getZTranslate()/-2);
			System.out.println(getXTranslate()+"X-Coord");
		}
	}
	
	public double getZTranslate() {
		return camera_position.getZ();
	}
	public double getXTranslate() {
		return camera_position.getX();
	}
	public double getYTranslate() {
		return camera_position.getY();
	}
}
