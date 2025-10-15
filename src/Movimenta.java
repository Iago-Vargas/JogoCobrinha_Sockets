import java.util.Random;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.util.List;
import java.awt.Rectangle;

/**
 * Regras do jogo: movimento, colisão, placar e reset.
 */
public class Movimenta {

    private static int pointsP1 = 0;
    private static int pointsP2 = 0;
    private static final int MAX_POINTS = 10;

    private static boolean matchOver = false;

    private static List<JButton> obstacles;

    /** Injeta a lista de obstáculos */
    public static void setObstacles(List<JButton> obs) {
        obstacles = obs;
    }

    private static boolean collidesWithObstacle(Rectangle target) {
        if (obstacles == null) return false;
        for (JButton b : obstacles) {
            if (target.intersects(b.getBounds())) return true;
        }
        return false;
    }

    public static void moveLeft(JButton btn) {
        int x = btn.getBounds().x - 20;
        if (x < 0) return;
        Rectangle target = new Rectangle(x, btn.getBounds().y, btn.getBounds().width, btn.getBounds().height);
        if (collidesWithObstacle(target)) return;
        btn.setBounds(x, btn.getBounds().y, btn.getBounds().width, btn.getBounds().height);
    }

    public static void moveUp(JButton btn) {
        int y = btn.getBounds().y - 20;
        if (y < 0) return;
        Rectangle target = new Rectangle(btn.getBounds().x, y, btn.getBounds().width, btn.getBounds().height);
        if (collidesWithObstacle(target)) return;
        btn.setBounds(btn.getBounds().x, y, btn.getBounds().width, btn.getBounds().height);
    }

    public static void moveRight(JButton btn, int panelWidth) {
        int x = btn.getBounds().x + 20;
        if (x > panelWidth - btn.getBounds().width) return;
        Rectangle target = new Rectangle(x, btn.getBounds().y, btn.getBounds().width, btn.getBounds().height);
        if (collidesWithObstacle(target)) return;
        btn.setBounds(x, btn.getBounds().y, btn.getBounds().width, btn.getBounds().height);
    }

    public static void moveDown(JButton btn, int panelHeight) {
        int y = btn.getBounds().y + 20;
        if (y > panelHeight - btn.getBounds().height) return;
        Rectangle target = new Rectangle(btn.getBounds().x, y, btn.getBounds().width, btn.getBounds().height);
        if (collidesWithObstacle(target)) return;
        btn.setBounds(btn.getBounds().x, y, btn.getBounds().width, btn.getBounds().height);
    }

    public static boolean intersects(JButton a, JButton b) {
        return a.getBounds().intersects(b.getBounds());
    }

    public static void randomPlace(JButton btn, int width, int height) {
        Random r = new Random();
        int x = r.nextInt(Math.max(1, width - btn.getBounds().width));
        int y = r.nextInt(Math.max(1, height - btn.getBounds().height));
        btn.setBounds(x, y, btn.getBounds().width, btn.getBounds().height);
    }

    public static void randomizeObstacles(List<JButton> obs, int width, int height) {
        if (obs == null) return;
        Random r = new Random();
        for (JButton b : obs) {
            int x = r.nextInt(Math.max(1, width - b.getBounds().width));
            int y = r.nextInt(Math.max(1, height - b.getBounds().height));
            b.setBounds(x, y, b.getBounds().width, b.getBounds().height);
        }
    }

    public static void addPoint(int player, JTextField scoreField) {
        if (matchOver) return;

        if (player == 1) pointsP1++;
        else if (player == 2) pointsP2++;

        renderScore(scoreField);

        if (pointsP1 >= MAX_POINTS || pointsP2 >= MAX_POINTS) {
            matchOver = true;
            String winner = (pointsP1 > pointsP2) ? "Jogador 1" : "Jogador 2";
            scoreField.setText(winner + " VENCEU! (" + pointsP1 + "x" + pointsP2 + ")");
        }
    }

    public static void renderScore(JTextField scoreField) {
        scoreField.setText(pointsP1 + " X " + pointsP2);
    }

    public static void resetMatch(JTextField scoreField, JButton fruitBtn, int width, int height) {
        pointsP1 = 0;
        pointsP2 = 0;
        matchOver = false;
        renderScore(scoreField);
        randomPlace(fruitBtn, width, height);
        randomizeObstacles(obstacles, width, height);
    }

    public static boolean isMatchOver() {
        return matchOver;
    }
}
