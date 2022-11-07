package magegame;

public class Ground extends GlobalProperties {

    public Ground() {
        this.x = 0;
        this.y = 400; // posição do chão
        this.w = 800;
        this.h = 100; // dimensão do chão
    }
    
    static boolean onGround(int y, int h) {
        if (y + h >= 400) {
            return true;
        }
        return false;
    }
}