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

