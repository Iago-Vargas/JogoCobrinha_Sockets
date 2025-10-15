import java.io.Serializable;

/**
 * Pacote de dados do jogo trafegando entre servidor e cliente.
 */
public class Componente implements Serializable {
    // Tipos de mensagem (renomeados, mantendo semântica)
    public static final int FRUIT = 1;
    public static final int PLAYER = 2;
    public static final int SCOREBOARD = 3;
    public static final int RESET = 4;
    public static final int OBSTACLE = 5;

    // Dados de posição 
    public int posX;
    public int posY;
    public int width;
    public int height;

    // Indica o que este objeto representa 
    public int kind;

    // Campos de placar/estado do jogo
    public int scoreP1;
    public int scoreP2;
    public boolean matchOver;


    // id opcional para barreira/obstáculo
    public int id;

    /** Construtor para componentes de posição  */
    public Componente(int posX, int posY, int width, int height) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.scoreP1 = 0;
        this.scoreP2 = 0;
        this.matchOver = false;
    }

    /** Construtor dedicado para SCOREBOARD/estado do jogo. */
    public Componente(int kind, int scoreP1, int scoreP2, boolean matchOver) {
        this.kind = kind;
        this.scoreP1 = scoreP1;
        this.scoreP2 = scoreP2;
        this.matchOver = matchOver;
    }

    /** Construtor para barreiras com id. */
    public Componente(int posX, int posY, int width, int height, int id) {
        this.kind = OBSTACLE;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.id = id;
    }


}
