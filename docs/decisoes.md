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

### D03 — Uso principal: decisões rápidas ("Pedir delivery? Sim ou Não")
- **Data:** 15/11/2026 · **Status:** Vigente
- **Contexto:** o usuário vai usar o app para decidir coisas do dia a dia e quer operar principalmente pelo widget, de forma rápida.
- **Decisão:** a moeda continua sempre CARA ou COROA, mas o usuário pode digitar o que cada lado significa e, se quiser, uma pergunta. Embaixo da moeda aparece a pergunta e o texto do lado sorteado. O texto "Deu CARA!" foi retirado por pedido do usuário, porque a própria moeda já mostra o lado.
- **Fundamento:** mantém a identidade do app (cara ou coroa) e dá sentido ao resultado sem mudar o sorteio. Tudo é opcional, para não atrasar quem só quer girar.
- **Alternativas descartadas:** trocar CARA/COROA na moeda pelo texto digitado (a moeda ficaria ilegível e perderia o jeito de moeda); lista de decisões salvas (escopo maior; fica para depois, se fizer falta).
- **Consequências:** o app guarda mais 3 textos no aparelho (continua sem internet).

### D04 — Digitar pelo widget: botão de lápis com janelinha
- **Data:** 15/11/2026 · **Status:** Vigente
- **Contexto:** o usuário quer digitar a pergunta direto do widget. O Android não permite campo de texto dentro de widget (só botões, textos e imagens).
- **Decisão (atualizada: sem o "Deu CARA!" em texto):** o widget tem dois toques: na **moeda** gira na hora; no **lápis** abre uma janelinha (`QuickFlipActivity`, com tema de diálogo) por cima da tela inicial, onde o usuário digita e toca em "Girar". A janelinha fecha e a animação roda no próprio widget. A pergunta vale para um giro só.
- **Fundamento:** é o mais perto de digitar no widget que o Android permite, mantendo o giro de um toque para quem não quer digitar.
- **Alternativas descartadas:** digitar só no app (mais lento para o objetivo do usuário); lista de decisões pré-prontas no widget (mais código e telas, e o usuário não pediu).
- **Consequências:** uma tela nova (`QuickFlipActivity`). Com 3 linhas de texto, a moeda fica menor no widget 2x2: aumentar o widget ajuda (visual a refinar depois).

### D05 — Série "melhor de N" com placar
- **Data:** 15/11/2026 · **Status:** Parcialmente substituída pela D06 (a opção "1 vez", o "Venceu: [lado]" em texto e a pergunta limpa no fim da série mudaram; a ideia de "melhor de 5/7" continua)
- **Contexto:** o usuário às vezes decide jogando a moeda na melhor de cinco e quer um contador da opção vencedora.
- **Decisão:** o usuário marca uma opção fixa: **1 vez**, **melhor de 5** ou **melhor de 7** (padrão 1), no app e na janelinha. Cada toque na moeda é um giro; a série termina quando um lado chega à maioria (N/2 + 1) e aparece "Venceu: [lado]". O toque seguinte começa uma série nova.
- **Texto fixo abaixo da moeda (revisão pedida pelo usuário):** 4 linhas sempre nos mesmos lugares (pergunta, placar, sequência do que saiu em cada giro, vencedor). O texto não muda durante a animação (sem "Girando...") e a moeda não muda de tamanho. O widget padrão passou de 2x2 para 3x3 para caber o texto.
- **Fundamento:** "melhor de" termina assim que o resultado já está decidido, que é como o usuário joga. Opções fixas (1, 5, 7) são ímpares, então nunca dão empate, e marcar é mais rápido que digitar (importante no widget). Um toque por giro mantém o widget simples e deixa o usuário ver cada resultado; a sequência mostra o que saiu em cada giro, e não só o contador.
- **Alternativas descartadas:** campo para digitar qualquer número (mais lento, exigia validar par/ímpar); girar todos os N de uma vez com animação automática (mais código de animação e o usuário não vê cada giro); jogar sempre os N giros inteiros, mesmo com a série decidida (gasta giros à toa).
- **Consequências:** a pergunta passa a valer até o fim da série. Mudar a opção no meio da série zera o placar (não há botão de "zerar" separado). Rótulos muito longos aparecem cortados com "…" no widget. Um widget já colocado na tela mantém o tamanho antigo até o usuário redimensionar ou colocar de novo.

### D06 — Modo livre, vencedor em destaque, zerar tudo e limites de texto
- **Data:** 15/11/2026 · **Status:** Vigente
- **Contexto:** o usuário quer também ficar jogando a moeda sem fim, contando cara e coroa; quer o vencedor bem grande sobre a moeda com "Jogar novamente?"; quer um botão de zerar; e percebeu que textos de tamanhos diferentes deixavam o placar descentralizado.
- **Decisão:**
  - Modos marcáveis: **Livre (sem fim)** (padrão, substitui "1 vez"), **Melhor de 5** e **Melhor de 7**.
  - Quando a série (5 ou 7) termina, aparece sobre a moeda o vencedor em letras grandes, "venceu!" e o botão "Jogar novamente?" (zera placar e sequência, mantém modo, pergunta e textos). Com a série terminada, tocar na moeda não gira.
  - **"Zerar tudo"** (no app e na janelinha do lápis, com confirmação) apaga placar, sequência, pergunta, textos e último resultado, e volta ao modo livre.
  - Placar em 3 colunas (lado esquerdo, "2 x 1", lado direito) com as laterais de mesma largura, então o placar fica sempre centralizado. A sequência usa ● (lado da cara) e ○ (lado da coroa). Letras diminuem sozinhas para caber (autosize).
  - Limites: textos de cara e coroa com 12 caracteres; pergunta com 30.
  - A pergunta passa a ficar guardada até o usuário mudar ou zerar.
- **Fundamento:** o modo livre atende quem gosta de jogar sem fim; o vencedor grande deixa claro o fim da série; a confirmação evita apagar tudo sem querer; limites e colunas de mesma largura garantem layout estável com qualquer texto.
- **Alternativas descartadas:** botão de zerar direto no widget, sem confirmação (risco de toque acidental e pouco espaço); campo numérico para a quantidade de jogadas; manter o modo "1 vez" (o livre já cobre, só que contando); mostrar o vencedor só em texto pequeno.
- **Consequências:** o código ficou dividido: `Coin.kt` só desenha a moeda e `CoinStore.kt` guarda o estado do jogo. O widget padrão é 3x3. O histórico guarda só os 7 últimos giros. Um widget já colocado mantém o tamanho antigo até ser redimensionado ou colocado de novo.

### D07 — Deslizar para cima na moeda, só no app
- **Data:** 15/11/2026 · **Status:** Vigente
- **Contexto:** o usuário perguntou se o giro poderia ser por deslize em vez de toque.
- **Decisão:** no **app**, a moeda gira com toque **ou** com deslize do dedo para cima (`GestureDetector`). No **widget**, continua só o toque.
- **Fundamento:** o `RemoteViews` do widget só aceita toque, e o deslize na tela inicial já troca de página. Manter o toque no app evita que o usuário precise aprender o gesto.
- **Alternativas descartadas:** só deslize (quem não conhece o gesto fica sem saber girar); deslize no widget (tecnicamente impossível).
- **Consequências:** `MainActivity.kt` usa `GestureDetector` e pede ao `ScrollView` para não roubar o deslize na moeda.

### D08 — Modo livre recomeça quando o app sai da tela
- **Data:** 15/11/2026 · **Status:** Vigente (a primeira versão zerava ao abrir o app; o usuário pediu para zerar ao sair da tela)
- **Contexto:** o modo livre deixava o app estranho, com contagem antiga aparecendo; o usuário quer que ele recomece.
- **Decisão:** quando o app sai da tela (`onStop` da `MainActivity`), se o modo for livre, placar e sequência são zerados. Pergunta e textos continuam. Girar a tela do celular não zera (`isChangingConfigurations`). Melhor de 5 e 7 não são afetados.
- **Fundamento:** a contagem sem fim serve para uma sessão de jogo; guardar para sempre acumularia números sem sentido.
- **Alternativas descartadas:** zerar só ao abrir o app; zerar também pergunta e textos.
- **Consequências:** sair do app também zera o placar mostrado no widget.

### D09 — Quarta opção: girar só 1 vez (padrão)
- **Data:** 15/11/2026 · **Status:** Vigente (muda a D06: o padrão deixa de ser o modo livre)
- **Contexto:** o usuário achou o modo livre estranho como padrão e quer uma primeira opção só para girar uma vez.
- **Decisão:** as opções são, nesta ordem: **Girar só 1 vez** (padrão), **Livre (sem fim)**, **Melhor de 5** e **Melhor de 7**. No modo "1 vez" não há placar nem sequência: aparece a pergunta e, se o usuário digitou textos, o texto do lado que saiu (a moeda já mostra CARA ou COROA). Cada giro vale sozinho. "Zerar tudo" volta ao modo "1 vez".
- **Fundamento:** o uso principal é decidir rápido, e um giro só é o mais simples.
- **Alternativas descartadas:** manter o livre como padrão.
- **Consequências:** o valor guardado `series_size` passa a ter 1 (padrão), 0 (livre), 5 e 7.

---

## Histórico (linha do tempo)

| Data | O que aconteceu |
|---|---|
| 15/11/2026 | Ideia definida: cara ou coroa com widget animado. Primeiro código escrito em Kotlin. |
| 15/11/2026 | Projeto movido do Google Drive para `C:\dev\cara-ou-coroa` e ajustado ao padrão (nomes em inglês, documentação, licença). |

| 15/11/2026 | Instalados JDK 17, Gradle 8.7 e Android SDK 34 (sem Android Studio) e gerado o Gradle Wrapper. O app compilou sem erros e gerou o APK de teste. Ainda não testado no celular. |

| 15/11/2026 | Ativada a Plataforma do Hipervisor do Windows (`bcdedit /set hypervisorlaunchtype auto`) e criado o emulador `pixel_api34` (Android 34). O usuário testou no emulador: app e widget funcionam e a animação ficou fluida. Ainda falta testar em celular de verdade. |
| 15/11/2026 | Adicionados os textos de cara/coroa, a pergunta opcional e o botão de lápis com a janelinha no widget (D03 e D04). Compila; a janelinha ainda não foi testada pelo usuário. |
| 15/11/2026 | Removido o texto "Deu CARA!/COROA!" (a moeda já mostra o lado). Adicionada a série "melhor de N" com placar e vencedor (D05). Compila; a série ainda não foi testada pelo usuário. |
| 15/11/2026 | Corrigida a janelinha do lápis: opções 1/5/7 empilhadas (a 7 ficava cortada), janelinha com 90% da largura e sem barra de título repetida, conteúdo rolável e teclado só abre ao tocar em um campo. |
| 15/11/2026 | Revisão da série (D05): opções fixas 1 / 5 / 7, texto fixo em 4 linhas abaixo da moeda, sequência do que saiu em cada giro, widget padrão 3x3. Compila; ainda não testado. |
| 15/11/2026 | D08 e D09: o modo livre zera quando o app sai da tela, e entra a opção "Girar só 1 vez" como padrão. Compila e instalado no emulador; não testado. |
| 15/11/2026 | D07: no app, deslizar para cima na moeda também gira (widget só toque). Compila; não testado. |
| 15/11/2026 | D06: modo livre, vencedor grande com "Jogar novamente?", "Zerar tudo", placar centralizado em 3 colunas e limites de texto (12 e 30). Compila e foi conferido visualmente no emulador; o usuário ainda não testou e falta o celular de verdade. |

## Perguntas em aberto
- O widget anima de forma fluida em celulares de verdade? (no emulador, sim)
- Vale incluir vibração e som ao girar?
- Qual será o visual final da moeda (por exemplo, símbolos no lugar do texto)?
