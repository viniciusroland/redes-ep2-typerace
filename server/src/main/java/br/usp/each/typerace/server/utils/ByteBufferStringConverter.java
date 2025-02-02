package br.usp.each.typerace.server.utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class ByteBufferStringConverter {
  // conversor de byte buffer <-> string, 
  // usado principalmente para publicar os comandos/eventos do servidor
  public static String byteBufferToString(ByteBuffer bb) {
    try {
      return new String(bb.array(), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return null; 
    }
  }
 
  public static ByteBuffer stringToByteBuffer(String s) {
    try {
      return ByteBuffer.wrap(s.getBytes("UTF-8"));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return null;
    }
  }
}
