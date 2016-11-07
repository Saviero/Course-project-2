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
    int zombieCounter; //the amount of zombies to kill
    Zombie[ ] zombies; //all the zombies

    private void loadTextures()
    {
        Texture load = new Texture("unit.jpg");
        textures.put("Unit", load);
        load = new Texture("unit_selected.jpg");
        textures.put("Unit_selected", load);
        load = new Texture("zombie.jpg");
        textures.put("Zombie", load);
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
        unitCounter = 2;
        selected = null;
        zombieCounter = 100;
        zombies = new Zombie[zombieCounter];
        for (int i = 0; i < zombieCounter; ++i) {
            zombies[i] = new Zombie(map);
        }
	}

	@Override
	public void render () {
        camera.update();
        mapRender();
        unitRender();
        zombieRender( );
        inputSwitch();
	}

	private void mapRender()
    {
        /*
        Method for drawing a map using shapeRenderer
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
        Texture texture_selected = textures.get("Unit_selected");
        FloatPoint brush;
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
            if (unit == selected) {
                batch.draw(texture_selected, brush.x, brush.y);
            }
            else {
                batch.draw(texture, brush.x, brush.y);
            }
            unit.move(map);
        }
        batch.end();
    }


    private void zombieRender( ) {
        //Here we make our zombies move
        Texture texture = textures.get("Zombie");
        Point brush;
        batch.begin();
        for (int i = 0; i < zombieCounter; ++i)
        {
            brush = new Point(zombies[i].getCoordinates());
            brush.y = height  - brush.y;

            /*
             Zombie coordinates pointing at the centre of tile and y coordinate is shifted;
             shifting y to match batch coordinates and make coordinates pointing at bottom left corner of tile
            */
            /*
            if (map.getTile((brush.x + texture.getWidth()) / 20, brush.y / 20) == null || map.getTile((brush.x + texture.getWidth()) / 20, brush.y / 20).getValue() != 1) {
                brush.x -= texture.getWidth();
            }
            if (map.getTile(brush.x / 20, (brush.y + texture.getHeight()) / 20) == null || map.getTile(brush.x / 20, (brush.y + texture.getHeight()) / 20).getValue() != 1) {
                brush.y -= texture.getHeight();
            }*/
//            brush.y -= texture.getHeight()/2;
            batch.draw(texture, brush.x, brush.y);
            zombies[i].walk(map);
        }
        batch.end();
    }


    private void inputSwitch() {
        if (input.isPressed() && input.getButton() == Buttons.LEFT) // Left mouse button
        {
            Point pos = new Point(input.mousePos());
            for (Unit unit : units) {
                if (pos.almostEqual(unit.getCoordinates(), tileWidth / 2)) {
                    selected = unit;
                    return;
                } else
                    selected = null;
            }
            Tile tile = map.getTile(pos.x / tileWidth, pos.y / tileWidth);
            if (tile instanceof RoadTile) {
                if (((RoadTile) tile).getUnit() == null) {
                    if (unitCounter > 0) { // if tile of road is without a unit, trying to spawn another one
                        pos.x -= pos.x % tileWidth;
                        pos.y -= pos.y % tileWidth;
                        pos.x += tileWidth / 2;
                        pos.y += tileWidth / 2;
                        Unit createUnit = new Unit(pos.x, pos.y, map);
                        ((RoadTile) tile).putUnit(createUnit);
                        units.addElement(createUnit);
                        unitCounter--;
                    }
                    else // otherwise, de-selecting unit
                    {
                        selected = null;
                    }
                }
                else if (((RoadTile) tile).getUnit() != null) // if there's a unit, selecting it
                {
                    selected = ((RoadTile) tile).getUnit();
                }
            }
        }
        if (input.isPressed() && input.getButton() == Buttons.RIGHT && selected != null) //Right mouse button
        {
            Point posMouse = new Point(input.mousePos());
            posMouse.x /= tileWidth;
            posMouse.y /= tileWidth;
            Tile tile = map.getTile(posMouse);
            if (tile == null) {
                return;
            }
            if (tile instanceof RoadTile && ((RoadTile) tile).getUnit() == null) { // if there's selected unit, trying to move it
                selected.setTarget(posMouse.x, posMouse.y, map);
            }
        }
    }

	@Override
	public void dispose () {
        batch.dispose();
        shapeRenderer.dispose();
	}
}
