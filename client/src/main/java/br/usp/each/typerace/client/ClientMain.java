package br.usp.each.typerace.client;

import org.java_websocket.client.WebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;

public class ClientMain {

  private WebSocketClient client;

  public ClientMain(WebSocketClient client) {
    this.client = client;
  }

  public void init(String idCliente) {
    // adicionando o nome do cliente como um custom header do WebSocketClient
    // com isso conseguimos linkar uma string customizada `a conexao do cliente
    System.out.println("Iniciando cliente: " + idCliente);
    this.client.addHeader("x-client-name", idCliente);
    this.client.connect();
  }

  public static void main(String[] args) {
    String porta = args[0];
    String clienteNome = args[1];

    try {
      // iniciando sempre em localhost para facilitar
      String localhost = "ws://localhost:";
      WebSocketClient client = new Client(new URI(localhost + porta));

      ClientMain main = new ClientMain(client);
      main.init(clienteNome);
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
  }
}
