package Simulador;
import javax.swing.SwingUtilities;

public class Simulation {
    public static void main(String[] args) {
        // É boa prática rodar GUI dentro da thread do Swing
        SwingUtilities.invokeLater(() -> {
            new Screen();
        });
    }
}

/*
 * // TESTE 1: ARITMÉTICA BÁSICA
LOCO 10
STOD 50
LOCO 5
ADDD 50
STOD 51
SUBD 50
HALT



// TESTE 2: ESTRESSE DE CACHE (WRITE-BACK & EVICÇÃO)
 
LOCO 111
STOD 0
LOCO 222
STOD 16
LODD 0 
HALT

 */