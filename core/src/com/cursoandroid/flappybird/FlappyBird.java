package com.cursoandroid.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture[] passaros;
    private Texture fundo;
    //atributos de configutacao
    private float larguraDispositivo;
    private float alturaDispositivo;
    private int estadoJogo = 0; //0 -> jogo não iniciado; 1 -> jogo iniciado; 2 -> game over
    private int pontuacao = 0;

    private Random numeroRandomico;

    private float variacao = 0;
    private float velocidadeQuedaPassaro = 0;
    private float posicaoInicialVertical;
    private float posicaoMovimentoCanoHorizontal;
    private float espacoEntreCanos;
    private float deltaTime;
    private float alturaEntreCanosRandomica;

    private Texture canoAlto;
    private Texture canoBaixo;
    private Texture gameOver;
    private BitmapFont font;
    private BitmapFont mensagem;
    private Circle passaroCirculo;
    private Rectangle colisaoCanoTopo;
    private Rectangle colisaoCanoBaixo;
   // private ShapeRenderer shapeRenderer;

    private boolean marcouPonto = false;

    //camera
    private OrthographicCamera camera;
    private Viewport viewport;

    private final float VIRTUAL_WIDTH = 768;
    private final float VIRTUAL_HEIGHT = 1024;
	@Override
	public void create () {
	    passaroCirculo = new Circle();
	    /*colisaoCanoBaixo = new Rectangle();
	    colisaoCanoTopo = new Rectangle();*/

	    //shapeRenderer = new ShapeRenderer();

        batch = new SpriteBatch();
        passaros = new Texture[3];

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(6);

        mensagem = new BitmapFont();
        mensagem.setColor(Color.WHITE);
        mensagem.getData().setScale(3);

        numeroRandomico = new Random();
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");
        fundo = new Texture("fundo.png");



        canoBaixo = new Texture("cano_baixo.png");
        canoAlto = new Texture("cano_topo.png");

        gameOver = new Texture("game_over.png");


        espacoEntreCanos = 300;

        larguraDispositivo = VIRTUAL_WIDTH;
        alturaDispositivo  = VIRTUAL_HEIGHT;

        /*Configurações de camera*/
        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2, 0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        posicaoInicialVertical = alturaDispositivo / 2;
        posicaoMovimentoCanoHorizontal = larguraDispositivo;
	}

	@Override
	public void render () {

	    camera.update();
	    //limpando frames e otimizando jogo
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        deltaTime = Gdx.graphics.getDeltaTime();
        variacao += deltaTime * 8;

        if (variacao > 2) variacao = 0;

	    if(estadoJogo == 0){
	        if(Gdx.input.justTouched()){
	            estadoJogo = 1;
            }

        }else {

            velocidadeQuedaPassaro++;
            if (posicaoInicialVertical > 0 || velocidadeQuedaPassaro < 0)
                posicaoInicialVertical -= velocidadeQuedaPassaro;

            if(estadoJogo == 1){
                posicaoMovimentoCanoHorizontal -= deltaTime * 200;
                //toque na tela
                if (Gdx.input.justTouched()) {
                    //Gdx.app.log("Toque", "Toque na tela");
                    velocidadeQuedaPassaro = -13;
                }

                //verifica se o cano saiu inteiramente da tela
                if (posicaoMovimentoCanoHorizontal < -canoAlto.getWidth()) {
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;
                    alturaEntreCanosRandomica = numeroRandomico.nextInt(400) - 200;
                    marcouPonto = false;
                }
                //verifica pontuacao
                if(posicaoMovimentoCanoHorizontal < 120){
                    if(!marcouPonto){
                        pontuacao++;
                        marcouPonto = true;
                    }
                }
            }else {//tela game over

                if(Gdx.input.justTouched()){
                    estadoJogo = 0;
                    pontuacao = 0;
                    velocidadeQuedaPassaro = 0;
                    posicaoInicialVertical = alturaDispositivo / 2;
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;
                }
            }
        }
        //configurar dados de projeção da câmera
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
        batch.draw(canoAlto, posicaoMovimentoCanoHorizontal, alturaDispositivo /2 + espacoEntreCanos/2 + alturaEntreCanosRandomica);
        batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos/2 +alturaEntreCanosRandomica);
        batch.draw(passaros[ (int) variacao], 120, posicaoInicialVertical/*, 110, 110*/);
        font.draw(batch, String.valueOf(pontuacao), larguraDispositivo / 2, alturaDispositivo - 50);

        if(estadoJogo == 2){
            batch.draw(gameOver, larguraDispositivo / 2 - gameOver.getWidth() / 2, alturaDispositivo / 2);
            mensagem.draw(batch, "Toque para reiiciar!", larguraDispositivo /2 - 230, alturaDispositivo/2 - gameOver.getHeight());
        }
        batch.end();

        passaroCirculo.set(120 + passaros[0].getWidth() / 2, posicaoInicialVertical + passaros[0].getHeight() / 2, passaros[0].getWidth()/2);
        colisaoCanoBaixo = new Rectangle(
                posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos/2 +alturaEntreCanosRandomica,
                canoBaixo.getWidth(), canoBaixo.getHeight()
        );
        colisaoCanoTopo = new Rectangle(
                posicaoMovimentoCanoHorizontal, alturaDispositivo /2 + espacoEntreCanos/2 + alturaEntreCanosRandomica,
                canoAlto.getWidth(), canoAlto.getHeight()
        );

        //desenhar formas
        /*shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(passaroCirculo.x, passaroCirculo.y, passaroCirculo.radius);
        shapeRenderer.rect(colisaoCanoTopo.x, colisaoCanoTopo.y, colisaoCanoTopo.width, colisaoCanoTopo.height);
        shapeRenderer.rect(colisaoCanoBaixo.x, colisaoCanoBaixo.y, colisaoCanoBaixo.width, colisaoCanoBaixo.height);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.end();*/

        //teste de colisão
        if(Intersector.overlaps(passaroCirculo, colisaoCanoBaixo) || Intersector.overlaps(passaroCirculo, colisaoCanoTopo)
        || posicaoInicialVertical <= 0 || posicaoInicialVertical >= alturaDispositivo ){
            //Gdx.app.log("Colisao", "Houve colisão");
            estadoJogo = 2;
        }
	}

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
