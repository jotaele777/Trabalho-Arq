package Simulador;

public class CacheLine {
    public boolean valid;
    public boolean dirty; // Indica se o dado foi modificado na cache mas não na RAM
    public int tag;
    public short data;    // No MIC-1 simplificado, vamos guardar 1 palavra por linha para facilitar a visualização

    public CacheLine() {
        this.valid = false;
        this.dirty = false;
        this.tag = 0;
        this.data = 0;
    }
}
