package br.usp.each.typerace.client;

import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import br.usp.each.typerace.client.utils.ByteBufferStringConverter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

class ClientTest {

    @InjectMocks
    private Client subject;
   
    @BeforeEach
    public void setup() throws URISyntaxException {
        subject = new Client(new URI("servidor"));
    }
    
    @Test
    public void iniciaConexaoSemErro() {
      ServerHandshake mockServerHandshake = mock(ServerHandshake.class);
     
      subject.onOpen(mockServerHandshake);
    }

    @Test
    public void deveReceberEventosDoServidor() {
      ByteBuffer sideEffect = ByteBufferStringConverter.stringToByteBuffer("JOGO_INICIADO");
     
      assertEquals(false, subject.getStatusGame());
    
      subject.onMessage(sideEffect); 
      
      assertEquals(true, subject.getStatusGame());
    }

    @Test
    public void deveConseguirSairDoServidorSeNaoEstiverEmJogo() {
      assertEquals(false, subject.getStatusGame());
     
      subject.saiDoServidor();
    }

}