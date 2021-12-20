package br.usp.each.typerace.server;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.WebSocketServer;

import br.usp.each.typerace.server.db.Palavras;
import br.usp.each.typerace.server.models.PlacarParcial;
import br.usp.each.typerace.server.utils.ByteBufferStringConverter;
import br.usp.each.typerace.server.utils.Stopwatch;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Server extends WebSocketServer {
  // principais eventos (poderia estar num submodule compartilhado com o client)
  private String EVENTO_JOGO_INICIADO = "JOGO_INICIADO";
  private String EVENTO_JOGO_TERMINADO = "JOGO_TERMINADO";

  // principais comandos (poderia estar num submodule compartilhado com o client)
  private String COMANDO_INICIAR_JOGO = "INICIAR_JOGO";
  private String COMANDO_LIMPAR_CONSOLE = "LIMPAR_CONSOLE";

  private boolean JOGO_ESTA_UP = false;

  // numero de acertos para vencer
  private int NUMERO_ACERTOS_VENCEDOR = 25;

  private String TRINTA_PALAVRAS = String.join("  ", Palavras.TRINTA_PALAVRAS);

  private Stopwatch cronometro = new Stopwatch();
  public Map<String, PlacarParcial> placarGeral = new HashMap<String, PlacarParcial>();

  private final Map<String, WebSocket> connections;

  public Server(int port, Map<String, WebSocket> connections) {
    super(new InetSocketAddress(port));
    this.connections = connections;
  }

  public boolean getStatusJogo() {
    return JOGO_ESTA_UP;
  } 

  // metodo para notificar todos os outros clientes de uma nova conexao
  private void notificaTodosExceto(String clienteNovo) {
    this.connections.forEach((cliente, conn) -> {
      if (!Objects.equals(cliente, clienteNovo)) {
        conn.send("Novo cliente conectado: " + clienteNovo);
      }
      conn.send("Total de clientes conectados: " + connections.size());
    });
  }

  // metodo para enviar uma string para qualquer cliente a partir do nome
  public void enviaMensagemParaCliente(String cliente, String mensagem) {
    WebSocket conn = getClienteConnAPartirDoNome(cliente);
    conn.send(mensagem);
  }

  // publica evento (no formato de byte buffer) para qualquer cliente a partir do nome
  private void publicaEventoParaCliente(String cliente, String sideEffect) {
    WebSocket conn = getClienteConnAPartirDoNome(cliente);
    ByteBuffer evento = ByteBufferStringConverter.stringToByteBuffer(sideEffect);
    conn.send(evento);
  }

  private void salvaConexao(String clienteNome, WebSocket conn) {
    connections.put(clienteNome, conn);
    placarGeral.put(clienteNome, new PlacarParcial());
  }

  // retorna o nome do cliente a partir do sua conexao
  private String getClienteNomeAPartirDaConn(WebSocket conn) {
    for (Entry<String, WebSocket> tupla : connections.entrySet()) {
      if (Objects.equals(tupla.getValue(), conn)) {
        return tupla.getKey();
      }
    }
    return null;
  }

  // retorna a conexao do cliente a partir do seu nome
  private WebSocket getClienteConnAPartirDoNome(String cliente) {
    for (Entry<String, WebSocket> tupla : connections.entrySet()) {
      if (Objects.equals(tupla.getKey(), cliente)) {
        return tupla.getValue();
      }
    }
    return null;
  }

  // metodo executado quando recebemos o comando de INICIAR_JOGO de algum cliente
  private void iniciaJogo() {
    mostraClientesConectados();
    mostraQtdAcertosParaVencer(); 

    ContagemRegressivaThread contagemRegressiva = new ContagemRegressivaThread();

    // inicia uma contagem regressiva
    contagemRegressiva.start();

    synchronized (contagemRegressiva) {
      try {
        contagemRegressiva.wait();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      // comeca o jogo depois da contagem regressiva de ~5 segundos
      JOGO_ESTA_UP = true;
      cronometro.start();

      // indica para todos os clientes que o jogo foi comecado
      publicaEventoParaTodosClientes(EVENTO_JOGO_INICIADO);

      // envia 30 palavras para todos os clientes
      enviaMensagemParaTodosClientes(TRINTA_PALAVRAS);
    }
  }

  private void mostraQtdAcertosParaVencer() {
    enviaMensagemParaTodosClientes("Digite " + NUMERO_ACERTOS_VENCEDOR + " palavras corretas para vencer!\n");
  }

  private void mostraClientesConectados() {
    List<String> clientes = connections
        .entrySet()
        .stream()
        .map(entry -> entry.getKey())
        .collect(Collectors.toList());

    String clientesConectadosMsg = "\nClientes conectados: " + String.join(", ", clientes);
    enviaMensagemParaTodosClientes(clientesConectadosMsg);
  }

  // envia mensagem (string) para todos os clientes
  private void enviaMensagemParaTodosClientes(String mensagem) {
    List<WebSocket> clientes = this.connections
        .entrySet()
        .stream()
        .map(tupla -> tupla.getValue())
        .collect(Collectors.toList());

    broadcast(mensagem, clientes);
  }

  // publica evento/comando (bytebuffer) para todos os clientes
  private void publicaEventoParaTodosClientes(String sideEffect) {
    List<WebSocket> clientes = this.connections
        .entrySet()
        .stream()
        .map(tupla -> tupla.getValue())
        .collect(Collectors.toList());

    ByteBuffer evento = ByteBufferStringConverter.stringToByteBuffer(sideEffect);
    broadcast(evento, clientes);
  }

  // retorna o podio ordenado, baseado no placar parcial de cada cliente
  private LinkedList<Entry<String, PlacarParcial>> getPodioLista() {
    LinkedList<Entry<String, PlacarParcial>> podio = new LinkedList<Entry<String, PlacarParcial>>(placarGeral.entrySet());

    Collections.sort(podio, new Comparator<Entry<String, PlacarParcial>>() {
      @Override
      public int compare(Entry<String, PlacarParcial> p1, Entry<String, PlacarParcial> p2) {
        return p2.getValue().compareTo(p1.getValue());
      }
    });

    return podio;
  }

  // mostra placar final para todos os clientes
  private void apresentaPlacarGeralParaTodosClientes() {
    LinkedList<Entry<String, PlacarParcial>> podio = getPodioLista();

    StringBuilder placarFinal = new StringBuilder();

    placarFinal.append("----- PLACAR FINAL TYPERACE EACH USP ----- \n\n");

    for (Entry<String, PlacarParcial> tupla : podio) {
      int colocacao = podio.indexOf(tupla) + 1;
      String cliente = tupla.getKey();
      PlacarParcial placar = tupla.getValue();

      int acertos = placar.getAcertos();
      int erros = placar.getErros();

      placarFinal
          .append(colocacao + "º " + cliente + " -> " + "Acertos: " + acertos + " / " + "Erros: " + erros + "\n");
    }

    placarFinal.append("----- TEMPO DE PARTIDA: "
        + cronometro.getElapsedTimeSecs() + " (s) -----------\n");

    enviaMensagemParaTodosClientes(placarFinal.toString());
  }

  // metodo utilizado para atualizar a tela do cliente quando ele acerta
  private void disponibilizaPalavrasEPlacarParcialParaCliente(String cliente) {
    String placarParcialStr = getPlacarParcialCliente(cliente);

    PlacarParcial placarCliente = placarGeral.get(cliente);
    String palavrasAtualizadas = String.join(" ", placarCliente.getPalavrasCliente());

    publicaEventoParaCliente(cliente, COMANDO_LIMPAR_CONSOLE);
    enviaMensagemParaCliente(cliente, placarParcialStr + palavrasAtualizadas);
  }
 
  private String getPlacarParcialCliente(String cliente) {
    PlacarParcial placarCliente = placarGeral.get(cliente);

    int acertos = placarCliente.getAcertos();
    int erros = placarCliente.getErros();
    return "Acertos: " + acertos + ", Erros: " + erros + " \n";
  }

  // metodo para encerrar o jogo, parar o cronometro, e mostrar placar geral para todos os clientes
  private void encerraJogo(boolean comSucesso) {
    if (comSucesso) {
      cronometro.stop();
      publicaEventoParaTodosClientes(EVENTO_JOGO_TERMINADO);
      apresentaPlacarGeralParaTodosClientes();
    } else {
      cronometro.clear();
    }

    JOGO_ESTA_UP = false;
  }

  private void resetaPlacar() {
    for (String cliente : placarGeral.keySet()) {
      placarGeral.put(cliente, new PlacarParcial());
    }
  }

  public class ContagemRegressivaThread extends Thread {
    // contagem regressiva de ~5 segundos
    int UM_SEGUNDO_E_MEIO = 1500;

    @Override
    public void run() {
      synchronized (this) {
        for (int i = 5; i > 0; i--) {
          try {
            enviaMensagemParaTodosClientes("O jogo vai comecar em: " + Integer.toString(i));
            Thread.sleep(UM_SEGUNDO_E_MEIO);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        notify();
      }
    }
  }

  // ------ CALLBACKS -------------------------------------------------

  @Override
  public void onOpen(WebSocket conn, ClientHandshake handshake) {
    // pegando nome do cliente a partir do header customizado "x-client-name"
    String novoCliente = handshake.getFieldValue("x-client-name");

    salvaConexao(novoCliente, conn);
    System.out.println("Novo cliente conectado com sucesso: " + novoCliente + ". Conexao: " + conn.getResourceDescriptor());

    // enviando mensagem para o cliente com conexao bem sucedida
    enviaMensagemParaCliente(novoCliente, "Bem vindo ao servidor, " + novoCliente);

    // notificando todos os outros clientes desse novo cliente
    notificaTodosExceto(novoCliente);
  }

  @Override
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    String cliente = getClienteNomeAPartirDaConn(conn);

    System.out.println("Conexao " + conn.getResourceDescriptor() + " fechada do " + cliente
        + " com codigo " + code + ". Info adicional: " + reason);

    // limpando conexao e placar do cliente desconectado
    connections.remove(cliente);
    placarGeral.remove(cliente);

    // encerrando jogo se as conexoes estiverem vazias
    if (connections.keySet().isEmpty()) {
      encerraJogo(false);
    }
  }

  // onMessage de string recebe as palavras digitadas do cliente e tenta pontuar
  @Override
  public void onMessage(WebSocket conn, String palavra) {
    String cliente = getClienteNomeAPartirDaConn(conn);
    PlacarParcial placarCliente = placarGeral.get(cliente);

    System.out.println("Mensagem recebida de " + cliente + ": " + palavra);

    if (JOGO_ESTA_UP) {
      boolean acertou = placarCliente.tentaPontuarAcerto(palavra);
      if (acertou && placarCliente.ehVencedor(NUMERO_ACERTOS_VENCEDOR)) {
        System.out.println(cliente + " venceu!");
        encerraJogo(true);
        resetaPlacar();
        return;
      }

      if (acertou) {
        disponibilizaPalavrasEPlacarParcialParaCliente(cliente);
      } else {
        enviaMensagemParaCliente(cliente, "Errou: " + palavra + "! :(");
      }
    }
  }

  // onMessage de byte buffer recebe os eventos/comandos do cliente,
  // visando produzir algum sideffect, como iniciar o jogo
  @Override
  public void onMessage(WebSocket conn, ByteBuffer sideEffect) {
    String cliente = getClienteNomeAPartirDaConn(conn);
    String comando = ByteBufferStringConverter.byteBufferToString(sideEffect);
    System.out.println("Comando recebido de " + cliente + ": " + comando);

    if (!JOGO_ESTA_UP && Objects.equals(comando, COMANDO_INICIAR_JOGO)) {
      iniciaJogo();
    }
  }

  @Override
  public void onError(WebSocket conn, Exception ex) {
    System.err.println("Um erro ocorreu com "
        + getClienteNomeAPartirDaConn(conn) + ":" + ex);
  }

  @Override
  public void onStart() {
    System.out.println("Servidor iniciado com sucesso!");
  }
 
  @Override
  public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft,
      ClientHandshake handshake) throws InvalidDataException {
    ServerHandshakeBuilder builder = super.onWebsocketHandshakeReceivedAsServer(conn, draft, handshake);

    String novoCliente = handshake.getFieldValue("x-client-name");

    if (connections.get(novoCliente) != null) {
      throw new InvalidDataException(CloseFrame.REFUSE);
    }

    return builder;
  }

}
