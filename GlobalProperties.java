package magegame;

import java.util.Random;

public abstract class GlobalProperties {
    Random random = new Random();
//    posicionamento e tamanho
    protected int x = 0, y = 0; // posição
    protected int w = 72, h = 72; // tamanho
    protected int hit_x, hit_y, hit_size_w, hit_size_h; // hitbox
    protected int atk_x, atk_y, atk_width, atk_height; // attack box
//    atributos
    protected int life; // vida
    protected int damage; // ataque
    protected boolean imune; // imunidade
    protected boolean attackOngoing; // ataque em andamento
//    movimentação
    protected float velY = 10, velX = 0f; // velocidade
    protected float constY = 0, fallY = 0.5f; // gravidade
    
//    variáveis de animação
    protected String direction = "right"; // direção do olhar
    protected String stance; // animador
    protected boolean stop = false; // parada forçada
    protected int delay; // espera
    protected int dodgeDelay; // espera da esquiva
    protected int time = 0, tswitch = 1; // tempos de animação
    protected int wait; // espera
    protected int frame = 1; // imagem de exibição
}