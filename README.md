# redes-ep2-typerace
Repositório para o EP2 de Redes de Computadores, EACH-USP - 2021/2

# Integrantes
* Integrante 1 - 1111111
* Integrante 2 - 2222222
* Integrante 3 - 3333333
* Integrante 4 - 4444444

## Pré-requisitos
* JDK 11 ou maior (testado com a JDK11 OpenJDK)
* Gradle (incluso no repositório, não é necessário instalá-lo)

### Rodando
Para rodar o servidor localhost (passar a porta como unico argumento)
```sh
./gradlew server:run --args="8080" --console-plain
```

Para rodar um cliente localhost (passar a porta como primeiro argumento e o nome do cliente como segundo argumento)
```sh
./gradlew client:run --args="8080 Pedro" --console-plain
```
