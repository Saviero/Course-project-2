package ru.edu.spbstu.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Hashtable;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.time.Instant;

public class Game extends ApplicationAdapter {

    private enum GameState
    {
        MAIN_MENU, PLAY, HIGHESTSCORES
    }



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
    List <Zombie> zombies; //all the zombies
    List <Bullet> bullets; //all the bullets
    GameState gamestate; //current state of the game
    Stage mainmenu; // stage for main menu
    Table table;
    Instant start;
    Instant finish;
    int delay;

    private void loadTextures()
    {
        Texture load = new Texture("unit.jpg");
        textures.put("Unit", load);
        load = new Texture("unit_selected.jpg");
        textures.put("Unit_selected", load);
        load = new Texture("zombie.jpg");
        textures.put("Zombie", load);
        load = new Texture("bullet.jpg");
        textures.put("Bullet", load);
    }
	
	@Override
	public void create () {
        gamestate = GameState.MAIN_MENU;
        mainmenu = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(mainmenu);
        camera = new OrthographicCamera();

        table = new Table();
        table.setFillParent(true);
        mainmenu.addActor(table);
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = new BitmapFont();
        style.fontColor = new Color(0, 0, 0, 0.7f);
        Label label = new Label("A game about zombies and stuff", style);
        table.top();
        table.add(label).expandX().pad(50);
        TextButton.TextButtonStyle stylebutton = new TextButton.TextButtonStyle();
        stylebutton.font = new BitmapFont();
        stylebutton.fontColor = new Color(0, 0, 0, 0.7f);
        TextButton button = new TextButton("New Game", stylebutton);
        table.row();
        table.add(button);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                loadGame();
            }
        });


        //table.setDebug(true, true);
	}

	private void loadGame()
    {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);
        map = new Map(width, height, tileWidth);
        map.generate();
        //map.bcd(map.getEntrance().firstElement()); //debug method
        loadTextures();
        input = new Input();
        Gdx.input.setInputProcessor(input);
        unitCounter = 2;
        selected = null;
        zombieCounter = 200;
        zombies = new ArrayList <Zombie>(zombieCounter);
        for (int i = 0; i < zombieCounter; ++ i) {
            zombies.add(i, new Zombie(map, textures.get("Zombie").getWidth()));
        }
        bullets = new ArrayList<Bullet>( );
        gamestate = GameState.PLAY;
        finish = start = Instant.now();
    }

    public void resize (int width, int height) {
        mainmenu.getViewport().update(width, height, true);
    }

	@Override
	public void render () {
        switch (gamestate) {
            case MAIN_MENU:
            {
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                Gdx.gl.glClearColor(1, 1, 1, 1);
                mainmenu.act(Gdx.graphics.getDeltaTime());
                mainmenu.draw();
                break;
            }
            case PLAY: {
                camera.update();
                mapRender();
                unitRender();
                zombieRender();
                bulletRender();
                inputSwitch();
                //map.bcd(map.getEntrance().firstElement()); //debug method
                break;
            }
            case HIGHESTSCORES: {

            }
        }
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
                if (map.getTile(j, i) instanceof RoadTile) {
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
            Bullet bullet = unit.shoot(map);
            if (bullet != null) {
                bullets.add(bullet);
            }
        }
        batch.end( );
    }


    private void zombieRender( ) {
        //Here we make our zombies move
        Texture texture = textures.get("Zombie");
        FloatPoint brush;
        batch.begin();
        for (Iterator <Zombie> it = zombies.iterator(); it.hasNext(); )
        {
            Zombie zombie = it.next();
            brush = new FloatPoint(zombie.getCoordinates());
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
            batch.draw(texture, brush.x - texture.getWidth() / 2, brush.y - texture.getHeight() / 2);
            zombie.walk(map);
            if (!zombie.isWalking()) {
                it.remove( );
            }
        }
        if (zombies.size() == 0) {
            ++delay;
            if (delay == 70) {
                finish = Instant.now();
                gamestate = GameState.HIGHESTSCORES;
            }
        }
        batch.end( );
    }

    private void bulletRender( ){
        Texture texture = textures.get("Bullet");
        FloatPoint brush;
        batch.begin();
        for (Iterator <Bullet> it = bullets.iterator(); it.hasNext( );)
        {
            Bullet bullet = it.next();
            brush = new FloatPoint(bullet.getCoordinates());
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
            batch.draw(texture, brush.x - texture.getWidth() / 2, brush.y - texture.getHeight() / 2);
            bullet.fly(map);
            if (!bullet.isMoving( )) {
                it.remove( );
            }
        }
        batch.end( );
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

    private void mainMenuRender()
    {

    }

	@Override
	public void dispose () {
        try {
            batch.dispose();
            shapeRenderer.dispose();
        }
        catch (NullPointerException e)
        {

        }
	}
}
