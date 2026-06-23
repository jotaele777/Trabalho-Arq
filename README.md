Simulador de Microarquitetura MIC-1 com Cache L1

Este projeto implementa um simulador visual da microarquitetura MIC-1 (baseado em Tanenbaum) com uma adição significativa: Hierarquia de Memória.

Desenvolvido para a disciplina de Arquitetura de Computadores.

🚀 Funcionalidades

Simulação de CPU: Ciclo completo de busca, decodificação e execução (Microinstruções).

Cache de Dados (L1): - Mapeamento Direto.

Política de Escrita Write-Back.

Visualização em tempo real de Hits, Misses, Dirty Bits e Evicção.

Montador Integrado: Aceita código Assembly IJVM (ex: LODD, STOD, LOCO).

Interface Gráfica: Desenvolvida em Java Swing.

📦 Como Executar

Pré-requisitos

Java 17 ou superior instalado.

Rodando o JAR

Baixe o arquivo SimuladorMIC1.jar e a pasta dataFiles. No terminal, execute:

java -jar SimuladorMIC1.jar

Docker

Para construir a imagem e rodar o container:

# Construir a imagem
docker build -t simulador-mic1 .

# Rodar (Requer configuração de X11 para GUI no Windows/Linux)
docker run -it --rm --net=host -e DISPLAY=$DISPLAY simulador-mic1


 Testes Demonstrativos

Teste Básico: Carregue um código simples de soma para ver o Datapath.

Teste de Cache: Use o código abaixo para ver o Write-Back em ação:

LOCO 111
STOD 0    ; Grava na Cache (Dirty)
LOCO 222
STOD 16   ; Conflito na Linha 0 -> Evicção para RAM
LODD 0    ; Cache Miss -> Busca da RAM
HALT


 Autores:
 
Pedro Rangel

João Lucas Pontes da Silva

Rafael Gusmão

Rafael Portela

Iuri Frazão
