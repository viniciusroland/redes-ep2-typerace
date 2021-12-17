package br.usp.each.typerace.server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import br.usp.each.typerace.server.models.PlacarParcial;
import br.usp.each.typerace.server.utils.ByteBufferStringConverter;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class ServerTest {

    @Mock
    private Map<String, WebSocket> connections;

    @Mock
    private WebSocket mockConnection;

    @InjectMocks
    private Server subject;

    @BeforeEach
    public void setup() {
        connections = new HashMap<>();
        subject = new Server(8080, connections);
        mockConnection = mock(WebSocket.class);
    }

    @Test
    public void deveArmazenarConexoesAbertas() {
        ClientHandshake mockHandshake = mock(ClientHandshake.class);
        
        when(mockHandshake.getFieldValue(anyString())).thenReturn("nome-cliente");

        subject.onOpen(mockConnection, mockHandshake);

        assertEquals(1, connections.size());
        verify(mockConnection, times(1)).getResourceDescriptor();
    }

    @Test
    public void deveRemoverConexoesFechadas() {
        connections.put("test", mockConnection);

        subject.onClose(mockConnection, 0, "Algum motivo", true);

        assertEquals(0, connections.size());
        verify(mockConnection, times(1)).getResourceDescriptor();
    }

    @Test
    public void deveTratarExcecoesDoServidor() {
        Exception exception = new Exception("Nao deve ser lancada");

        try {
            subject.onError(mockConnection, exception);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void deveReceberComandoParaIniciarJogo() {
      assertEquals(false, subject.getStatusJogo());
      
      ByteBuffer sideEffect = ByteBufferStringConverter.stringToByteBuffer("INICIAR_JOGO");
      subject.onMessage(mockConnection, sideEffect);

      assertEquals(true, subject.getStatusJogo());
    }
   
    @Test
    public void deveMarcarPlacarParcial() {
      String PALAVRA_CORRETA = "alaude";
      String PALAVRA_ERRADA = "erro";

      assertEquals(false, subject.getStatusJogo());
      
      ByteBuffer sideEffect = ByteBufferStringConverter.stringToByteBuffer("INICIAR_JOGO");
      subject.onMessage(mockConnection, sideEffect);

      assertEquals(true, subject.getStatusJogo());
     
      connections.put("cliente1", mockConnection);
      subject.placarGeral.put("cliente1", new PlacarParcial());
      subject.onMessage(mockConnection, PALAVRA_CORRETA);

      assertEquals(1, subject.placarGeral.get("cliente1").getAcertos());
      assertEquals(0, subject.placarGeral.get("cliente1").getErros());
     
      subject.onMessage(mockConnection, PALAVRA_ERRADA);
      assertEquals(1, subject.placarGeral.get("cliente1").getAcertos());
      assertEquals(1, subject.placarGeral.get("cliente1").getErros());
    }
   
    @Test
    public void mostraResultado() {
      
    }
}