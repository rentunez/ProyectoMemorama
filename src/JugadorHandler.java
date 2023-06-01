import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class JugadorHandler implements Runnable {
    private Socket socket;
    private MemoramaJuego juego;
    private int jugador;

    public JugadorHandler(Socket socket, MemoramaJuego juego, int jugador) {
        this.socket = socket;
        this.juego = juego;
        this.jugador = jugador;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            writer.println("Bienvenido al juego de memorama. Esperando a otro jugador...");

            // Espera a que se unan ambos jugadores antes de comenzar el juego
            juego.esperarJugadores();

            writer.println("Comienza el juego. ¡Buena suerte!");

            while (!juego.juegoTerminado()) {
                // Envía el estado del juego al jugador
                writer.println(juego.getEstadoJuego(jugador));

                // Recibe la selección del jugador
                int cartaSeleccionada = Integer.parseInt(reader.readLine());

                // Realiza la jugada
                juego.realizarJugada(jugador, cartaSeleccionada);

                // Espera un breve tiempo para sincronizar las jugadas de ambos jugadores
                Thread.sleep(500);
            }

            // Juego terminado, envía el estado final y cierra la conexión
            writer.println(juego.getEstadoJuego(jugador));
            writer.println("¡Juego terminado!");

            socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
