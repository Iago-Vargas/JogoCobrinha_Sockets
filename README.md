# Jogo de Coleta em Rede (Swing + Sockets)

Projeto acadêmico em **Java SE** que implementa um **jogo 1x1 em rede** usando **Swing** para UI e **sockets TCP** para comunicação. Dois jogadores se movimentam no tabuleiro; ao encostar na **fruta** (`@`), o jogador marca ponto. Há **barreiras** (botões) posicionadas aleatoriamente para dificultar o caminho. Vence quem atinge o placar máximo.

> Tecnologias: Java 8+ • Swing (JFrame/JButton/JPanel) • TCP sockets • ObjectInputStream/ObjectOutputStream

---

## 🎯 Objetivos do Projeto

- Exercitar **cliente-servidor** em Java.
- Separar **lógica de jogo** (movimento, colisões, placar) da **UI** (Swing).
- Sincronizar **estado mínimo** via objetos serializados.
- Respeitar o **Form Editor do NetBeans**, mantendo nomes gerados na UI.

---

## 🧩 Como o Jogo Funciona

- Duas aplicações:
  - **Servidor (Jogador 1)**: `ServidorJogador1` abre servidor na **porta 12345**.
  - **Cliente (Jogador 2)**: `ClienteJogador2` conecta no IP/porta do servidor.
- **Controles**: setas do teclado (↑, ↓, ←, →) movem o jogador local.
- **Pontos**: ao colidir com a fruta:
  - Placar é atualizado;
  - Fruta reposicionada aleatoriamente;
  - **Barreiras** também são randomizadas.
- **Fim de jogo**: quando um jogador atinge **MAX_POINTS (10)**.
- **Reiniciar**: no servidor, o botão **“Reiniciar Jogo”** zera placar e reposiciona elementos e notifica o cliente.

---

## 🔓 Por trás dos Panos
---

## Arquitetura 

* **Componente**: protocolo/DTO serializável (mensagens do jogo).
* **Comunicador**: camada de I/O com `ObjectInputStream`/`ObjectOutputStream`.
* **Movimenta**: regras do jogo (movimento, colisão, placar e reset).
* **ServidorJogador1 / ClienteJogador2**: UI Swing + integração de rede.

---

## Classe por Classe e Função por Função

### `Componente` (protocolo/DTO serializável)

* **Constantes**: `FRUIT`, `PLAYER`, `SCOREBOARD`, `RESET`, `OBSTACLE` — definem o tipo do pacote.
* **Campos de posição**: `posX`, `posY`, `width`, `height`.
* **`kind`**: qual o tipo do pacote (usa as constantes).
* **Placar/estado**: `scoreP1`, `scoreP2`, `matchOver`.
* **`id`**: opcional p/ obstáculos.
* **Construtor (posicional)** `Componente(int posX, int posY, int width, int height)`: cria um pacote com bounding box (posição/tamanho). Usado pra **PLAYER** e **FRUIT** (depois você seta `kind`).
* **Construtor (placar/estado)** `Componente(int kind, int scoreP1, int scoreP2, boolean matchOver)`: pacote voltado a **SCOREBOARD/RESET**.
* **Construtor (obstáculo)** `Componente(int posX, int posY, int width, int height, int id)`: define **OBSTACLE** com identificação.

---

### `Comunicador` (camada de I/O de rede)

* **`Comunicador(Socket socket)`**: cria `ObjectOutputStream` (primeiro!) e `ObjectInputStream`.
* **`readText()`**: lê uma `String` do stream.
* **`sendText(String mensagem)`**: envia `String` (chama `flush` e `writeObject`).
* **`readPacket()`**: lê um `Componente` do stream (bloqueante).
* **`sendPacket(Componente componente)`**: envia um `Componente` (chama `flush` e `writeObject`).

> **Observação**: a ordem de criar primeiro o `ObjectOutputStream` em ambos os lados evita deadlock no handshake do stream de objetos.

---

### `Movimenta` (regras do jogo)

* **Estado estático**: `pointsP1`, `pointsP2`, `MAX_POINTS = 10`, `matchOver`, lista estática de `obstacles`.
* **`setObstacles(List<JButton> obs)`**: injeta a lista de obstáculos controlada pela UI.
* **`collidesWithObstacle(Rectangle target)`**: checa se o próximo retângulo colide com algum obstáculo.
* **Movimento**:

  * `moveLeft(JButton btn)`
  * `moveUp(JButton btn)`
  * `moveRight(JButton btn, int panelWidth)`
  * `moveDown(JButton btn, int panelHeight)`

  Calcula o próximo retângulo, valida bordas e colisão; aplica `setBounds` se puder.
* **`intersects(JButton a, JButton b)`**: colisão entre dois botões (retângulos).
* **`randomPlace(JButton btn, int width, int height)`**: posiciona um botão aleatoriamente dentro do painel.
* **`randomizeObstacles(List<JButton> obs, int width, int height)`**: embaralha posições dos obstáculos.
* **`addPoint(int player, JTextField scoreField)`**: incrementa placar (P1 ou P2) e atualiza texto; se alguém chega a `MAX_POINTS`, define `matchOver` e mostra vencedor.
* **`renderScore(JTextField scoreField)`**: escreve `"P1 X P2"`.
* **`resetMatch(JTextField scoreField, JButton fruitBtn, int width, int height)`**: zera placar/estado, reposiciona fruta e randomiza obstáculos.
* **`isMatchOver()`**: retorna `matchOver`.

---

### `ClienteJogador2` (UI + cliente TCP)

* **Construtor `ClienteJogador2()`**:

  * Cria UI (Swing).
  * Define **barreiras** (quatro `JButton`) e injeta em `Movimenta`.
  * Inicia **thread** que:

    * Cria `Socket(host, 12345)` (usar IP real).
    * Instancia `Comunicador`.
    * Loop infinito lendo `Componente`:

      * **`PLAYER`**: atualiza posição do **jogador 1**.
      * **`FRUIT`**: atualiza fruta, **pontua P1** (placar espelhado) e randomiza obstáculos.
      * **`RESET`**: reseta partida e randomiza obstáculos.
* **`jButtonFrutaKeyPressed(KeyEvent)`**:

  * Lê setas e move **jogador 2** com `Movimenta`.
  * Se colidiu com a fruta: `addPoint(2)`, reposiciona fruta, randomiza obstáculos e, se jogo não acabou, **envia** pacote `FRUIT` com posição atual pro servidor (sincronizar).
  * Sempre ao final: **envia** pacote `PLAYER` com posição do **jogador 2**.
* **`main`**: sobe a janela do cliente (Nimbus L&F, se disponível).

---

### `ServidorJogador1` (UI + servidor TCP)

* **Construtor `ServidorJogador1()`**:

  * Cria UI (Swing).
  * Define **barreiras** e injeta em `Movimenta`.
  * Inicia **thread** que:

    * Abre `ServerSocket(12345)` e **aceita** o cliente.
    * Instancia `Comunicador`.
    * Loop infinito lendo `Componente`:

      * **`PLAYER`**: atualiza posição do **jogador 2**.
      * **`FRUIT`**: atualiza fruta, **pontua P2** e randomiza obstáculos.
      * **`RESET`**: reseta e randomiza obstáculos.
* **`jButtonFrutaKeyPressed(KeyEvent)`**:

  * Move **jogador 1** pelas setas e aplica `Movimenta`.
  * Se colidiu com a fruta: `addPoint(1)`, reposiciona fruta, randomiza obstáculos e, se jogo não acabou, **envia** `FRUIT` com a nova posição ao cliente.
  * Ao final: **envia** `PLAYER` com a posição do **jogador 1**.
* **`btnReiniciarActionPerformed(ActionEvent)`**:

  * Chama `resetMatch(...)` localmente.
  * **Envia** um `Componente(RESET, 0, 0, false)` ao cliente.
  * **Envia** também o `FRUIT` atual (garante sincronismo).
  * Randomiza obstáculos novamente.
* **`main`**: sobe a janela do servidor.

---

