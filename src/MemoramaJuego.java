import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MemoramaJuego {
    private static final int NUM_PAREJAS = 8;

    private String[] cartas;
    private boolean[] cartasVisibles;
    private List<Integer> cartasSeleccionadas;

    private boolean[] jugadoresConectados;

    public MemoramaJuego() {
        cartas = new String[NUM_PAREJAS * 2];
        cartasVisibles = new boolean[NUM_PAREJAS * 2];
        cartasSeleccionadas = new ArrayList<>();
        jugadoresConectados = new boolean[2];

        inicializarCartas();
        mezclarCartas();
    }

    private void inicializarCartas() {
        for (int i = 0; i < NUM_PAREJAS; i++) {
            cartas[i] = String.valueOf(i + 1);
            cartas[i + NUM_PAREJAS] = String.valueOf(i + 1);
        }
    }

    private void mezclarCartas() {
        List<String> listaCartas = Arrays.asList(cartas);
        Collections.shuffle(listaCartas);
        listaCartas.toArray(cartas);
    }

    public synchronized void esperarJugadores() {
        int jugador = Thread.currentThread().getName().equals("Thread-0") ? 1 : 2;
        jugadoresConectados[jugador - 1] = true;

        while (!(jugadoresConectados[0] && jugadoresConectados[1])) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void realizarJugada(int jugador, int cartaSeleccionada) {
        cartasSeleccionadas.add(cartaSeleccionada);

        if (cartasSeleccionadas.size() == 2) {
            if (sonCartasIguales()) {
                cartasVisibles[cartasSeleccionadas.get(0)] = true;
                cartasVisibles[cartasSeleccionadas.get(1)] = true;
            } else {
                cartasSeleccionadas.clear();
            }

            notifyAll(); // Notifica a los jugadores para que actualicen su estado
        }
    }

    private boolean sonCartasIguales() {
        int carta1 = cartasSeleccionadas.get(0);
        int carta2 = cartasSeleccionadas.get(1);

        return cartas[carta1].equals(cartas[carta2]);
    }

    public synchronized String getEstadoJuego(int jugador) {
        StringBuilder estadoJuego = new StringBuilder();

        for (int i = 0; i < cartas.length; i++) {
            if (cartasVisibles[i]) {
                estadoJuego.append(cartas[i]).append(" ");
            } else {
                estadoJuego.append("* ");
            }

            if ((i + 1) % 4 == 0) {
                estadoJuego.append("\n");
            }
        }

        estadoJuego.append("\nJugador actual: ").append(jugador);

        return estadoJuego.toString();
    }

    public synchronized boolean juegoTerminado() {
        for (boolean cartaVisible : cartasVisibles) {
            if (!cartaVisible) {
                return false;
            }
        }

        return true;
    }
}
