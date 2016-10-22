package ru.edu.spbstu.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.InputAdapter;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


public class Game extends ApplicationAdapter {

    private class Input extends InputAdapter
    {
        private int mouseX;
        private int mouseY;
        private boolean pressed;

        Input()
        {
            super();
            mouseX = 0;
            mouseY = 0;
            pressed = false;
        }

        @Override
        public boolean touchDown(int x, int y, int pointer, int button)
        {
            mouseX = x;
            mouseY = y;
            pressed = true;
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
    }

    SpriteBatch batch;
	Hashtable<String, Texture> textures = new Hashtable<String, Texture>();
    ShapeRenderer shapeRenderer;
    OrthographicCamera camera;
    Input input;
    Map map;
    int width = 800;
    int height = 480;
    int tileWidth = 20;
    Vector<Unit> units = new Vector<Unit>();

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
	}

	@Override
	public void render () {
        camera.update();
        mapRender();
        unitRender();
        if (input.isPressed())
        {
            Point pos = new Point(input.mousePos());
            int tileX = pos.x/tileWidth;
            int tileY = pos.y/tileWidth;
            Tile tile = map.getTile(tileX, tileY);
            if (tile instanceof RoadTile) {
                pos.x -= pos.x % tileWidth;
                pos.y -= pos.y % tileWidth;
                pos.x += tileWidth / 2;
                pos.y += tileWidth / 2;
                Unit createUnit = new Unit(pos.x, pos.y, map);
                ((RoadTile) tile).putUnit(createUnit);
                units.addElement(createUnit);
            }
        }
	}

	private void mapRender()
    {
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
        Texture texture = textures.get("Unit");
        Point brush;
        batch.begin();
        for (Unit unit : units)
        {
            brush = new Point(unit.coordinates);
            brush.y = height - brush.y;
            brush.x -= tileWidth/2;
            brush.y -= tileWidth/2;
            batch.draw(texture, brush.x, brush.y);
        }
        batch.end();
    }

	@Override
	public void dispose () {
        batch.dispose();
        shapeRenderer.dispose();
	}
}
