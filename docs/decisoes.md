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

### D10 — Letras maiores, limites maiores e "Zerar tudo" em destaque
- **Data:** 15/11/2026 · **Status:** Vigente (muda os limites da D06: 12 e 30 viram 20 e 40)
- **Contexto:** o usuário testou em um celular de verdade e achou as letras do texto abaixo da moeda pequenas, os 12 caracteres pouco e o "Zerar tudo" discreto demais.
- **Decisão:** letras do texto da moeda maiores (widget até 16 sp, app até 24 sp), com a letra diminuindo sozinha só quando precisa; cada lado do placar pode usar até 2 linhas; limites de 20 caracteres para cara e coroa e 40 para a pergunta; "Zerar tudo" vira um botão largo com contorno vermelho claro, no app e na janelinha.
- **Fundamento:** com mais espaço por linha, textos maiores continuam centralizados; a cor chama atenção para uma ação que apaga dados (a confirmação continua).
- **Alternativas descartadas:** manter 1 linha por lado e só aumentar a letra (cortaria textos longos); pôr "Zerar tudo" no widget (risco de toque acidental).
- **Consequências:** o texto ocupa mais altura, então a moeda fica um pouco menor no widget; aumentar o widget na vertical devolve o espaço.

### D11 — "Salvar" na janelinha, vibração e versão do app
- **Data:** 15/11/2026 · **Status:** Vigente (muda a D04: a janelinha não gira mais, só salva)
- **Contexto:** o usuário testou no celular dele e quis separar "configurar" de "jogar", ter vibração com opção de desligar, e ver a versão do app. Também pediu que o widget não gaste bateria em segundo plano.
- **Decisão:** o botão da janelinha vira **Salvar**: guarda modo, pergunta e textos, zera placar, sequência e último resultado, e o widget fica em "Toque para girar" mostrando a pergunta. A janelinha avisa que o jogo atual será zerado. A moeda vibra 40 ms ao parar e 150 ms no vencedor; a opção "Vibrar quando a moeda parar" fica no app, ligada por padrão, e "Zerar tudo" não a desliga. A versão (`versionName` 0.2.0) aparece no fim da tela do app.
- **Fundamento:** o novo jogo começa limpo e o usuário decide quando girar. A vibração só pede a permissão VIBRATE, que o Android concede sem perguntar. Nada roda em segundo plano: `updatePeriodMillis` é 0 e não há serviço nem alarme, então o widget só trabalha por cerca de 1,3 segundo quando o usuário toca.
- **Alternativas descartadas:** dois botões (salvar e salvar-e-girar); confirmação ao trocar de modo no app (só valeria se afetasse o desempenho); atualizar o widget de tempos em tempos (gastaria bateria).
- **Consequências:** o fluxo pelo lápis ficou com 3 passos (lápis, digitar e salvar, tocar na moeda). A permissão do app deixou de ser "nenhuma". Há um histórico de versões em `docs/versoes.md`.

### D12 — Modo "Cara ou Coroa?", ordem das opções e rótulos dos campos
- **Data:** 15/11/2026 · **Status:** Vigente (muda a D09)
- **Contexto:** o usuário quis um nome melhor para o modo de um giro, o modo livre por último, o mesmo "Jogar novamente?" depois de um giro, e rótulos que não sumissem ao digitar.
- **Decisão:** as opções são **Cara ou Coroa?** (padrão), **Melhor de 5**, **Melhor de 7** e **Livre (sem fim)**. No modo de um giro, o resultado aparece grande sobre a moeda (sem "venceu!") com "Jogar novamente?". Os campos ganharam rótulos fixos "Pergunta", "Cara" e "Coroa". O placar ficou com ●/○ em cima dos números e os nomes alinhados pela linha dos números; a linha da sequência ficou amarela e em negrito.
- **Fundamento:** o modo de um giro é uma série de 1 vitória, então reaproveita toda a lógica das séries. Rótulos fixos continuam visíveis depois de digitar (a dica do campo some).
- **Alternativas descartadas:** manter "venceu!" no modo de um giro (ficaria "CARA venceu!"); usar só as dicas dos campos.
- **Consequências:** nenhuma mudança nos dados guardados (`series_size` continua 1, 0, 5 ou 7).

### D13 — "Zerar tudo" na tela do vencedor
- **Data:** 15/11/2026 · **Status:** Vigente (muda a D10, que descartou o botão no widget por risco de toque acidental)
- **Contexto:** quando a série termina, o usuário decide entre jogar de novo ou começar outra coisa.
- **Decisão:** abaixo do "Jogar novamente?" há um botão pequeno "Zerar tudo" em vermelho claro, no app e no widget. Nos dois abre a confirmação "Zerar tudo?". No widget, o botão abre a `ConfirmResetActivity`, uma tela transparente que só mostra a caixa de confirmação.
- **Fundamento:** o botão fica pequeno e discreto, e a confirmação evita apagar por engano. O toque natural continua sendo o "Jogar novamente?", em amarelo.
- **Alternativas descartadas:** botão sem confirmação no widget; deixar o widget só com o lápis (um toque a mais).
- **Consequências:** uma tela nova (`ConfirmResetActivity`) e um tema transparente. A função `askResetAll` ganhou o parâmetro `afterClose`.

### D14 — Nome do app: Moedinha
- **Data:** 15/11/2026 · **Status:** Vigente
- **Contexto:** o usuário escolheu um nome próprio para o app. A pasta e o repositório continuam `cara-ou-coroa`.
- **Decisão:** o nome que aparece para o usuário (`app_name`, título da tela e dica do widget) passa a ser **Moedinha**. O `applicationId` continua `com.deverasrenato.caraoucoroa`.
- **Fundamento:** trocar o `applicationId` faz o Android tratar como outro app (o celular instalaria uma segunda cópia e o placar salvo ficaria na antiga). Antes de publicar, ainda vale trocar, porque não haverá usuários para perder dados.
- **Alternativas descartadas:** trocar o `applicationId` agora; renomear a pasta e o repositório (quebraria o vínculo com o GitHub sem ganho).
- **Consequências:** o código e os identificadores continuam com o nome antigo (`CoinWidgetProvider`, tema `Theme.CaraOuCoroa`).

### D15 — Identidade visual: meia-noite e ouro, moeda em relevo, ícone B e fontes
- **Data:** 15/11/2026 · **Status:** Vigente (muda o visual provisório; a moeda deixa de ter texto)
- **Contexto:** o usuário achou o app "triste". O design foi explorado num canvas (cores, telas, widget, moeda e ícone) e aprovado antes de mexer no código.
- **Decisão:**
  - **Cores:** azul meia-noite (`#0B1030` e `#151A45`) com ouro (`#FFC83D`), cartões translúcidos, botão principal dourado em pílula e "Zerar tudo" em vermelho claro.
  - **Moeda:** continua desenhada por código (`Coin.kt`), agora com degradê dourado, aro, anel e uma silhueta em relevo baixo, **sem texto**: uma pessoa neutra (cara) e uma coroa (coroa). O relevo é a mesma silhueta desenhada três vezes (sombra, luz e cor principal).
  - **Ícone:** opção B, uma moeda girando com uma coroa pequena, como ícone adaptativo (`mipmap-anydpi-v26`).
  - **Fontes:** Baloo 2 (títulos e botões) e Nunito (textos), dentro do app em `res/font`, ambas com licença OFL (cópias em `docs/OFL-baloo2.txt` e `docs/OFL-nunito.txt`). O widget (`RemoteViews`) continua com a fonte do sistema, porque não aceita fonte própria.
  - Tema escuro fixo; opções de modo viram botões largos com contorno dourado quando marcadas.
- **Fundamento:** o lado da moeda se reconhece pela silhueta, então o texto some e o desenho fica limpo. Desenhar por código mantém o app leve e o código simples; as fontes variáveis aumentam o APK em cerca de 1 MB, o que é aceitável.
- **Alternativas descartadas:** imagens prontas da moeda (mais pesadas e difíceis de animar); texto sobre a moeda (poluía o desenho); fonte do sistema no app todo; tema claro/escuro automático (as cores foram pensadas para o escuro).
- **Ajustes depois do primeiro teste no emulador:** (1) trocar "Quantas vezes jogar?" no app agora começa um jogo novo na hora (placar zerado, moeda parada, camada do vencedor some) e cancela um giro em andamento; antes, o modo só era guardado ao sair da tela ou girar, e a camada "Jogar novamente?" ficava sobre a moeda. (2) No modo "Cara ou Coroa?", o cartão abaixo da moeda mostra o lado que saiu (CARA ou COROA) e, se houver, o texto digitado, no app e no widget, para não ficar vazio.
- **Consequências:** a pergunta "Qual será o visual final da moeda?" foi respondida. O `contentDescription` da moeda no app diz o lado ("CARA"/"COROA") para leitores de tela. Falta conferir no celular do usuário (letra pequena do S23) e no widget.

### D16 — Sem "Melhor de 7"; nome do lado embaixo da moeda; vidro translúcido do vencedor
- **Data:** 15/11/2026 · **Status:** Vigente (muda a D12 e a D15)
- **Contexto:** depois de testar o novo visual, o usuário achou o cartão do placar grande e vazio, quis ver "CARA" ou "COROA" escrito embaixo da moeda (app e widget), dispensou o "Melhor de 7" e quis a camada do vencedor parecendo estar por cima da moeda.
- **Decisão:** modos: **Cara ou Coroa?**, **Melhor de 5** e **Livre (sem fim)**, lado a lado como no canvas (um 7 guardado vira o padrão). O nome do lado que saiu aparece em dourado embaixo da moeda depois do giro. No modo de 1 giro a linha do placar some, deixando o cartão menor. O fundo da camada do vencedor virou vidro escuro translúcido (70%) com contorno claro. O app ganhou o brilho dourado atrás da moeda e o cartão ficou mais compacto.
- **Fundamento:** o nome escrito confirma o resultado (a moeda não tem mais texto) e mostra que o widget funcionou; o vidro translúcido deixa a moeda aparecer por baixo.
- **Alternativas descartadas:** manter o "Melhor de 7"; mostrar o nome só no cartão.
- **Ajustes depois do segundo teste:** (1) o nome do vencedor tem tamanho automático e altura fixa, então "Zerar tudo" não sai da camada quando o texto tem duas linhas; (2) girar com a série terminada (botão "Girar moeda", moeda do app ou moeda do widget) entende "jogar novamente": zera o placar, tira a camada e já gira; (3) a camada do vencedor ficou quase opaca (92%), porque a versão de 70% dificultava a leitura; (4) no modo de 1 giro, depois de "Jogar novamente?" o widget mostra "Cara ou Coroa?" (ou a pergunta) e "Toque para girar", em vez de ficar vazio; (5) o fundo do widget usa o mesmo degradê do app e o mesmo brilho atrás da moeda.
- **Ajustes depois do terceiro teste:** (1) o balão do vencedor no app ficou mais compacto (nome com altura de 68 dp), para "Zerar tudo" caber com nome de duas linhas; (2) ao girar com o balão aberto, o balão fecha primeiro (200 ms no app, 250 ms no widget) e só depois a moeda gira; toques durante o giro são ignorados; (3) na janelinha, os botões de modo não alinham mais pela linha do texto (`baselineAligned="false"`) e o título repetido "Moedinha" foi escondido.
- **Consequências:** o `series_7` saiu dos layouts e do código; o valor 7 deixou de ser aceito em `getSeriesSize`.

### D17 — Regras de início e fim do jogo (estados e uma só função de início)
- **Data:** 15/11/2026 · **Status:** Vigente
- **Contexto:** o app tinha três jeitos de recomeçar (zerar só o placar, zerar placar e último lado, apagar tudo) e cada tela escolhia um. Isso gerou remendos (`hasResult` e `hasPlayed`, moeda mostrando um lado antigo depois de "Jogar novamente?", widget vazio) e dados "bagunçados".
- **Decisão:** separar **configuração** (modo, pergunta, textos, vibração) de **jogo** (placar, sequência, último lado). O jogo tem três estados, calculados por `CoinStore.gameState()`: **READY** (nada girou), **PLAYING** (girou, série aberta) e **FINISHED** (um lado venceu; nunca no modo livre). Só `CoinStore.startNewGame()` começa um jogo novo, e é chamada ao: salvar na janelinha, trocar o modo, jogar novamente ou girar depois do fim, e sair do app no modo livre. "Zerar tudo" continua apagando configuração e jogo.
- **Fundamento:** um único lugar manda no jogo, então app e widget nunca discordam. As telas só perguntam o estado (`currentBoard`, `resultText`) em vez de deduzir pelo placar.
- **Alternativas descartadas:** manter as três funções com comentários; guardar o estado como um campo próprio (o placar já diz tudo, e um campo duplicado poderia sair de sincronia).
- **Ajuste final:** depois do resultado, o cartão repete a mensagem do início: a pergunta (ou "Cara ou Coroa?") em cima e o convite ("Toque na moeda" no app, "Toque para girar" no widget) embaixo; o texto digitado para o lado que saiu fica no meio.
- **Consequências:** `resetScores` virou privada (`clearScores`) e `hasResult` foi removida. Depois de "Jogar novamente?" a moeda volta a mostrar CARA (o jogo está pronto, sem lado "anterior").

### D18 — Abertura (splash) e boas-vindas com destaque para o widget
- **Data:** 15/11/2026 · **Status:** Vigente
- **Contexto:** o usuário achou a abertura branca do Android feia e quis uma tela com o nome, e que o app ensinasse a adicionar o widget (o diferencial). As telas foram desenhadas e aprovadas antes no canvas (Abertura e Boas-vindas).
- **Decisão:** `SplashActivity` é a primeira tela (LAUNCHER): fundo meia-noite, moeda girando devagar (mesmo desenho do app, 4 meios giros em 2,6 s, via `Coin.frame(..., baseTurns = 4)`), nome "Moedinha" e versão. Depois vai para `WelcomeActivity` (só na primeira vez, ou sempre se o usuário pediu) ou direto para `MainActivity`. As boas-vindas mostram uma tela inicial de exemplo com o widget pulsando e 3 passos. O botão "Entendi, abrir o app" é o principal; o botão de baixo alterna entre "Não mostrar de novo" (com ✓) e "Mostrar sempre que abrir o app". Na tela principal há um botão para rever as boas-vindas.
- **Fundamento:** quem instala precisa descobrir o widget logo, sem virar chatice (por isso só na primeira vez, com escolha). No Android 12+ a abertura do sistema usa a mesma cor de fundo (`values-v31`), então não pisca branco.
- **Alternativas descartadas:** deixar só a abertura do sistema (sem nome nem animação); mostrar as boas-vindas sempre (cansativo); biblioteca de splash (código mais avançado que o necessário).
- **Consequências:** `welcome_seen` e `welcome_always` ficam nas preferências e "Zerar tudo" não os apaga (como a vibração). `MainActivity` deixou de ser a tela de entrada.

### D19 — Layouts salvos ("Meus layouts")
- **Data:** 15/11/2026 · **Status:** Vigente
- **Contexto:** o usuário decide coisas parecidas várias vezes ("Pedir delivery? Sim/Não") e trocar de assunto exigia mudar modo, pergunta e textos à mão.
- **Decisão:** o usuário salva até **5 layouts**, cada um com **nome, modo, pergunta e textos de cara e coroa**. Tocar em um layout configura tudo, começa um jogo novo e atualiza o widget. Segurar o dedo pergunta se quer apagar. O botão "Salvar como layout" (no app e na janelinha do lápis) guarda o que está nos campos naquele momento; nome repetido substitui o layout, e com 5 salvos avisa para apagar um. Vibração e boas-vindas continuam preferências gerais, fora do layout.
- **Armazenamento:** arquivo de preferências separado (`presets`), com 5 vagas (`p0_name`, `p0_mode`, `p0_question`, `p0_heads`, `p0_tails`, e assim por diante). Por ser separado, o "Zerar tudo" não apaga os layouts.
- **Fundamento:** guardar pergunta e textos junto com o modo deixa o widget pronto com um toque, que é o objetivo do app. Cinco vagas cabem numa fileira curta, sem rolagem de lista, e mantêm o código simples (`SharedPreferences`, sem banco de dados).
- **Alternativas descartadas:** guardar só pergunta e textos (o modo teria de ser trocado à mão); incluir a vibração no layout (é preferência do aparelho); lista ilimitada (exigiria rolagem, apagar e reordenar); layouts só no app (o widget perderia o sentido de ser rápido).
- **Consequências:** novo arquivo `Presets.kt` e o desenho `preset_chip.xml`. A confirmação do "Zerar tudo" avisa que os layouts continuam. O widget não mudou. Versão 0.5.0.

### D20 — Tela "Sobre o desenvolvedor" e "Doe um café" pela Play
- **Data:** 15/11/2026 · **Status:** Planejada (ainda não implementada)
- **Contexto:** o usuário quer apresentar-se como desenvolvedor no app (para portfólio), mostrar como encontrá-lo e oferecer "Doe um café para o dev".
- **Decisão:** primeiro, uma tela "Sobre o desenvolvedor", aberta por um link discreto no fim da tela principal, com apresentação e links de perfil que abrem no navegador, sem pedido de dinheiro. Depois, quando o app estiver na loja, o "Doe um café" como **produto avulso do Google Play Billing** (compra feita dentro da própria Play Store), com a biblioteca oficial.
- **Fundamento:** o link de perfil não exige permissão nem biblioteca e já ajuda o portfólio. Pagar pelo Google Play Billing é o caminho aceito pela loja para cobrar dentro do app; um link de pagamento por fora corre o risco de ser recusado pela política de Pagamentos. Não dá para testar o Billing com APK instalado à mão, por isso ele fica para depois da publicação em teste.
- **Alternativas descartadas:** PIX ou página de apoio (Buy Me a Coffee, Ko-fi) dentro do app (risco com a política da loja e expõe dados do usuário); começar já pelo Billing (exige conta, perfil de pagamentos e app na loja).
- **Consequências:** a seção de privacidade e o formulário "Segurança dos dados" precisarão ser ajustados na Fase E; a regra "poucas bibliotecas novas" ganha uma exceção (Play Billing). Detalhes em `docs/publicacao.md`.

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
| 15/11/2026 | Planejamento da publicação: criado `docs/publicacao.md` (fases A a E) e registrada a D20 (tela "Sobre" e "Doe um café" pela Play). Nada implementado. |
| 15/11/2026 | D19: layouts salvos (até 5), no app e na janelinha do lápis. Versão 0.5.0. Compila e instalado no emulador; ainda não testado por toque. |
| 15/11/2026 | D18: abertura com moeda girando e boas-vindas do widget. Compila e instalado no emulador; as duas telas foram conferidas por captura; o botão de rever e o botão de alternar ainda não foram testados por toque. |
| 15/11/2026 | D14 e D15: app renomeado para Moedinha; novo visual (meia-noite e ouro), moeda em relevo sem texto, ícone B e fontes Baloo 2 e Nunito. Compila e instalado no emulador; a tela principal foi conferida por captura, o widget e a janelinha ainda não; não testado no celular. |
| 15/11/2026 | D13: botão pequeno "Zerar tudo" abaixo do "Jogar novamente?", no app e no widget, com confirmação. Compila e instalado no emulador; não testado. |
| 15/11/2026 | D11 e D12: "Salvar" na janelinha, vibração com opção de desligar, versão 0.2.0 no app, modo "Cara ou Coroa?", nova ordem das opções e rótulos fixos nos campos. Compila e instalado no emulador; ainda não testado no celular. |
| 15/11/2026 | Teste no celular de verdade: tudo funcionou. D10: letras maiores, limites 20 e 40 e "Zerar tudo" em destaque. Compila; ainda não testado no celular. |
| 15/11/2026 | D08 e D09: o modo livre zera quando o app sai da tela, e entra a opção "Girar só 1 vez" como padrão. Compila e instalado no emulador; não testado. |
| 15/11/2026 | D07: no app, deslizar para cima na moeda também gira (widget só toque). Compila; não testado. |
| 15/11/2026 | D06: modo livre, vencedor grande com "Jogar novamente?", "Zerar tudo", placar centralizado em 3 colunas e limites de texto (12 e 30). Compila e foi conferido visualmente no emulador; o usuário ainda não testou e falta o celular de verdade. |

## Perguntas em aberto
- O widget anima de forma fluida em celulares de verdade? (no emulador, sim)
- Vale incluir vibração e som ao girar?
- O widget fica bom com o novo visual em celulares de verdade (letra pequena do S23)?
