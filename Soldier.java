package magegame;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Soldier extends GlobalProperties {

    // sprites - walking
    private BufferedImage[] walkingRight = new BufferedImage[10];
    private BufferedImage[] walkingLeft = new BufferedImage[10];
    // sprites - taking damage
    private BufferedImage[] takingDmg = new BufferedImage[4];
    // sprites - attacking
    private BufferedImage atkSprite;
    // sprites - true sprite
    public BufferedImage currentSoldier = walkingRight[0];

    int cont = 0;
    int instance = 0;

    public boolean onPlayerReach(int hit_x, int hit_y, int hit_width, int hit_height) {
//        VERIFY IF THE SOLDIER IS ON PLAYER'S ATK ZONE
        if ((this.hit_x + this.hit_size_w > hit_x && this.hit_x < hit_x + hit_width) && (this.hit_y + this.hit_size_h > hit_y && this.hit_y < hit_y + hit_height)) {
            return true;
        }
        return false;
    }

    public boolean atkPossibleIfPlayerOnArea(int x, int y, int width, int height) {
//        MAKES POSSIBLE TO ATTACK WHEN PLAYER REACH THIS AREA
        if ((this.atk_x + this.atk_width > x && this.atk_x < x + width) && (this.atk_y + this.atk_height > y && this.atk_y < y + height)) {
            return true;
        }
        return false;
    }

    public boolean playerOnSight(int x, int y, int width) {
//        WALK WHEN THE PLAYER IS ON THIS RANGE EFFECTIVENESS
        if ((this.hit_x + this.hit_size_w + 500 > x && this.hit_x - 500 < x + width) && (this.hit_y + 200 > y && this.y - 200 < y)) {
            return true;
        }
        return false;
    }

    public void updateLifeBox() {
//        ADAPTS THE HITBOX AREA OF THE SOLDIER
        this.hit_x = this.x + 21;
        this.hit_y = this.y;
        this.hit_size_w = 30;
        this.hit_size_h = 72;
    }

    public void attackBoxPosition(int x, int y, int width, String dir) {
//        ADAPTS THE POSITION OF THE ATK BOX 
        this.atk_height = 72;
        this.atk_y = y; // the atk height is equals to the soldier's height

        if (dir == "right") {
            this.atk_x = (x + (width / 2)); // when facing to the right direction
        } else if (dir == "left") {
            this.atk_x = x - 9; // when facing to the left direction
        }

        this.atk_width = 45; // defines the size of the area of the attack
    }

    int offcenter = 0;

    public Soldier() {
        this.y = 400 - h;

        this.life = 200;
        this.damage = 4;

        this.stance = "standing";
        this.direction = "right";
        this.frame = 0;
        try {
            atkSprite = ImageIO.read(getClass().getResourceAsStream("soldier/soldier(cutL).png"));
            takingDmg[0] = ImageIO.read(getClass().getResourceAsStream("soldier/soldierDMG0.png"));
            takingDmg[1] = ImageIO.read(getClass().getResourceAsStream("soldier/soldierDMG1.png"));
            takingDmg[2] = ImageIO.read(getClass().getResourceAsStream("soldier/soldierDMG2.png"));
            takingDmg[3] = ImageIO.read(getClass().getResourceAsStream("soldier/soldierDMG3.png"));
            walkingRight[0] = ImageIO.read(getClass().getResourceAsStream("soldier/soldierR00.png"));
            walkingRight[1] = ImageIO.read(getClass().getResourceAsStream("soldier/soldierR01.png"));
            walkingRight[2] = ImageIO.read(getClass().getResourceAsStream("soldier/soldierR02.png"));
            walkingRight[3] = ImageIO.read(getClass().getResourceAsStream("soldier/soldierR03.png"));
            walkingRight[4] = ImageIO.read(getClass().getResourceAsStream("soldier/soldierR04.png"));
            walkingRight[5] = ImageIO.read(getClass().getResourceAsStream("soldier/soldierR05.png"));
            walkingRight[6] = ImageIO.read(getClass().getResourceAsStream("soldier/soldierR06.png"));
            walkingRight[7] = ImageIO.read(getClass().getResourceAsStream("soldier/soldierR07.png"));
            walkingRight[8] = ImageIO.read(getClass().getResourceAsStream("soldier/soldierR08.png"));
            walkingRight[9] = ImageIO.read(getClass().getResourceAsStream("soldier/soldierR09.png"));
            walkingLeft[0] = ImageIO.read(getClass().getResourceAsStream("soldier/soldierL00.png"));
            walkingLeft[1] = ImageIO.read(getClass().getResourceAsStream("soldier/soldierL01.png"));
            walkingLeft[2] = ImageIO.read(getClass().getResourceAsStream("soldier/soldierL02.png"));
            walkingLeft[3] = ImageIO.read(getClass().getResourceAsStream("soldier/soldierL03.png"));
            walkingLeft[4] = ImageIO.read(getClass().getResourceAsStream("soldier/soldierL04.png"));
            walkingLeft[5] = ImageIO.read(getClass().getResourceAsStream("soldier/soldierL05.png"));
            walkingLeft[6] = ImageIO.read(getClass().getResourceAsStream("soldier/soldierL06.png"));
            walkingLeft[7] = ImageIO.read(getClass().getResourceAsStream("soldier/soldierL07.png"));
            walkingLeft[8] = ImageIO.read(getClass().getResourceAsStream("soldier/soldierL08.png"));
            walkingLeft[9] = ImageIO.read(getClass().getResourceAsStream("soldier/soldierL09.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void movement(int targetX, boolean sight) {
        if (this.velX > 0) {
            this.velX -= 0.6f;
        }
        if (this.velX < 0) {
            this.velX += 0.6f;
        }
        if (!sight) {
            this.stance = "standing";
            return;
        }
        if (this.stance == "standing" || this.stance == "walking") {
            if (this.x < targetX - 15 || this.x > targetX + 15) {
                this.velX = 2;
                if (this.x > targetX + 15) {
                    this.velX *= -1;
                    this.direction = "left";
                } else if (this.x < targetX - 15) {
                    this.direction = "right";
                }
                this.stance = "walking";
            }
        } else if (this.stance == "attacking") {
            this.attackOngoing = false;
            this.time++;
            if (this.time > 80) {
                this.time = 0;
                this.stance = "standing";
            }
            if (this.time > 60) {
                this.attackOngoing = true;
            }

        } else if (this.stance == "hited") {
            this.time++;
            if (this.time > 60) {
                this.time = 0;
                this.stance = "standing";
            }
        }
        this.x += Math.round(this.velX);
    }

    public void spriteDetector() {
        this.h = 72;
        offcenter = 0;
        switch (this.stance) {
            case "walking":
                this.time++;
                if (this.time > 6) {
                    this.frame++;
                    this.time = 0;
                }
                if (this.frame > 9) {
                    this.frame = 0;
                }

                if (this.direction == "right") {
                    currentSoldier = walkingRight[this.frame];
                } else if (this.direction == "left") {
                    currentSoldier = walkingLeft[this.frame];
                }
                break;

            case "standing":
                if (direction == "right") {
                    currentSoldier = walkingRight[0];
                } else if (direction == "left") {
                    currentSoldier = walkingLeft[0];
                }
                break;
            case "hited":
                this.time++;
                if (this.time > 10) {
                    this.frame++;
                    this.time = 0;
                    this.wait++;
                }
                if (this.frame > 1) {
                    this.frame = 0;
                }
                if (direction == "right") {
                    currentSoldier = takingDmg[this.frame];
                } else if (direction == "left") {
                    currentSoldier = takingDmg[this.frame + 2];
                }
                if (this.wait > 8) {
                    this.stance = "standing";
                    this.wait = 0;
                }
                break;
            case "attacking":
                this.h = 81;
                if (direction == "right") {
                    if (this.time < 60) {
                        currentSoldier = atkSprite.getSubimage(99, 1, 24, 27);
                    } else {
                        currentSoldier = atkSprite.getSubimage(143, 1, 33, 27);
                    }
                } else if (direction == "left") {
                    if (this.time < 60) {
                        currentSoldier = atkSprite.getSubimage(10, 1, 24, 27);
                    } else {
                        offcenter = 9;
                        currentSoldier = atkSprite.getSubimage(45, 1, 33, 27);
                    }
                }
        }
    }
}
