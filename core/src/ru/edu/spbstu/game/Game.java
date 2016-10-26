package ru.edu.spbstu.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Buttons;

import java.util.Hashtable;
import java.util.Vector;


public class Game extends ApplicationAdapter {

    private class Input extends InputAdapter
    {
        /*
        This class implements controls for the game
         */
        private int mouseX;
        private int mouseY;
        private boolean pressed;
        private int button;

        Input()
        {
            super();
            mouseX = 0;
            mouseY = 0;
            pressed = false;
            button = 0;
        }

        @Override
        public boolean touchDown(int x, int y, int pointer, int button)
        {
            mouseX = x;
            mouseY = y;
            pressed = true;
            this.button = button;
            return false;
        }

        @Override
        public boolean touchUp(int x, int y, int pointer, int button)
        {
            pressed = false;
            return false;
        }

        public boolean isPressed()
        {
            return pressed;
        }

        public Point mousePos()
        {
            return new Point (mouseX, mouseY);
        }

        public int getButton() {return button;}
    }

    SpriteBatch batch;
	Hashtable<String, Texture> textures = new Hashtable<String, Texture>(); // dictionary for textures
    ShapeRenderer shapeRenderer;
    OrthographicCamera camera;
    Input input;
    Map map;
    int width = 800;
    int height = 480;
    int tileWidth = 20;
    Vector<Unit> units = new Vector<Unit>(); // all units generated on map
    int unitCounter; // how much units user can spawn at the time
    Unit selected; // selected unit

    private void loadTextures()
    {
        Texture load = new Texture("unit.jpg");
        textures.put("Unit", load);
    }
	
	@Override
	public void create () {
		batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);
		map = new Map(width, height, tileWidth);
        map.generate();
        loadTextures();
        input = new Input();
        Gdx.input.setInputProcessor(input);
        unitCounter = 1; // TODO: improve unitCounter
        selected = null;
	}

	@Override
	public void render () {
        camera.update();
        mapRender();
        unitRender();
        inputSwitch();
	}

	private void mapRender()
    {
        /*
        Method for drawingg a map using shapeRenderer
         */
        Point brush = new Point (0, 0);
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 1, 1, 1);
        for (int i=(map.getHeight()-1); i>=0; --i) {
            for(int j=0; j<map.getWidth(); ++j) {
                if (map.getTile(j, i).getValue() == 1 || map.getTile(j, i).getValue() == 2) {
                    shapeRenderer.rect(brush.x, brush.y, tileWidth, tileWidth);
                }
                brush.x += tileWidth;
            }
            brush.y += tileWidth;
            brush.x = 0;
        }
        shapeRenderer.end();
    }

    private void unitRender()
    {
        /*
        A method for drawing all units on map
         */
        Texture texture = textures.get("Unit");
        Point brush;
        batch.begin();
        for (Unit unit : units)
        {
            brush = unit.getCoordinates();
            /*
             Unit coordinates pointing at the centre of tile and y coordinate is shifted;
             shifting y to match batch coordinates and make coordinates pointing at bottom left corner of tile
            */
            brush.y = height - brush.y;
            brush.x -= tileWidth/2;
            brush.y -= tileWidth/2;
            batch.draw(texture, brush.x, brush.y);
            unit.move(map);
        }
        batch.end();
    }

    private void inputSwitch()
    {
        if (input.isPressed() && input.getButton() == Buttons.LEFT) // Left mouse button
        {
            Point pos = new Point(input.mousePos());
            int tileX = pos.x/tileWidth;
            int tileY = pos.y/tileWidth;
            Tile tile = map.getTile(tileX, tileY);
            if (tile instanceof RoadTile) {
                if (((RoadTile) tile).getUnit() == null && unitCounter > 0) { // if tile of road is without a unit, trying to spawn another one
                    pos.x -= pos.x % tileWidth;
                    pos.y -= pos.y % tileWidth;
                    pos.x += tileWidth / 2;
                    pos.y += tileWidth / 2;
                    Unit createUnit = new Unit(pos.x, pos.y, map);
                    ((RoadTile) tile).putUnit(createUnit);
                    units.addElement(createUnit);
                    unitCounter--;
                }
                else if (((RoadTile) tile).getUnit() != null) // if there's a unit, selecting it
                {
                    selected = ((RoadTile) tile).getUnit();
                }
            }
        }
        if (input.isPressed() && input.getButton() == Buttons.RIGHT && selected != null) //Right mouse button
        {
            Point pos = new Point(input.mousePos());
            int tileX = pos.x/tileWidth;
            int tileY = pos.y/tileWidth;
            Tile tile = map.getTile(tileX, tileY);
            if (tile instanceof RoadTile) // if there's selected unit, trying to move it
                selected.setTarget(tileX, tileY, map); //TODO: Implement method
        }
    }

	@Override
	public void dispose () {
        batch.dispose();
        shapeRenderer.dispose();
	}
}
