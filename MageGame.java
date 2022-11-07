package magegame;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class MageGame extends Canvas implements KeyListener, Runnable { // extende o Canvas do java já na classe

    // game variables
    private static final int WIDTH = 800, HEIGHT = 500;
    private String game = "running";
    protected Player jogador = new Player();
    protected Soldier[] soldado = new Soldier[50];
    private int countdownSoldier = 0;
    protected Sniper[] atirador = new Sniper[10];
    protected Projectiles[] projetil = new Projectiles[200];
    private int countdownArcher = 0;
    private int enemyDeathCount = 0;
    private int rand = 1;
    protected Ground terreno = new Ground();
    // controls variables
    protected boolean[] keyBindings = new boolean[8];
    protected boolean attack = false;
    protected boolean stopmotion = false;
    // images
    private int imgSize = 3;
    private int yDaSetinha = 280, posicaoDaSetinha = 1;
    private int tempoDaSetinha = 0;
    private BufferedImage bg;
    private BufferedImage setinha;

    public MageGame() { // esse é o metodo construtor, que sempre recebe o mesmo nome da classe em que está

        Dimension dimension = new Dimension(WIDTH, HEIGHT); // nao sei a necessidade disso
        this.setPreferredSize(dimension); // define o tamanho

        addKeyListener(this); // para as teclas funcionarem na tela criada
        setFocusable(true); // o que faz? não sei
        setFocusTraversalKeysEnabled(false); // também não sei 

        try {
            bg = ImageIO.read(getClass().getResourceAsStream("bg.png"));
            setinha = ImageIO.read(getClass().getResourceAsStream("setinha.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        if (game == "paused") {

        } else if (game == "running") {
            if (jogador.life < 0) {
                game = "over";
                return;
            }
            initPlayer();

            // GERADOR DE SOLDADOS
            int cont = 0;
            if (countdownSoldier >= 180) {
                while (soldado[cont] != null) {
                    cont++;
                }
                if (cont < 5) {
                    soldado[cont] = new Soldier();
                    if (rand == 1) {
                        soldado[cont].x = 20;
                        rand = 0;
                    } else {
                        soldado[cont].x = 700;
                        rand = 1;
                    }
                }

                countdownSoldier = 0;
            }
            countdownSoldier++;

            // (não só) MOVIMENTADOR DE SOLDADOS (mas sim tudo memo)
            for (int i = 0; i < soldado.length; i++) {

                if (soldado[i] != null) {

                    initSoldier(i);

                    if (soldado[i].atkPossibleIfPlayerOnArea(jogador.hit_x, jogador.hit_y, jogador.hit_size_w, jogador.hit_size_h)) {

                        soldado[i].stance = "attacking";

                        if (soldado[i].attackOngoing && !jogador.imune) {
                            jogador.life -= soldado[i].damage;
                        }

                    }

                    if (soldado[i].onPlayerReach(jogador.atk_x, jogador.atk_y, jogador.atk_width, jogador.atk_height) && jogador.attackOngoing) {

                        soldado[i].stance = "hited";
                        soldado[i].attackOngoing = false;
                        soldado[i].life -= jogador.damage;

                        if (soldado[i].life < 1) {
                            enemyDeathCount++;
                            soldado[i] = null;
                        }

                    }
                }
            }

            // GERADOR DE ATIRADORES
            cont = 0;
            if (countdownArcher >= 10) {
                while (atirador[cont] != null) {
                    cont++;
                }
                if (cont < 2) {
                    atirador[cont] = new Sniper();
                    if (rand == 1) {
                        atirador[cont].x = -50;
                        rand = 0;
                    } else {
                        atirador[cont].x = 850;
                        rand = 1;
                    }
                }

                countdownArcher = 0;
            }
            countdownArcher++;

            for (int i = 0; i < atirador.length; i++) {

                if (atirador[i] != null) {
                    initArcher(i);

                    if (atirador[i].playerOnSight(jogador.hit_x, jogador.hit_y, jogador.hit_size_w)) {

                        for (int p = 0; p < 20; p++) {
                            if (projetil[p] == null && atirador[i].projectileDelay == 0) {
                                projetil[p] = new Projectiles();
                                atirador[i].projectileDelay = 120;
                                projetil[p].initialProparties(atirador[i].x, atirador[i].y, atirador[i].w, atirador[i].direction, atirador[i].damage);
                            }
                        }
                    }

                    if (atirador[i].onPlayerReach(jogador.atk_x, jogador.atk_y, jogador.atk_width, jogador.atk_height) && jogador.attackOngoing) {
                        atirador[i].life -= jogador.damage;
                        atirador[i].projectileDelay += 1;

                        if (atirador[i].life < 0) {
                            atirador[i] = null;
                        }
                    }
                }
            }

//            ATUALIZADOR DE PROJETEIS
            for (int i = 0; i < projetil.length; i++) {
                if (projetil[i] != null) {
                    projetil[i].moveProjectile();

                    if (projetil[i].colisionWithPlayer(jogador.hit_x, jogador.hit_y, jogador.hit_size_w, jogador.hit_size_h)) {
                        if (!jogador.imune) {
                            jogador.life -= projetil[i].damage;
                        }
                    }

                    if (projetil[i].outOfBox(0, WIDTH) || (projetil[i].colisionWithPlayer(jogador.hit_x, jogador.hit_y, jogador.hit_size_w, jogador.hit_size_h) && !jogador.imune)) {
                        projetil[i] = null;
                    };

                }
            }
            for (int i = 0; i < 10; i++) {
                if (atirador[i] != null) {
                    if (atirador[i].projectileDelay > 0) {
                        atirador[i].projectileDelay--;
                    }
                }
            }

        } else if (game == "over") {
            deathUiControls();
            instanceOfSetinha();
        }
    }

    public void render() {
        BufferStrategy bs = this.getBufferStrategy();

        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        g.setFont(new Font("Serif", Font.BOLD, 30));

        if (game == "menu") {

            g.setColor(Color.gray);
            g.fillRect(0, 0, WIDTH, HEIGHT);
            g.setColor(Color.white);
            g.drawString("LAST MAGE", 30, HEIGHT / 2 - 120);
            g.setFont(new Font("Serif", Font.BOLD, 20));
            g.drawString("Start Game", 30, (HEIGHT / 2) - 90);
            g.drawString("Exit", 30, (HEIGHT / 2) - 60);

            jogador.getNilaMenuLoop();
            g.drawImage(jogador.infiniteRun, (WIDTH / 2) - ((jogador.infiniteRun.getWidth() * 15) / 2), HEIGHT - jogador.infiniteRun.getHeight() * 15, jogador.infiniteRun.getWidth() * 15, jogador.infiniteRun.getHeight() * 15, null);

        } else if (game == "paused") {

////            PAUSED SCREEN
            g.setColor(Color.black);
            g.fillRect(0, 150, WIDTH, 200);

            g.setColor(Color.white);
            g.drawString("GAME PAUSED", 290, 265);

        } else if (game == "running") {

////            BACKGROUND
            g.setColor(Color.gray);
            g.fillRect(0, 0, WIDTH, HEIGHT);
//            g.drawImage(bg, 0, 0, null);

////            Interface
            g.setColor(Color.gray);
            g.fillRect(40, 20, 251, 20);
            if (jogador.life > 100) {
                g.setColor(Color.green);
            } else {
                g.setColor(Color.red);
            }
            g.fillRect(39, 19, jogador.life / 2, 19);
            g.setColor(Color.black);

            g.setFont(new Font("Serif", Font.BOLD, 15));
            g.drawString("nila", 45, 35);

            g.setFont(new Font("Serif", Font.BOLD, 20));
            g.drawString(Integer.toString(enemyDeathCount), 45, 60);

////            ENEMY SOLDIER
            for (int i = 0; i < soldado.length; i++) {
                if (soldado[i] != null) {
                    g.setColor(Color.black);
                    g.drawRect(soldado[i].x, soldado[i].y - 5, 50, 3);
                    g.setColor(Color.red);
                    g.fillRect(soldado[i].x, soldado[i].y - 5, soldado[i].life / 4, 3);
                    g.drawImage(soldado[i].currentSoldier, soldado[i].x - (soldado[i].offcenter * imgSize), soldado[i].y, soldado[i].currentSoldier.getWidth() * imgSize, soldado[i].currentSoldier.getHeight() * imgSize, null);
                }
            }

//            ENEMY ARCHER
            for (int i = 0; i < atirador.length; i++) {
                if (atirador[i] != null) {
                    // debug boxes
//                    g.setColor(Color.black);
//                    g.fillRect(atirador[i].x, atirador[i].y, atirador[i].w, atirador[i].h);
//                    g.setColor(Color.red);
//                    g.drawRect(atirador[i].hit_x - 50, atirador[i].hit_y, atirador[i].hit_size_w + 100, atirador[i].hit_size_h);
//                    g.drawRect(atirador[i].hit_x - 500, atirador[i].hit_y - 200, atirador[i].hit_size_w + 1000, atirador[i].hit_size_h + 200);
//                    g.setColor(Color.white);
//                    g.drawRect(atirador[i].hit_x, atirador[i].hit_y, atirador[i].hit_size_w, atirador[i].hit_size_h);
                    g.setColor(Color.black);
                    g.fillRect(atirador[i].x, atirador[i].y - 5, atirador[i].life / 3, 3);
                    g.setColor(Color.red);
                    g.fillRect(atirador[i].x, atirador[i].y - 6, atirador[i].life / 3, 3);
                    g.drawImage(atirador[i].currentSniper, atirador[i].x + atirador[i].w, atirador[i].y, (atirador[i].currentSniper.getWidth() * imgSize) * -1, atirador[i].currentSniper.getHeight() * imgSize, null);
                }
            }

////            PLAYER
            g.drawImage(jogador.currentNila, jogador.x, jogador.y, jogador.currentNila.getWidth() * imgSize, jogador.currentNila.getHeight() * imgSize, null);
            g.drawRect(jogador.hit_x, jogador.hit_y, jogador.hit_size_w, jogador.hit_size_h);

            for (int i = 0; i < 20; i++) {
                if (projetil[i] != null) {
                    g.setColor(Color.blue);
                    g.fillRect(projetil[i].x, projetil[i].y, projetil[i].w, projetil[i].h);
                }
            }

        } else if (game == "over") {

////            OVER SCREEN
            g.setColor(Color.black);
            g.fillRect(0, 0, WIDTH, HEIGHT);
            g.setColor(Color.red);
            g.drawString("YOU DIED", 330, 250);
            g.drawImage(setinha, 280, yDaSetinha, 15 * 2, 15 * 2, null);

////            OPTIONS
            g.setColor(Color.white);
            g.setFont(new Font("Serif", Font.TRUETYPE_FONT, 20));
            g.drawString("Retry", 385, 310);
            g.drawString("Return to Main Menu", 325, 340);
            g.drawString("Exit Game", 367, 370);

        }

        // Renderiza tudo acima
        bs.show();
    }

    public static void main(String[] args) {
        MageGame jogo = new MageGame();
        JFrame frame = new JFrame("LAST MAGE"); // para a tela do jogo

        frame.add(jogo);
//        frame.setLocationRelativeTo(null); // muda a posição inicial do frame
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((int) d.getWidth() / 2 - (int) WIDTH / 2, (int) d.getHeight() / 2 - (int) HEIGHT / 2);
        frame.pack(); // sei lá o q faz
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // permite que a aplicação seja fechada
        frame.setResizable(false); // impede que o frame seja redimensionado
        frame.setVisible(true); // para a frame ser visivel

        new Thread(jogo).start();
    }

    @Override
    public void run() {
        while (true) {
            render();
            update();
            try {
                Thread.sleep(1000 / 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) { // ativa quando a tecla é acionada. Recebe KeyChar, char output
    }

    @Override
    public void keyPressed(KeyEvent e) { // ativa quando uma tecla fisica é apertada. Recebe KeyCode, int outputs
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP: // pulo
                keyBindings[0] = true;
                break;

            case KeyEvent.VK_DOWN: // baixo 
                keyBindings[1] = true;
                break;

            case KeyEvent.VK_LEFT: // esquerda
                keyBindings[2] = true;
                break;

            case KeyEvent.VK_RIGHT: // direita
                keyBindings[3] = true;
                break;

            case KeyEvent.VK_C: // ataque leve
                keyBindings[4] = true;
                break;

            case KeyEvent.VK_Z: // esquiva
                keyBindings[5] = true;
                break;

            case KeyEvent.VK_ESCAPE:
                if (game == "running") {
                    game = "paused";
                } else if (game == "paused") {
                    if (jogador.life < 0) {
                        game = "over";
                    } else {
                        game = "running";
                    }
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { // só é ativa quando a tecla é solta
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP: // pulo
                keyBindings[0] = false;
                break;

            case KeyEvent.VK_DOWN: // baixo
                keyBindings[1] = false;
                break;

            case KeyEvent.VK_LEFT: // esquerda
                keyBindings[2] = false;
                break;

            case KeyEvent.VK_RIGHT: // direita
                keyBindings[3] = false;
                break;

            case KeyEvent.VK_C: // ataque leve
                keyBindings[4] = false;
                break;

            case KeyEvent.VK_Z: // esquiva
                keyBindings[5] = false;
                break;

            default:
                break;
        }
    }
    int contador = 0;

    public void initPlayer() {
        refactorPlayerInCanvas();
        jogador.controlInputs(keyBindings[0], keyBindings[1], keyBindings[2], keyBindings[3], keyBindings[4], keyBindings[5]);
        jogador.spriteDetector();
        jogador.attackBoxPosition(jogador.x, jogador.y, jogador.w, jogador.direction);
    }

    public void initSoldier(int i) {
        soldado[i].movement(jogador.x, soldado[i].playerOnSight(jogador.x, jogador.y, jogador.w));
        soldado[i].y = 400 - soldado[i].h;
        soldado[i].updateLifeBox();
        soldado[i].attackBoxPosition(soldado[i].x, soldado[i].y, soldado[i].w, soldado[i].direction);
        soldado[i].spriteDetector();
    }

    public void initArcher(int i) {
        atirador[i].movement(jogador.hit_x, atirador[i].playerOnSight(jogador.x, jogador.y, jogador.w));
        atirador[i].updateLifeBox();
        atirador[i].spriteDetector();
    }

    public void refactorPlayerInCanvas() {
        if (jogador.y < 0) {
            jogador.y = 0;
        }
        if (jogador.y > HEIGHT - jogador.h) {
            jogador.y = HEIGHT - jogador.h;
        }
        if (jogador.x < 0 - 20) {
            jogador.x = - 20;
        }
        if (jogador.x > WIDTH - (jogador.w - 20)) {
            jogador.x = WIDTH - (jogador.w - 20);
        }
    }

    public void deathUiControls() {

        if (keyBindings[4]) {
            switch (posicaoDaSetinha) {
                case 1:
                    jogador.life = 500;
                    jogador.x = 400 - (jogador.w / 2);
                    jogador.y = 150;
                    jogador.constY = 0;
                    jogador.stance = "standing";
                    jogador.stop = false;
                    enemyDeathCount = 0;

                    for (int i = 0; i < soldado.length; i++) {
                        if (soldado[i] != null) {
                            soldado[i] = null;
                        }
                    }
                    game = "running";
                    break;
                case 2:
                    game = "menu";
                    break;
                case 3:
                    System.exit(0);
                    break;
                default:
                    break;
            }
        }

        if (keyBindings[0] && keyBindings[1]) {
            return;
        }

        tempoDaSetinha--;

        if (tempoDaSetinha > 0) {
            return;
        }

        if (keyBindings[0]) {
            posicaoDaSetinha -= 1;
            tempoDaSetinha = 10;
        }
        if (keyBindings[1]) {
            posicaoDaSetinha += 1;
            tempoDaSetinha = 10;
        }

        if (posicaoDaSetinha > 3) {
            posicaoDaSetinha = 1;
        } else if (posicaoDaSetinha < 1) {
            posicaoDaSetinha = 3;
        }
    }

    public void instanceOfSetinha() {
        switch (posicaoDaSetinha) {
            case 1:
                yDaSetinha = 280;
                break;
            case 2:
                yDaSetinha = 310;
                break;
            case 3:
                yDaSetinha = 340;
                break;
        }
    }
}
