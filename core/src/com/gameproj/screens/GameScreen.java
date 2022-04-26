package com.gameproj.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gameproj.Controller;
import com.gameproj.GameOne;
import com.gameproj.actors.enemies.Enemy;
import com.gameproj.actors.enemies.Goomba;
import com.gameproj.entities.Player;
import com.gameproj.entities.collectables.Collectable;
import com.gameproj.entities.collectables.ItemDef;
import com.gameproj.entities.environment.Brick;
import com.gameproj.entities.environment.BrickPiece;
import com.gameproj.entities.environment.NonCollectable;
import com.gameproj.entities.environment.WalkableTerrain;
import com.gameproj.utilities.ItemHandler;
import com.gameproj.utilities.TextureGrabber;
import com.gameproj.utilities.WorldContactListener;
import com.gameproj.utilities.WorldGenerator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import static com.gameproj.GameOne.PPM;

public class GameScreen implements Screen {
    private final OrthographicCamera camera;
    public final Controller controller;
    public  final Viewport viewport;
    private final SpriteBatch batch;

    private Set<Brick> bricks;
    private Set<Collectable> collectables;
    private Set<NonCollectable> nonCollectables;
    private Set<Enemy> enemies;
    public static Stack<ItemDef> itemsToAdd = new Stack<>();
    private ItemHandler itemHandler;

    private OrthogonalTiledMapRenderer renderer;
    private Box2DDebugRenderer b2dRenderer;
    private World world;

    private Player player;

    public GameScreen(GameOne game) {
        camera = game.getCamera();
        initAtlasMap();
        controller = new Controller(this);
        viewport = new FitViewport(400 / PPM, 200 / PPM , this.camera);
        batch = game.batch;

        bricks = new HashSet<>();
        collectables = new HashSet<>();
        nonCollectables = new HashSet<>();
        enemies = new HashSet<>();
        WorldGenerator.init(this);
        itemHandler = new ItemHandler(this);

        world.setContactListener(new WorldContactListener().init(this));
        GameOne.jukeBox.play("over_world_music", true);
    }

    @Override
    public void show() {

    }
    public void update(float delta) {
        controller.update(delta);
        world.step(1/60f, 6, 2);

        renderer.setView(this.camera);
        player.update(delta);
        if(player.b2dBody.getPosition().x > 1.9961113)
            camera.position.x = player.b2dBody.getPosition().x;

        updateWorldItems(delta);
        camera.update();
    }
    @Override
    public void render(float delta) {
        update(delta);

        ScreenUtils.clear(0.258f, 0.258f, 0.258f, 1);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        renderer.render(new int[] {0, 1, 2, 3, 4, 5, 6});
//        b2dRenderer.render(world, camera.combined);

        drawWorldItems(delta);

        if(player.isInvulnerable()) {
            if(GameOne.frame % 6 == 0)
                player.draw(batch, 0.7f);
        } else
            player.draw(batch);

//        debug();

        batch.end();
        renderer.render(new int[] {7});


    }

    @Override
    public void resize(int width, int height) {

        this.viewport.update(width, height);

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        this.renderer.dispose();
        this.world.dispose();
        this.b2dRenderer.dispose();
        TextureGrabber.dispose();
    }
    public void setRenderer(OrthogonalTiledMapRenderer renderer) {
        this.renderer = renderer;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }
    public void setB2dRenderer(Box2DDebugRenderer b2dRenderer) {
        this.b2dRenderer = b2dRenderer;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Set<Brick> getBricks() {
        return bricks;
    }

    public Set<Collectable> getCollectables() {
        return collectables;
    }
    private void initAtlasMap() {
        HashMap<String, TextureAtlas> atlasMap = new HashMap<>();
        atlasMap.put("tiles", new TextureAtlas("graphics_packed/tiles/mario/mario_textures.atlas"));
        atlasMap.put("items-npcs", new TextureAtlas("graphics_packed/items-npcs/items-npcs_textures.atlas"));
        atlasMap.put("enemies", new TextureAtlas("graphics_packed/enemies/enemies_textures.atlas"));
        TextureGrabber.init(atlasMap);
    }
    private void updateWorldItems(float delta) {
        for(Brick b : bricks)
            b.update();
        itemHandler.update();
        for(Collectable c : collectables)
            c.update(delta);
        for(NonCollectable nC : nonCollectables)
            nC.update(delta);
        for(Enemy e : enemies)
            e.update(delta);
        for(Fixture f : WorldGenerator.oneWayPlatforms) {
            ( (WalkableTerrain) (f.getUserData()) ).collidableWithPlayer = (f.getBody().getWorldCenter().y < player.b2dBody.getWorldCenter().y - (player.fixture.getShape().getRadius()));
        }
    }
    private void drawWorldItems(float delta) {
        for(Collectable c : collectables)
            if(c.isActive())
                c.draw(batch);
        for(Enemy e : enemies)
            if(e.isActive())
                e.draw(batch);
        for(NonCollectable nC : nonCollectables)
            nC.draw(batch);
    }
    private void debug() {
        batch.draw(TextureGrabber.grab("mushroom"),
                player.b2dBody.getWorldCenter().x - player.fixture.getShape().getRadius(),
                player.b2dBody.getWorldCenter().y - player.fixture.getShape().getRadius(),
                player.getRegionWidth() / PPM,
                player.getRegionHeight() / PPM);

        batch.draw(TextureGrabber.grab("mushroom"),
                ( (Goomba) (enemies.toArray()[0]) ).getB2dBody().getWorldCenter().x,
                ( (Goomba) (enemies.toArray()[0]) ).getB2dBody().getWorldCenter().y,
                player.getRegionWidth() / PPM,
                player.getRegionHeight() / PPM);
    }

    public Set<Enemy> getEnemies() {
        return enemies;
    }

    public Set<NonCollectable> getNonCollectables() {
        return nonCollectables;
    }
}
