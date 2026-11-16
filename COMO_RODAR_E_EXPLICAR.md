# Bomberman POO - Terminal

## 1. Como compilar

Entre na pasta do projeto:

```bash
cd Bomberman
```

Compile:

```bash
javac -d out src/Principal.java
```

## 2. Como rodar

```bash
java -cp out Principal
```

## 3. Como jogar

No menu inicial:

- `1` inicia o jogo.
- `2` sai.

Durante o jogo:

- `W`, `A`, `S`, `D`: movem o personagem.
- `B`: planta uma bomba.
- `E`: espera um turno.
- `H`: pede uma dica sobre poder escondido.
- `DEPURACAO`: mostra poderes e saida escondidos.
- `Q`: abre o menu de saida/salvamento.

## 4. Regras implementadas

- O mapa e uma matriz quadrada de 15x15.
- `P` e o jogador.
- `X` e parede indestrutivel.
- `#` e caixote destrutivel.
- `C` e inimigo comum.
- `R` e inimigo rastreador.
- `S` e inimigo sabotador.
- `B` e bomba.
- `+` e fogo da explosao.
- `*` e poder revelado.
- `O` e saida revelada.

O jogador comeca com 3 vidas, 1 bomba simultanea e raio de explosao 1.
As bombas detonam depois de 5 turnos.
A explosao se propaga em cruz e para ao bater em parede ou caixote.
Caixotes podem esconder poderes ou a saida.
Para vencer, elimine todos os inimigos e pise na saida revelada.

## 5. Onde aparecem os conceitos de POO

- Encapsulamento: atributos privados em `Jogador`, `Tabuleiro`, `Celula`, `Bomba` e outras classes.
- Composicao: `Jogo` possui `Tabuleiro`, `Jogador`, lista de `Inimigo`, lista de `Bomba` e lista de `Chama`.
- Heranca: `Inimigo` e uma classe abstrata, herdada por `InimigoComum`, `InimigoRastreador` e `InimigoSabotador`.
- Polimorfismo: cada inimigo implementa `agir(Jogo jogo)` de forma diferente.
- Interface: `Destrutivel` representa objetos que podem reagir a explosoes.
- Excecoes personalizadas: `ExcecaoMovimentoInvalido` e `ExcecaoLimiteBombas`.
- Arquivos: jogo salvo em `dados/jogo_salvo.txt` e ultimo mapa em `dados/ultimo_mapa.txt`.

## 6. Sugestao para o relatorio

Estrutura recomendada:

1. Capa.
2. Sumario.
3. Introducao explicando o jogo.
4. Diagrama de classes.
5. Explicacao dos conceitos de POO usados.
6. Passos para compilar e executar.
7. Exemplos de uso com capturas do terminal.
8. Dificuldades encontradas.
9. Sugestoes futuras.
10. Conclusao.
