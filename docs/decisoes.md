# Cara ou Coroa — Histórico e decisões

Este arquivo registra **o que foi decidido, por quê, e o que foi descartado**. Serve para quem retomar o projeto (inclusive você mesmo, daqui a meses) entender os motivos das escolhas sem depender de memória ou de conversas antigas.

**Como manter:** toda decisão relevante ganha uma entrada nova no fim da seção "Decisões", com o próximo número (D03, D04…). Se uma decisão mudar, não apague a antiga: marque como **Substituída** e crie uma nova entrada explicando a mudança.

**Formato de cada decisão:** Contexto, Decisão, Fundamento (por que), Alternativas descartadas e Consequências.

---

## Decisões

### D01 — Tecnologia: Kotlin nativo com Android Studio (exceção ao padrão Expo)
- **Data:** 15/11/2026 · **Status:** Vigente
- **Contexto:** o padrão em `C:\dev\_padrao-apps` usa Expo + React Native + TypeScript. Mas o diferencial deste app é um widget na tela inicial com animação, e isso é uma função nativa do Android. O dono do projeto autorizou usar Kotlin e o Android Studio, desde que o código respeite o nível do 2º período.
- **Decisão:** usar Kotlin com layouts XML, aberto no Android Studio. Somente Android.
- **Fundamento:** o widget (`AppWidgetProvider` e `RemoteViews`) é uma API nativa; no Expo seria preciso uma biblioteca de terceiros, um build nativo (não funciona no Expo Go) e mais camadas para entender. Em Kotlin o widget é o caminho direto e documentado pelo Android.
- **Alternativas descartadas:** Expo com `react-native-android-widget` (mais peças, sem Expo Go, menos documentação); Flutter (exigiria aprender Dart e também depende de código nativo para widget); Jetpack Compose e Glance (modernos, mas conceitos avançados demais para o nível atual).
- **Consequências:** não há iOS; o teste é no celular ou emulador pelo Android Studio (não no Expo Go); o APK sai do Android Studio, e não do EAS Build. O resto do padrão continua: documentação, idioma, Git e licença.

### D02 — A moeda é desenhada por código, sem imagens
- **Data:** 15/11/2026 · **Status:** Vigente
- **Contexto:** o widget só consegue animar trocando a imagem várias vezes seguidas, e o app precisa da mesma moeda.
- **Decisão:** desenhar cada quadro da animação em um `Bitmap` usando `Canvas` (arquivo `Coin.kt`). O widget troca a imagem 18 vezes, a cada 70 ms, em uma thread separada. O app usa o mesmo desenho com um `ValueAnimator`.
- **Fundamento:** um desenho só para o app e o widget; nenhuma imagem para preparar; fácil mudar cor, tamanho e velocidade.
- **Alternativas descartadas:** sequência de PNGs prontos (muitos arquivos); `AnimationDrawable` (não funciona de forma confiável em `RemoteViews`); GIF (o widget não anima GIF).
- **Consequências:** a fluidez do widget depende do launcher (tela inicial) do celular, que pode limitar a velocidade das atualizações. Se ficar travado, ajustar `FRAME_COUNT` e `FRAME_DELAY_MS` em `Coin.kt`.

---

## Histórico (linha do tempo)

| Data | O que aconteceu |
|---|---|
| 15/11/2026 | Ideia definida: cara ou coroa com widget animado. Primeiro código escrito em Kotlin. |
| 15/11/2026 | Projeto movido do Google Drive para `C:\dev\cara-ou-coroa` e ajustado ao padrão (nomes em inglês, documentação, licença). |

| 15/11/2026 | Instalados JDK 17, Gradle 8.7 e Android SDK 34 (sem Android Studio) e gerado o Gradle Wrapper. O app compilou sem erros e gerou o APK de teste. Ainda não testado no celular. |

## Perguntas em aberto
- O widget anima de forma fluida nos celulares do usuário? (testar)
- Vale incluir vibração e som ao girar?
- Qual será o visual final da moeda (por exemplo, símbolos no lugar do texto)?
