package br.usp.each.typerace.server.db;

import java.util.Arrays;
import java.util.List;

public class Palavras {
  private static List<String> PALAVRAS = Arrays.asList(
    "hidramatico", "alaude", "pandemia", "cicatrizacao", "sacudida", "tabuada",
 "exoneravel", "monopolista", "artemisia", "atingido", "renegar", "agnostico",
 "bustie", "antonomastico", "francofilia", "bicho", "meia-sola", "trambolhoes",
 "intervencao", "imobiliaria", "admoestacao", "mala", "afundar", "bispo",
 "pericial", "cineasta", "aglomerar", "multiplicacao", "distanciado", "acento",
 "bruto", "crepitante", "desmiolado", "microeconomia", "sulcar", "telha",
 "badulaque", "popularizacao", "classico", "falsificador", "biologico",
 "movimentado", "testamentario", "acafajestar-se", "munificencia", "higienista",
 "maternal", "modernizar", "mercadorias", "fulgido", "papaia", "pirua",
 "lugar-comum", "pluvial", "inerte", "queijadinha", "escavadeira", "sessenta",
 "supositorio", "iogue", "motociclista", "juramentar", "indolente",
 "alexandrino", "encaixamento", "pontual", "publicacao", "inconstante", "roseo",
 "crotalo", "arauaqui", "amaldicoado", "disparado", "disfuncao", "agressao",
 "inventariar", "escorrido", "polipeptidico", "alento", "rastelo", "adjetivo",
 "estalo", "preguear", "r", "palco", "sucubo", "enlameado", "emerito",
 "polinizador", "iniciar", "gorducho", "inimizade", "burro", "calcinar",
 "sequestrado", "macumbeiro", "equilibrar", "galantear", "noivo", "sensorial", "amor"
  );
 
  public static List<String> TRINTA_PALAVRAS = PALAVRAS.subList(1, 30);
  public static List<String> CINQUENTA_PALAVRAS = PALAVRAS.subList(1, 50);
  public static List<String> SESSENTA_PALAVRAS = PALAVRAS.subList(1, 60);
  public static List<String> OITENTA_PALAVRAS = PALAVRAS.subList(1, 80);
  public static List<String> CEM_PALAVRAS = PALAVRAS.subList(1, 100);
}
