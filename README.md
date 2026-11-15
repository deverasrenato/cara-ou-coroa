# Cara ou Coroa

**Gire a moeda direto da tela inicial.**

App de cara ou coroa para Android. O diferencial é o widget: o usuário toca nele, a moeda gira com animação e o resultado aparece, sem precisar abrir o app.

Projeto acadêmico de Análise e Desenvolvimento de Sistemas (ADS), desenvolvido em Kotlin com o Android Studio. Somente Android.

> **Status:** em desenvolvimento (MVP). O app compila, gera o APK de teste e funciona no emulador (app e widget com animação), mas ainda não foi testado em celular de verdade. Veja o [Roadmap](#roadmap).

## Como o app funciona
1. O usuário abre o app e toca na moeda (ou no botão "Girar moeda"): a moeda gira e mostra CARA ou COROA.
2. O usuário adiciona o widget à tela inicial (segurar a tela, Widgets, Cara ou Coroa).
3. Tocando na moeda do widget, ela gira com animação e o resultado aparece ali mesmo.
4. Para decidir algo específico, o usuário toca no lápis do widget e digita (tudo opcional) a pergunta e o que significa cara e coroa. Exemplo: "Pedir delivery?", cara = "Sim", coroa = "Não". A moeda para em CARA e, embaixo dela, aparece:

```
Pedir delivery?
Sim
```

5. O usuário escolhe o modo marcando uma opção: **Girar só 1 vez** (padrão, sem placar), **Livre (sem fim)** (conta cara e coroa até o app sair da tela), **Melhor de 5** ou **Melhor de 7**. Cada toque na moeda é um giro (no app, deslizar o dedo para cima na moeda também gira). Nos modos com placar, logo abaixo da moeda ficam linhas fixas e centralizadas: a pergunta, o placar e o que saiu em cada giro (● = lado da cara, ○ = lado da coroa):

```
Pedir delivery?
● Sim   2 x 1   Não ○
● ○ ●
```

6. Quando um lado ganha a série (3 vitórias na melhor de 5, 4 na melhor de 7), o vencedor aparece grande sobre a moeda, com o botão **"Jogar novamente?"**. Os giros que sobrariam não são jogados.
7. **"Zerar tudo"** (no app e na janelinha do lápis, com confirmação) apaga placar, pergunta e textos e volta ao modo "girar só 1 vez".
8. Limites de texto: 12 letras para cara e coroa, 30 para a pergunta. O app e o widget guardam o mesmo estado e mostram sempre o mesmo.

## Tecnologias
- [Kotlin](https://kotlinlang.org) e o [Android Studio](https://developer.android.com/studio)
- Layouts em XML (telas tradicionais do Android, sem bibliotecas extras além do AndroidX)
- App Widget (`AppWidgetProvider` e `RemoteViews`) para o widget da tela inicial

A moeda é desenhada por código (`Canvas`), sem imagens prontas. O código usa nomes em inglês e **comentários em português**, com o objetivo de ser simples e didático.

## Como rodar o projeto
**Pré-requisitos:** [Android Studio](https://developer.android.com/studio) (ele já traz o Java e o SDK do Android) e [Git](https://git-scm.com).

```bash
git clone https://github.com/deverasrenato/cara-ou-coroa.git
```

1. Abra a pasta `cara-ou-coroa` no Android Studio e espere o Gradle sincronizar (na primeira vez demora e baixa bastante coisa).
2. Rode no celular (com a depuração USB ligada) ou no emulador, pelo botão ▶.
3. Para testar o widget: segure a tela inicial, toque em **Widgets**, procure "Cara ou Coroa" e arraste para a tela.

> Dica: não coloque a pasta do projeto dentro do Google Drive ou do OneDrive. Use `C:\dev`.

## Gerar o APK (instalador para Android)
No Android Studio: **Build > Build Bundle(s) / APK(s) > Build APK(s)**. Ou, pelo terminal (com `JAVA_HOME` e `ANDROID_HOME` definidos):

```bash
.\gradlew.bat assembleDebug
```

O arquivo fica em `app/build/outputs/apk/debug/app-debug.apk`. Para instalar no celular, permita a instalação de fontes desconhecidas.

## Estrutura de pastas
```
cara-ou-coroa/
├── app/
│   └── src/main/
│       ├── java/com/deverasrenato/caraoucoroa/   # código Kotlin
│       │   ├── Coin.kt                # desenha a moeda e a animação
│       │   ├── CoinStore.kt           # guarda o estado do jogo (placar, textos, modo)
│       │   ├── CoinWidgetProvider.kt  # o widget
│       │   ├── QuickFlipActivity.kt   # a janelinha do lápis do widget
│       │   └── MainActivity.kt        # a tela do app
│       ├── res/                       # layouts, textos, cores e ícone
│       └── AndroidManifest.xml
├── docs/           # requisitos, plano de execução e decisões
├── build.gradle.kts, settings.gradle.kts, gradle.properties
└── CLAUDE.md       # contexto para o Claude Code
```

## Documentação
- [`docs/requisitos.md`](docs/requisitos.md): requisitos, histórias de usuário e regras de negócio.
- [`docs/plano-de-execucao.md`](docs/plano-de-execucao.md): estado atual, etapas e como retomar em outro computador.
- [`docs/decisoes.md`](docs/decisoes.md): histórico e registro das decisões, com o fundamento de cada uma.

## Roadmap
**Já existe no código (compila; ainda não testado no celular):**
- [x] Moeda desenhada por código, com giro, subida e sombra
- [x] Tela do app com botão "Girar moeda"
- [x] Widget (padrão 3x3) que gira ao toque
- [x] Último resultado compartilhado entre o app e o widget
- [x] Textos de cara e coroa e pergunta opcional (no app e na janelinha do widget)
- [x] Girar só 1 vez, modo livre e séries "melhor de 5/7" com placar e sequência
- [x] Vencedor grande sobre a moeda, "Jogar novamente?" e "Zerar tudo"

**Próximos passos:**
- [x] Compilar (APK de teste gerado por linha de comando)
- [x] Testar no emulador (app e widget funcionando)
- [ ] Testar em celular de verdade
- [ ] Ajustar a fluidez da animação do widget
- [ ] Ícone final e visual do widget
- [ ] Vibração e som ao girar (opcional)
- [ ] APK de teste

## Privacidade
Nenhum dado sai do aparelho. O app guarda apenas o último resultado (cara ou coroa) no próprio celular, e não usa internet nem pede permissões.

## Licença
**Copyright (c) 2026 Renato Veras. Todos os direitos reservados.** O texto completo está em [`LICENSE`](LICENSE), em inglês, com a tradução em português ao final.

---

Desenvolvido por **Renato Veras**, estudante de ADS.
