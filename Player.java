package magegame;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Player extends GlobalProperties {

    int kframe = 1;

//    sprites
    private BufferedImage nilaStanding;
    private BufferedImage nilaJumpOrCrouch;
    private BufferedImage nilaRun;
    private BufferedImage nilaFire;
    private BufferedImage nilaRoll;
//    sprites - true sprite
    public BufferedImage currentNila;
    public BufferedImage infiniteRun;
//    frame update

    public void hitbox() {
        this.hit_x = this.x + 21;
        this.hit_y = this.y + 21;
        this.hit_size_w = 30;
        this.hit_size_h = 30;
    }

    public void attackBoxPosition(int x, int y, int width, String dir) {
        this.atk_height = 48;
        this.atk_y = y;

        if (dir == "right") {
            this.atk_x = (x + (width / 2) + 6);
        } else if (dir == "left") {
            this.atk_x = x;
        }

        this.atk_width = 30;
    }

    public void correctPlayerStatus() {
//        gravity
        if (this.constY < -20) {
            this.constY = -20;
        }
        this.constY -= this.fallY;
        this.y -= Math.round(this.constY);
        if (Ground.onGround(this.y, this.h)) {
            this.y = 400 - this.h;
        }

//        velocity
        if (this.velX > 0.5f) {
            this.velX -= 0.8f;
        } else if (this.velX < -0.5f) {
            this.velX += 0.8f;
        }
        this.x += Math.round(this.velX);

//        animation
        this.stance = "standing";
        if (!Ground.onGround(this.y, this.h)) {
            this.stance = "jumping";
        }

//        attack and dodge
        this.attackOngoing = false;
        this.imune = false;

        if (this.delay > 0) {
            this.delay--;
        }
        if (this.dodgeDelay > 0) {
            this.dodgeDelay--;
        }
    }

    public void controlInputs(boolean up, boolean down, boolean left, boolean right, boolean c, boolean z) {
        hitbox();
        if (this.stop) {
            return;
        }

        correctPlayerStatus();
        
        if (left && !right) {
            this.direction = "left";
        } else if (right && !left) {
            this.direction = "right";
        }

        if (down) {

            if (!Ground.onGround(this.y, this.h)) {
                this.constY -= 2;
            } else {
                this.hit_y = this.y + 42;
                this.stance = "crouch";
            }
            
            if (left && !right) {
                this.velX = -3.8f;

            } else if (right && !left) {
                this.velX = 3.8f;
            }

        } else if (up) {

            this.stance = "jumping";

            if (Ground.onGround(this.y, this.h)) {
                this.constY = this.velY;
            }

            if (left && !right) {
                this.velX = -6;

            } else if (right && !left) {
                this.velX = 6;

            }

        } else if (z && dodgeDelay <= 0) {

            this.stance = "dodge";
            this.stop = true;
            this.dodgeDelay = 40;
            this.imune = true;

        } else if (c && delay <= 0) {

            this.stance = "fire";
            this.attackOngoing = true;
            this.stop = true;
            this.delay = 15;
            this.velX = 0;

        } else if (left && right) {

            this.stance = "pose";

        } else if (left || right) {

            if (left) {
                this.direction = "left";
                this.velX = -6;

            } else if (right) {
                this.direction = "right";
                this.velX = 6;

            }
            if (Ground.onGround(this.y, this.h)) {
                this.stance = "running";
            }

        }
    }

    public Player() {

        // atributos do player
        this.x = 400 - 36;
        this.y = 50;
        this.velX = 0;

        this.life = 500;
        this.damage = 2;

        // frame update
        this.direction = "right";
        this.stance = "standing";
        this.stop = false;
        this.time = 0;
        this.frame = 1;
        // construtor de sprites
        try { // making a better alternative... incoming.
            
            nilaJumpOrCrouch = ImageIO.read(getClass().getResourceAsStream("nila/nila2.2jump.png"));
            nilaRun = ImageIO.read(getClass().getResourceAsStream("nila/Nila2.2run.png"));
            nilaStanding = ImageIO.read(getClass().getResourceAsStream("nila/Nila2.2standing.png"));
            nilaFire = ImageIO.read(getClass().getResourceAsStream("nila/Nila2.2fire.png"));
            nilaRoll = ImageIO.read(getClass().getResourceAsStream("nila/Nila2.2roll.png"));
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getNilaMenuLoop() {
        this.time++;
        if (this.time > 6) {
            this.frame++;
            this.time = 0;
        }
        if (this.frame > 8) {
            this.frame = 1;
        }
        infiniteRun = nilaRun.getSubimage((this.frame * 24) - 24, (1 * 24) - 24, 24, 24);
    }

    public void spriteDetector() {
        switch (this.stance) {
//             pretty obvious
            case "standing":
                if (this.direction == "right") {
                    currentNila = nilaStanding.getSubimage((1 * 24) - 24, (1 * 24) - 24, 24, 24);
                } else if (this.direction == "left") {
                    currentNila = nilaStanding.getSubimage((2 * 24) - 24, (1 * 24) - 24, 24, 24);
                }
                break;

//                nothing new...
            case "running":
                this.time++;
                if (this.time > 6) {
                    this.frame++;
                    this.time = 0;
                }
                if (this.frame > 8) {
                    this.frame = 1;
                }
                if (direction == "right") {
                    currentNila = nilaRun.getSubimage((this.frame * 24) - 24, (1 * 24) - 24, 24, 24);
                } else if (direction == "left") {
                    currentNila = nilaRun.getSubimage((this.frame * 24) - 24, (2 * 24) - 24, 24, 24);
                }
                break;

//                kind complex.
            case "jumping":
                if (this.constY > 0) { // going up
                    if (this.direction == "right") {
                        currentNila = nilaJumpOrCrouch.getSubimage((1 * 24) - 24, (2 * 24) - 24, 24, 24);
                    } else if (this.direction == "left") {
                        currentNila = nilaJumpOrCrouch.getSubimage((1 * 24) - 24, (1 * 24) - 24, 24, 24);
                    }
                } else { // falling
                    if (this.direction == "right") {
                        currentNila = nilaJumpOrCrouch.getSubimage((2 * 24) - 24, (2 * 24) - 24, 24, 24);
                    } else if (this.direction == "left") {
                        currentNila = nilaJumpOrCrouch.getSubimage((2 * 24) - 24, (1 * 24) - 24, 24, 24);
                    }
                }
                break;

//                you need an explanation...?
            case "fire":
                this.time++;
                if (this.time > 3) {
                    kframe++;
                    this.time = 0;
                }

                if (kframe > 13) { // this is for reseting the frame, for when you call the fire attack will always initialized on frame 0
                    kframe = 1;
                    this.stop = false; // to get back
                    return;
                }
                if (this.direction == "right") {
                    currentNila = nilaFire.getSubimage((kframe * 24) - 24, (1 * 24) - 24, 24, 24);
                } else if (this.direction == "left") {
                    currentNila = nilaFire.getSubimage((kframe * 24) - 24, (2 * 24) - 24, 24, 24);
                }
                break;

//                
            case "crouch":
                if (this.direction == "right") {
                    currentNila = nilaJumpOrCrouch.getSubimage(0, (3 * 24) - 24, 24, 24);
                } else if (this.direction == "left") {
                    currentNila = nilaJumpOrCrouch.getSubimage(24, (3 * 24) - 24, 24, 24);
                }
                break;

//                
            case "pose":
                if (this.direction == "right") {
                    currentNila = nilaStanding.getSubimage((1 * 24) - 24, (2 * 24) - 24, 24, 24);
                } else if (this.direction == "left") {
                    currentNila = nilaStanding.getSubimage((2 * 24) - 24, (2 * 24) - 24, 24, 24);
                }
                break;

//                
            case "dodge":
                this.time++;
                if (this.time > 1) {
                    frame++;
                    this.time = 0;
                }

                if (direction == "right") {
                    this.x += 8;
                } else if (direction == "left") {
                    this.x -= 8;
                }

                if (this.frame > 3) {
                    this.kframe++;
                    this.frame = 1;
                }

                if (this.kframe > 4) {
                    this.kframe = 1;
                    this.stop = false; // to get back
                    return;
                }
                if (direction == "right") {
                    currentNila = nilaRoll.getSubimage((this.kframe * 24) - 24, (1 * 24) - 24, 24, 24);
                } else if (direction == "left") {
                    currentNila = nilaRoll.getSubimage((this.kframe * 24) - 24, (2 * 24) - 24, 24, 24);
                }
                break;
        }
    }
}
