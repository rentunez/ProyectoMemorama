import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MemoramaServidor {
    private static final int PUERTO = 1234; // Puerto en el que el servidor escucha las conexiones

    private MemoramaJuego juego; // Objeto que representa el estado y la lógica del juego

    public MemoramaServidor() {
        juego = new MemoramaJuego();
    }

    public void iniciar() {
        try {
            ServerSocket serverSocket = new ServerSocket(PUERTO);
            System.out.println("Servidor iniciado. Esperando jugadores...");

            // Espera la conexión de los dos jugadores
            for (int i = 0; i < 2; i++) {
                Socket socket = serverSocket.accept();
                System.out.println("Jugador " + (i + 1) + " conectado.");

                // Crea un hilo para manejar al jugador
                Thread jugadorThread = new Thread(new JugadorHandler(socket, juego, i + 1));
                jugadorThread.start();
            }

            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MemoramaServidor servidor = new MemoramaServidor();
        servidor.iniciar();
    }
}
