package magegame;

public class Projectiles extends GlobalProperties {

    public void initialProparties(int x, int y, int w, String dir, int dmg) {
        this.x = x + (w / 2);
        this.y = y + 20;
        this.direction = dir;
        this.damage = dmg;
    }

    public void moveProjectile() {
        if (this.direction == "right") {
            this.x += this.velX;
        } else if (this.direction == "left") {
            this.x -= this.velX;
        }
    }

    public boolean outOfBox(int canvaX, int canvaWidth) {
        if (this.x > canvaWidth + 20 || this.x < canvaX - 20) {
            return true;
        }
        return false;
    }

    public boolean colisionWithPlayer(int x, int y, int width, int heght) {
        if ((this.x + this.w > x && this.x < x + width) && (this.y + this.h > y && this.y < y + h)) {
            return true;
        }
        return false;
    }

    public Projectiles() {
        this.x = 100;
        this.y = 200;
        this.w = 10;
        this.h = 10;

        this.velX = 10;
    }
}
