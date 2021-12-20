package br.usp.each.typerace.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;

import br.usp.each.typerace.client.utils.ByteBufferStringConverter;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Scanner;

public class Client extends WebSocketClient {
  // principais eventos (poderia estar num submodule compartilhado com o backend)
  private String EVENTO_JOGO_INICIADO = "JOGO_INICIADO";
  private String EVENTO_JOGO_TERMINADO = "JOGO_TERMINADO";

  // principais comandos (poderia estar num submodule compartilhado com o backend)
  private String COMANDO_SAIR_JOGO = "SAIR_JOGO";
  private String COMANDO_LIMPAR_CONSOLE = "LIMPAR_CONSOLE";
  private String COMANDO_INICIAR_JOGO = "INICIAR_JOGO";

  private boolean ESTA_EM_GAME = false;
 
  public boolean getStatusGame() {
    return ESTA_EM_GAME;
  }

  public Client(URI serverUri) {
    super(serverUri);
  }

  public void saiDoServidor() {
    close();
  }

  // o leitar eh basicamente uma thread apartada que roda indefinidamente um Scanner.nextLine() 
  // e envia pro servidor a mensagem ou o comando lido
  public void iniciaLeitor() {
    new Thread() {

      @Override
      public void run() {
        try (var sc = new Scanner(System.in)) {
          while (true) {
            String palavraDigitada = sc.nextLine();

            if (!ESTA_EM_GAME && Objects.equals(palavraDigitada, COMANDO_SAIR_JOGO)) {
              saiDoServidor();
              return; 
            } 
           
            if (!ESTA_EM_GAME && Objects.equals(palavraDigitada, COMANDO_INICIAR_JOGO)) {
              publicaComando(COMANDO_INICIAR_JOGO);
              continue; 
            } 
            
            if (ESTA_EM_GAME) {
              enviaMensagem(palavraDigitada);
            }
          }
        }
      }
    }.start();
  }
 
  private void publicaComando(String comando) {
    ByteBuffer bb = ByteBufferStringConverter.stringToByteBuffer(comando);
    send(bb);
  }
 
  private void enviaMensagem(String mensagem) {
    send(mensagem);
  }

  // lida com evento de jogo iniciado enviado pelo servidor 
  private void lidaComPossivelEventoDeJogoIniciado(String mensagem) {
    if (Objects.equals(mensagem, EVENTO_JOGO_INICIADO)) {
      ESTA_EM_GAME = true;
      limpaConsole(); 
    }
  }

  // lida com evento de jogo terminado enviado pelo servidor 
  private void lidaComPossivelEventoDeJogoTerminado(String mensagem) {
    if (Objects.equals(mensagem, EVENTO_JOGO_TERMINADO)) {
      ESTA_EM_GAME = false;
      limpaConsole();
    }
  }

  // lida com comando de limpar o console enviado pelo servidor
  private void lidaComPossivelComandoDeLimparConsole(String mensagem) {
    if (Objects.equals(mensagem, COMANDO_LIMPAR_CONSOLE)) {
      limpaConsole();
    }
  }
  
  // limpa console para dar espaco para as palavras atualizadas
  public void limpaConsole() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }

  // ------ CALLBACKS -------------------------------------------------
  
  @Override
  public void onOpen(ServerHandshake handshakedata) {
    iniciaLeitor();
  }

  @Override
  public void onClose(int code, String reason, boolean remote) {
   if (code == CloseFrame.REFUSE || code == CloseFrame.PROTOCOL_ERROR) {
     System.out.println("\nExcecao: Nome de cliente ja esta em uso! Mude o nome de cliente e tente novamente.");
   }
  }

  @Override
  public void onMessage(String mensagem) {
    System.out.println(mensagem);
  }
 
  // lida com os eventos/comandos enviados pelo servidor no formato de byte buffer 
  @Override
  public void onMessage(ByteBuffer sideEffect) {
    String s = ByteBufferStringConverter.byteBufferToString(sideEffect);
    lidaComPossivelEventoDeJogoIniciado(s);
    lidaComPossivelEventoDeJogoTerminado(s);
    lidaComPossivelComandoDeLimparConsole(s);
  }

  @Override
  public void onError(Exception ex) {
    System.err.println("Erro: " + ex);
  }
}