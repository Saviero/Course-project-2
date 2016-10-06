package ru.edu.spbstu.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Game extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
    ShapeRenderer shapeRenderer;
    OrthographicCamera camera;
    Map map;
    int width = 800;
    int height = 480;
    int tileWidth = 20;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);
        shapeRenderer.setProjectionMatrix(camera.combined);
		map = new Map(width, height, tileWidth);
        map.generate();
        for(int i=0; i<map.height; ++i) {
            for (int j = 0; j < map.width; ++j) {
                System.out.print(map.mapArray[i][j]);
                System.out.print(" ");
            }
            System.out.print("\n");
        }
	}

	@Override
	public void render () {
        camera.update();
        mapRender();

	}

	private void mapRender()
    {
        int xBrush = 0;
        int yBrush = 0;
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 1, 1, 1);
        for (int i=(map.height-1); i>=0; --i) {
            for(int j=0; j<map.width; ++j) {
                if (map.mapArray[i][j] == 1 || map.mapArray[i][j] == 2) {
                    shapeRenderer.rect(xBrush, yBrush, tileWidth, tileWidth);
                }
                xBrush += tileWidth;
            }
            yBrush += tileWidth;
            xBrush = 0;
        }
        shapeRenderer.end();
    }
	
	@Override
	public void dispose () {
        batch.dispose();
        shapeRenderer.dispose();
	}
}
