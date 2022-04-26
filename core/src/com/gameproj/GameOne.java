package com.gameproj;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.gameproj.screens.GameScreen;
import com.gameproj.utilities.JukeBox;
import com.gameproj.utilities.TexturePackerTool;

public class GameOne extends Game {
	private OrthographicCamera camera;
	public SpriteBatch batch;
	public static JukeBox jukeBox;
	public static float gravity;
	public static final int tile = 16;
	public static final float PPM = 100;
	public static float timer = 0;
	public static int frame = 0;

	
	@Override
	public void create () {
		TexturePackerTool.init();
		Box2D.init();
		jukeBox = new JukeBox().init();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 400, 280);

		batch = new SpriteBatch();
		setScreen(new GameScreen(this));

	}

	@Override
	public void render () {
		timer += Gdx.graphics.getDeltaTime();
		incFrame();
		super.render();

	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}

	public OrthographicCamera getCamera() {
		return camera;
	}
	public static void p(Object s) {
		System.out.print(s.toString());
	}
	public static void p(Object s, boolean newLine) {
		System.out.println(s.toString());
	}
	private static void incFrame() {
		if(frame >= 10000)
			frame = 0;
		frame++;
	}
}
