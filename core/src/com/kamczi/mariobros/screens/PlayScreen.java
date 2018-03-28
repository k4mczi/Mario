package com.kamczi.mariobros.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kamczi.mariobros.MarioBros;
import com.kamczi.mariobros.Scenes.Hud;
import com.kamczi.mariobros.Sprites.Mario;
import com.kamczi.mariobros.Tools.B2WorldCreator;

/**
 * Created by Kamil on 2018-03-23.
 */

public class PlayScreen implements Screen {

    private MarioBros game;
    private TextureAtlas atlas;

    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //box2d
    private World world;
    private Box2DDebugRenderer b2dr;

    private Mario player;

    public PlayScreen(MarioBros game){
        atlas = new TextureAtlas("Mario_and_Enemies.pack");
        this.game=game;
        gamecam = new OrthographicCamera(MarioBros.V_WIDTH,MarioBros.V_HEIGHT);
        gamePort = new FitViewport(MarioBros.V_WIDTH/MarioBros.PPM,MarioBros.V_HEIGHT/MarioBros.PPM,gamecam);
        hud = new Hud(game.batch);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1/MarioBros.PPM);
        gamecam.position.set(gamePort.getScreenWidth()/2,gamePort.getScreenHeight()/2,0);

        world = new World(new Vector2(0,-10),true);
        b2dr = new Box2DDebugRenderer();

        new B2WorldCreator(world,map);

        player = new Mario(world, this);
    }

    public TextureAtlas getAtlas(){
        return atlas;
    }
    public void handleInput(float dt){
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP))
            player.b2body.applyLinearImpulse(new Vector2(0,4f), player.b2body.getWorldCenter(), true);
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2)
            player.b2body.applyLinearImpulse(new Vector2(0.1f,0), player.b2body.getWorldCenter(),true);
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x <= -2)
            player.b2body.applyLinearImpulse(new Vector2(-0.1f,0), player.b2body.getWorldCenter(),true);
    }

    public void update(float dt){
        handleInput(dt);

        world.step(1/60f,6,2);
        player.update(dt);
        gamecam.position.x = player.b2body.getPosition().x;
        gamecam.update();
        renderer.setView(gamecam);
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        //renderer our Box2DDebugLines
        b2dr.render(world, gamecam.combined);

        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        player.draw(game.batch);
        game.batch.end();
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);

        hud.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width,height);
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
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
