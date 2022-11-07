package magegame;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Sniper extends GlobalProperties {

    private BufferedImage sniperBase;
    public BufferedImage currentSniper;
    public int projectileDelay = 120;
    
    public void updateLifeBox() {
        this.hit_x = this.x + 33;
        this.hit_y = this.y;
        this.hit_size_w = 30;
        this.hit_size_h = 72;
    }

    public boolean playerTooClose(int x, int y, int width) {
        if ((this.hit_x + this.hit_size_w + 50 > x && this.hit_x - 50 < x + width) && (this.hit_y + 200 > y && this.y - 200 < y)) {
            return true;
        }
        return false;
    }

    public boolean playerOnSight(int x, int y, int width) {
        if ((this.hit_x + this.hit_size_w + 400 > x && this.hit_x - 400 < x + width) && (this.hit_y + 200 > y && this.y - 200 < y)) {
            return true;
        }
        return false;
    }
    
    public boolean onPlayerReach(int hit_x, int hit_y, int hit_width, int hit_height) {
        if ((this.hit_x + this.hit_size_w > hit_x && this.hit_x < hit_x + hit_width) && (this.hit_y + this.hit_size_h > hit_y && this.hit_y < hit_y + hit_height)) {
            return true;
        }
        return false;
    }

    public Sniper() {
        this.w = 96;
        this.h = 72;
        this.y = 400 - h;

        this.life = 2;
        this.damage = 20;

        this.stance = "standing";
        this.direction = "right";
        this.frame = 1;
        
        try {
            
            sniperBase = ImageIO.read(getClass().getResourceAsStream("sniper/SniperBase.png"));
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void correctStats() {

        if (this.velX > 0.5) {
            this.velX -= 0.8f;
        } else if (this.velX < -0.5) {
            this.velX += 0.8f;
        } else {
            this.velX = 0;
        }
        this.x += this.velX;

        if (this.velX > 0) {
            this.direction = "right";
        } else if (this.velX < 0) {
            this.direction = "left";
        }
    }

    public void movement(int targetX, boolean sight) {

        correctStats();
        
        if (targetX > this.hit_x + this.hit_size_w) {
            this.velX = 2;
        } else if (targetX + 32 < this.hit_x) {
            this.velX = -2;
        }
    }

    public void spriteDetector() {
        currentSniper = sniperBase;
    }
}
