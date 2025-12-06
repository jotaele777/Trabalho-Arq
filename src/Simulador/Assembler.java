package Simulador;
import java.util.Map;

public class Assembler {
    
    public static short assembleAndLoad(String assemblyCode) { 
        if (assemblyCode == null || assemblyCode.trim().isEmpty()) {
            return -1;
        }

        // Divide por linhas e pega a primeira (assume-se chamada linha a linha pelo FileParser)
        String line = assemblyCode.trim().split("\n")[0].trim();
        
        if (line.isEmpty() || line.startsWith("//") || line.startsWith("#") || line.startsWith(";")) {
            return -1;
        }

        // Remove vírgulas para flexibilidade (LODD 10 vs LODD, 10)
        String[] parts = line.replace(",", " ").trim().split("\\s+");
        String mnemonic = parts[0].toUpperCase();

        if (!Dicio.inst.containsKey(mnemonic)) { 
            throw new IllegalArgumentException("Erro de Montagem: Mnemônico inválido '" + mnemonic + "'");
        }

        String binaryPrefix = Dicio.inst.get(mnemonic); 
        StringBuilder binaryBuilder = new StringBuilder(binaryPrefix);

        try {
            // TIPO 1: Instruções com Endereço (12 bits)
            // Ex: LODD, STOD, JUMP...
            if (isAddressInstruction(mnemonic)) {
                if (parts.length < 2) throw new IllegalArgumentException(mnemonic + " requer endereço.");
                
                int address = Integer.parseInt(parts[1]);
                if (address < 0 || address > 4095) throw new IllegalArgumentException("Endereço fora do limite (0-4095): " + address);
                
                // Formata para 12 bits binários
                String binAddr = String.format("%12s", Integer.toBinaryString(address)).replace(' ', '0');
                binaryBuilder.append(binAddr);
            } 
            // TIPO 2: Instruções com Valor Imediato (8 bits)
            // Ex: INSP, DESP
            else if (isValueInstruction(mnemonic)) {
                if (parts.length < 2) throw new IllegalArgumentException(mnemonic + " requer valor.");
                
                int value = Integer.parseInt(parts[1]);
                if (value < 0 || value > 255) throw new IllegalArgumentException("Valor fora do limite (0-255): " + value);
                
                String binVal = String.format("%8s", Integer.toBinaryString(value)).replace(' ', '0');
                binaryBuilder.append(binVal);
            }
            // TIPO 3: Instruções sem operando (HALT, POP, etc)
            else {
                // Já pegou o prefixo, não faz nada
            }
            
            String binaryFinal = binaryBuilder.toString();
            // Ajuste para instruções de 15 bits (completar com 0 se necessário, embora ISA MIC-1 seja 16)
            while(binaryFinal.length() < 16) {
                binaryFinal = "0" + binaryFinal;
            }

            return (short) Integer.parseInt(binaryFinal, 2);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Operando não numérico na instrução: " + line);
        }
    }
    
    private static boolean isAddressInstruction(String m) {
        return m.equals("LODD") || m.equals("STOD") || m.equals("ADDD") || m.equals("SUBD") || 
               m.equals("JPOS") || m.equals("JZER") || m.equals("JUMP") || m.equals("LOCO") || 
               m.equals("LODL") || m.equals("STOL") || m.equals("ADDL") || m.equals("SUBL") || 
               m.equals("JNEG") || m.equals("JNZE");
    }
    
    private static boolean isValueInstruction(String m) {
        return m.equals("INSP") || m.equals("DESP");
    }
}