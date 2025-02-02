# redes-ep2-typerace
Repositório para o EP2 de Redes de Computadores, EACH-USP - 2021/2

# Integrantes
* Vinicius Roland Crisci - 10773381
* Rafael Bingre Malhado - 10296952
* Vinicius Mariano Bispo - 10875965
* William Hideyuki Ayukawa - 10346892

## Pré-requisitos
* JDK 11 ou maior (testado com a JDK11 OpenJDK)
* Gradle (incluso no repositório, não é necessário instalá-lo)

### Rodando
Para rodar o servidor localhost (passar a porta como único argumento)
```sh
./gradlew server:run --args="8080" --console=plain
```

Para rodar um cliente localhost (passar a porta como primeiro argumento e o nome do cliente como segundo argumento)
```sh
./gradlew client:run --args="8080 Pedro" --console=plain
```

### Iniciar jogo
Para o cliente iniciar o jogo após conectado basta digitar INICIAR_JOGO

### Sair do jogo
Para o cliente sair do jogo antes de começar basta digitar SAIR_JOGO

### Digitando as palavras
Após as palavras aparecerem na tela, como 'alaude', 'pandemia', 'cicatrizacao', você já pode começar a digitar. Para enviar uma palavra ao servidor, basta digita-la e apertar ENTER para enviar

### Printscreens

Conexão iniciada com clientes
![Conexao iniciada com clientes](https://i.postimg.cc/HsPZcm9H/conexao-iniciada.png)

Contagem regressiva após iniciar o jogo
![Contagem regressiva apos iniciar jogo](https://i.postimg.cc/kg0TsyHp/contagem-regressiva.png)

Jogo rodando
![Jogo rodando](https://i.postimg.cc/nhjdqW3m/jogo-rodando.png)

Placar final
![Placar final](https://i.postimg.cc/wBD0Gm8T/placar-final.png)
