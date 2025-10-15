import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Camada de comunicação via socket (Object streams).
 */
public class Comunicador {

    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public Comunicador(Socket socket) throws IOException {
        // Ordem importa: saída primeiro.
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in  = new ObjectInputStream(socket.getInputStream());
    }

    public String readText() throws IOException, ClassNotFoundException {
        return (String) in.readObject();
    }

    public void sendText(String mensagem) throws IOException {
        out.flush();
        out.writeObject(mensagem);
    }

    public Componente readPacket() throws IOException, ClassNotFoundException {
        return (Componente) in.readObject();
    }

    public void sendPacket(Componente componente) throws IOException {
        out.flush();
        out.writeObject(componente);
    }
}
