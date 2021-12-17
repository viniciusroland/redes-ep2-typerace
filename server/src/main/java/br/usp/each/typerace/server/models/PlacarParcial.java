package br.usp.each.typerace.server.models;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import br.usp.each.typerace.server.db.Palavras;

public class PlacarParcial implements Comparable<PlacarParcial> {
  private int acertos = 0;
  private int erros = 0;
  private List<String> palavrasDoCliente = new LinkedList<String>(Palavras.TRINTA_PALAVRAS);

  public void checaAcerto(String mensagem, int index) {
    if (Objects.equals(mensagem, palavrasDoCliente.get(index))) {
      marcarAcerto();
    } else {
      marcarErro();
    }
  }

  public boolean tentaPontuarAcerto(String mensagem) {
    boolean acertou = palavrasDoCliente
      .stream()
      .anyMatch((palavra) -> Objects.equals(mensagem, palavra));

    if (acertou) {
      marcarAcerto();
      palavrasDoCliente.remove(mensagem);
    } else {
      marcarErro();
    }

    return acertou;
  }

  public void marcarAcerto() {
    acertos++;
  }

  public void marcarErro() {
    erros++;
  }

  public int getAcertos() {
    return acertos;
  }

  public int getErros() {
    return erros;
  }

  public List<String> getPalavrasCliente() {
    return palavrasDoCliente;
  }

  public boolean ehVencedor(int numeroAcertosVencedor) {
    return numeroAcertosVencedor == acertos; 
  }

  @Override
  public int compareTo(PlacarParcial x) {
    return this.acertos - x.acertos;
  }
}
