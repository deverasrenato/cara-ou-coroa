# Cara ou Coroa

**Gire a moeda direto da tela inicial.**

App de cara ou coroa para Android. O diferencial é o widget: o usuário toca nele, a moeda gira com animação e o resultado aparece, sem precisar abrir o app.

Projeto acadêmico de Análise e Desenvolvimento de Sistemas (ADS), desenvolvido em Kotlin com o Android Studio. Somente Android.

> **Status:** em desenvolvimento (MVP). O app compila e gera o APK de teste, mas ainda não foi testado no celular. Veja o [Roadmap](#roadmap).

## Como o app funciona
1. O usuário abre o app e toca na moeda (ou no botão "Girar moeda"): a moeda gira e mostra CARA ou COROA.
2. O usuário adiciona o widget à tela inicial (segurar a tela, Widgets, Cara ou Coroa).
3. Tocando no widget, a moeda gira com animação e o resultado aparece ali mesmo.
4. O app e o widget guardam o último resultado e mostram sempre o mesmo.

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
│       │   ├── CoinWidgetProvider.kt  # o widget
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
- [x] Widget 2x2 que gira ao toque
- [x] Último resultado compartilhado entre o app e o widget

**Próximos passos:**
- [x] Compilar (APK de teste gerado por linha de comando)
- [ ] Testar no celular
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
