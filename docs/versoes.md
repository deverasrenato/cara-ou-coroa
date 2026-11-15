# Cara ou Coroa — Histórico de versões

O número da versão fica em `app/build.gradle.kts` (`versionName` e `versionCode`) e aparece no fim da tela principal do app.

**Como numeramos:** `0.X.Y` enquanto o app ainda é um MVP em teste (X sobe quando entra uma funcionalidade nova; Y, para correções). A `1.0.0` será a primeira versão pronta para outras pessoas usarem. O `versionCode` é um número inteiro que sobe 1 a cada versão que for instalada por cima da anterior (o Android usa esse número).

| Versão | versionCode | O que tem |
|---|---|---|
| 0.1.0 | 1 | Projeto inicial: moeda desenhada por código, app e widget que giram com animação, último resultado compartilhado. (Antes se chamava "1.0" no código por ser o valor padrão do gerador.) |
| 0.2.0 | 2 | Pergunta e textos de cara e coroa; lápis do widget com a janelinha ("Salvar"); modos Cara ou Coroa?, melhor de 5 e 7 e livre; placar centralizado com ●/○ e sequência; vencedor grande com "Jogar novamente?"; "Zerar tudo"; deslize para cima no app; vibração com opção de desligar; "Zerar tudo" pequeno na tela do vencedor; letras maiores e limites de 20 e 40 caracteres; número da versão no app. |
