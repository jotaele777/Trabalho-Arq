package Simulador;

import java.io.PrintWriter;

public class CacheController {
    private MainMemory ram;
    private CacheLine[] lines;
    private int size; // Tamanho da cache (ex: 16 linhas)
    
    // Estatísticas para mostrar na apresentação
    public int hits = 0;
    public int misses = 0;
    public boolean lastAccessWasHit = false; // Para feedback visual (verde/vermelho)

    public CacheController(MainMemory ram, int size) {
        this.ram = ram;
        this.size = size;
        this.lines = new CacheLine[size];
        for (int i = 0; i < size; i++) {
            lines[i] = new CacheLine();
        }
    }

    /* Leitura: CPU pede um endereço -> Cache verifica se tem -> Se não, busca na RAM */
    public short read(short address) {
        // Garantir que o endereço é positivo (Java short é signed)
        int addr = address & 0xFFFF;
        
        // Cálculo de Mapeamento Direto (Como visto na prova!)
        int index = addr % size; 
        int tag = addr / size;

        CacheLine line = lines[index];

        // 1. Verificar HIT
        if (line.valid && line.tag == tag) {
            hits++;
            lastAccessWasHit = true;
            return line.data;
        }

        // 2. Handle MISS
        misses++;
        lastAccessWasHit = false;

        // Se a linha atual estiver suja (Dirty), precisamos salvar na RAM antes de sobrescrever (Write-Back)
        if (line.valid && line.dirty) {
            // Reconstrói o endereço antigo: (Tag * Size) + Index
            int oldAddress = (line.tag * size) + index;
            ram.setManual(oldAddress, line.data);
        }

        // Busca o novo dado na RAM
        short dataFromRam = ram.getManual(addr);

        // Atualiza a linha da cache
        line.valid = true;
        line.dirty = false; // Acabamos de ler da RAM, então está limpo
        line.tag = tag;
        line.data = dataFromRam;

        return line.data;
    }

    /* Escrita: CPU escreve -> Cache marca como Dirty (Write-Back) */
    public void write(short address, short value) {
        int addr = address & 0xFFFF;
        int index = addr % size;
        int tag = addr / size;

        CacheLine line = lines[index];

        // Se já existe algo válido lá mas de outro endereço (conflito), salva na RAM antes
        if (line.valid && line.tag != tag && line.dirty) {
             int oldAddress = (line.tag * size) + index;
             ram.setManual(oldAddress, line.data);
        }

        // Escreve na Cache e marca como sujo (Dirty)
        // A RAM NÃO é atualizada agora (característica do Write-Back)
        line.valid = true;
        line.tag = tag;
        line.data = value;
        line.dirty = true; 
        
        // Nota: Em simuladores reais, write miss pode ter políticas diferentes (allocate vs no-allocate).
        // Aqui assumimos Write-Allocate for simplicity.
    }
    
    public CacheLine[] getLines() {
        return lines;
    }
    
    public int getSize() {
        return size;
    }
    
    public void resetStats() {
        hits = 0;
        misses = 0;
    }
    
    public void clearCache() {
        for(int i=0; i<size; i++) {
            lines[i] = new CacheLine();
        }
        resetStats();
    }
}
