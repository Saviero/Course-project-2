package ru.edu.spbstu.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.TimeUtils;

import java.io.*;
import java.util.*;
import java.util.List;

public class Game extends ApplicationAdapter {

    private enum GameState
    {
        MAIN_MENU, PLAY, HIGHEST_SCORES, ENTER_NAME
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
    Vector<Unit> units; // all units generated on map
    int unitCounter; // how much units user can spawn at the time
    Unit selected; // selected unit
    int zombieCounter; //the amount of zombies to kill
    List <Zombie> zombies; //all the zombies
    List <Bullet> bullets; //all the bullets
    GameState gamestate; //current state of the game
    Stage mainscreen; // stage for main menu
    long start;
    long finish;
    int delay;
    Stage resultTable; //stage for results
    HighScoreTable scores;
    String playername = "Hero";


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

        newGame();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./scores"))){
            scores = (HighScoreTable) ois.readObject();
            ois.close();
        }
        catch (FileNotFoundException e)
        {
            System.err.println("SCORES file not found, creating new one");
            FileHandle file = new FileHandle("./scores");
            scores = new HighScoreTable();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file.file())))
            {
                oos.writeObject(scores);
                oos.flush();
                oos.close();
            }
            catch (IOException exc)
            {
                System.err.println("EVERYTHING IS VERY BAD");
            }
        }
        catch (IOException e)
        {
            System.err.println("SCORES file cannot be loaded, creating new table");
            scores = new HighScoreTable();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./scores")))
            {
                oos.writeObject(scores);
                oos.flush();
                oos.close();
            }
            catch (IOException exc)
            {
                System.err.println("EVERYTHING IS VERY BAD");
            }
        }
        catch (ClassNotFoundException e)
        {
            System.err.println("Class not found!");
        }
        //table.setDebug(true, true);
	}

	private void enterName()
    {
        gamestate = GameState.ENTER_NAME;
        mainscreen = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(mainscreen);
        camera = new OrthographicCamera();
        Table table = new Table();

        table.setFillParent(true);
        mainscreen.addActor(table);
        TextField.TextFieldStyle style = new TextField.TextFieldStyle();
        style.font = new BitmapFont(new FileHandle("./mainmenu.fnt"));
        style.messageFont = new BitmapFont();
        style.fontColor = new Color(0, 0, 0, 0.7f);
        style.messageFontColor = new Color(0, 0, 0, 0.3f);
        Label.LabelStyle lstyle = new Label.LabelStyle();
        lstyle.font = new BitmapFont(new FileHandle("./mainmenu.fnt"));
        lstyle.fontColor = new Color(0, 0, 0, 0.7f);
        table.add(new Label("Enter your name: ", lstyle));
        TextField tfield = new TextField("Hero", style);
        table.add(tfield);
        mainscreen.setKeyboardFocus(tfield);
        tfield.setAlignment(Align.center);
        tfield.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                playername = textField.getText();
            }
        });
        table.row();
        table.center();
        TextButton.TextButtonStyle stylebutton = new TextButton.TextButtonStyle();
        stylebutton.font = new BitmapFont(new FileHandle("./mainmenu.fnt"));
        stylebutton.fontColor = new Color(0, 0, 0, 0.7f);
        TextButton button = new TextButton("Play", stylebutton);
        table.add(button).colspan(2).pad(50);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                loadGame();
            }
        });

    }

	private void newGame( ) {
        gamestate = GameState.MAIN_MENU;
        mainscreen = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(mainscreen);
        camera = new OrthographicCamera();

        Table table = new Table();
        table.setFillParent(true);
        mainscreen.addActor(table);
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = new BitmapFont(new FileHandle("./mainmenu.fnt"));
        style.fontColor = new Color(0, 0, 0, 0.7f);
        Label label = new Label("A game about zombies and stuff", style);
        table.top();
        table.add(label).expandX().pad(50);
        TextButton.TextButtonStyle stylebutton = new TextButton.TextButtonStyle();
        stylebutton.font = new BitmapFont(new FileHandle("./mainmenu.fnt"));
        stylebutton.fontColor = new Color(0, 0, 0, 0.7f);
        TextButton button = new TextButton("New Game", stylebutton);
        table.row();
        table.add(button);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                enterName();
            }
        });
        button = new TextButton("Records", stylebutton);
        table.row();
        table.add(button);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                results();
            }
        });
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
        units = new Vector<Unit>();
        selected = null;
        zombieCounter = 1;
        zombies = new ArrayList <Zombie>(zombieCounter);
        for (int i = 0; i < zombieCounter; ++ i) {
            zombies.add(i, new Zombie(map, textures.get("Zombie").getWidth()));
        }
        bullets = new ArrayList<Bullet>( );
        gamestate = GameState.PLAY;
        finish = start = TimeUtils.millis();
        delay = 0;
    }

    public void results( ) {
        resultTable = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(resultTable);
        camera = new OrthographicCamera();

        Table table = new Table();
        table.setFillParent(true);
        resultTable.addActor(table);
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = new BitmapFont(new FileHandle("./mainmenu.fnt"));
        style.fontColor = new Color(0, 0, 0, 0.7f);
        Label label = new Label("Best scores", style);
        table.top();
        table.add(label).expandX().pad(50);

        String strscore;
        Integer place = 1;
        for(HighScore score : scores)
        {
            strscore = place.toString() + ": " + score.getScore();
            Label lstrscore = new Label(strscore, style);
            table.row();
            table.add(lstrscore);
            ++place;
        }

        TextButton.TextButtonStyle stylebutton = new TextButton.TextButtonStyle();
        stylebutton.font = new BitmapFont(new FileHandle("./mainmenu.fnt"));
        stylebutton.fontColor = new Color(0, 0, 0, 0.7f);
        TextButton button = new TextButton("Main menu", stylebutton);
        table.row();
        table.add(button).pad(50);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {


                newGame();
            }
        });
    }

    public void resize (int width, int height) {
        mainscreen.getViewport().update(width, height, true);
    }

	@Override
	public void render () {
        switch (gamestate) {
            case MAIN_MENU:
            {
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                Gdx.gl.glClearColor(1, 1, 1, 1);
                mainscreen.act(Gdx.graphics.getDeltaTime());
                mainscreen.draw();
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
            case HIGHEST_SCORES: {
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                Gdx.gl.glClearColor(1, 1, 1, 1);
                resultTable.act(Gdx.graphics.getDeltaTime());
                resultTable.draw();
                break;
            }
            case ENTER_NAME: {
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                Gdx.gl.glClearColor(1, 1, 1, 1);
                mainscreen.act(Gdx.graphics.getDeltaTime());
                mainscreen.draw();
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
            if (finish == start) {
                finish = TimeUtils.millis();
            }
            if (delay == 70) {
                gamestate = GameState.HIGHEST_SCORES;
                scores.addScore(playername, finish - start);
                results();
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
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./scores")))
            {
                oos.writeObject(scores);
                oos.flush();
                oos.close();
            }
            catch (IOException exc)
            {
                System.err.println("EVERYTHING IS VERY BAD");
            }
            batch.dispose();
            shapeRenderer.dispose();
        }
        catch (NullPointerException e)
        {

        }
	}

}


