# Cara ou Coroa — Plano de execução

Plano pensado para quem está no **2º período de ADS**: poucas coisas por vez, cada etapa entrega algo que dá para ver rodando, e cada etapa ensina um conceito novo. Atualizado em 15/11/2026.

## 1. Onde estamos
**Já existe no código (compila, gera o APK e foi testado no emulador; falta testar em celular de verdade):**
- Moeda desenhada por código, com giro, subida e sombra (`Coin.kt`).
- Tela do app com botão "Girar moeda" (`MainActivity.kt`).
- Widget (padrão 3x3) que gira ao toque (`CoinWidgetProvider.kt`).
- Estado do jogo guardado no aparelho e compartilhado entre o app e o widget (`CoinStore.kt`).
- Modos "Cara ou Coroa?" (padrão), melhor de 5/7 e livre, vencedor grande, "Jogar novamente?" e "Zerar tudo" (D06, D08, D09, D12).
- Janelinha com "Salvar", vibração com opção de desligar e número da versão (D11, D12).
- No app, deslizar para cima na moeda também gira (D07).
- Novo visual (D14, D15): nome Moedinha, meia-noite e ouro, moeda em relevo sem texto, ícone novo e fontes Baloo 2 e Nunito. A tela principal foi conferida no emulador; widget e janelinha ainda não.
- Layouts salvos (D19, versão 0.5.0): até 5, com modo, pergunta e textos; escolhidos no app ou na janelinha do lápis. Compila e instalado no emulador; ainda não testado por toque.
- Abertura (moeda girando) e boas-vindas do widget (D18): conferidas por captura no emulador; botões ainda não testados por toque.

**Ainda não existe:**
- Teste do novo visual em celular de verdade (no emulador já funciona).
- Som.
- Testes em mais celulares (o do usuário é um Galaxy S23 com letra pequena; o emulador se comportou melhor).

## 2. O app, em uma frase
Um cara ou coroa para Android em que o usuário gira a moeda, com animação, direto de um widget na tela inicial, sem abrir o app.

## 3. Escopo do MVP, enxuto
**Entra:** girar no app; girar no widget com animação; mostrar o resultado; app e widget com o mesmo último resultado.

**Fica de fora por enquanto:** histórico de resultados, estatísticas, sons, vibração, escolha de moeda, widgets de tamanhos diferentes, iOS.

Cortar cedo é o que garante que o MVP fique pronto.

## 4. Etapas
### Etapa 0 — Base segura
- Instalar o Android Studio e abrir a pasta `C:\dev\cara-ou-coroa`.
- Esperar o Gradle sincronizar e corrigir erros de compilação, se houver.
- Criar o repositório privado no GitHub e enviar os commits.
- **Pronto quando:** o app compila, abre no celular (ou emulador) e o código está no GitHub.
- **Você aprende:** Android Studio, Gradle, Git e GitHub (commit, push), rodar um app de verdade.

### Etapa 1 — App com a moeda
- Testar o giro no app e ler o código de `Coin.kt` e `MainActivity.kt`.
- **Pronto quando:** tocar na moeda gira e mostra CARA ou COROA.
- **Você aprende:** `Canvas` e `Bitmap` (desenhar por código), `ValueAnimator` (animação), `SharedPreferences` (guardar um dado simples).

### Etapa 2 — Widget
- Adicionar o widget à tela inicial e testar o toque.
- Ajustar `FRAME_COUNT` e `FRAME_DELAY_MS` se a animação ficar travada.
- **Pronto quando:** tocar no widget gira a moeda e mostra o resultado, sem abrir o app.
- **Você aprende:** `AppWidgetProvider`, `RemoteViews`, `PendingIntent` (um "aviso" que o Android entrega depois) e `Thread`.

### Etapa 2.5 — Decisões rápidas (textos e pergunta)
- Textos de cara e coroa, pergunta opcional e o lápis do widget com a janelinha.
- **Pronto quando:** pelo widget, o usuário digita "Pedir delivery?" com cara = "Sim" e coroa = "Não", toca em "Salvar" e depois na moeda, e vê o resultado com esses textos.
- **Você aprende:** `EditText` (campo de texto), mais de uma tela (`Activity`), tema de diálogo, `SharedPreferences` com texto.
- **Situação:** código pronto e compilando; falta o usuário testar.

### Etapa 2.6 — Modos de jogo (Cara ou Coroa?, melhor de 5 e 7, livre)
- Opções Cara ou Coroa? / melhor de 5 / Livre (marcar), linhas fixas e centralizadas abaixo da moeda (pergunta, placar em 3 colunas, sequência ●/○).
- **Pronto quando:** no modo livre o placar conta sem fim; em "melhor de 5", cada toque gira uma vez, o texto não pula e ao chegar a 3 vitórias o vencedor aparece grande sobre a moeda.
- **Você aprende:** `RadioGroup` (opções de marcar), `SharedPreferences` com mais de um dado, estado que dura mais de um toque.
- **Situação:** código pronto, compilando e conferido visualmente no emulador (D06); falta o usuário testar.

### Etapa 2.7 — Vencedor, jogar novamente e zerar
- Camada do vencedor sobre a moeda, botão "Jogar novamente?", "Zerar tudo" com confirmação e limites de texto (20 e 40).
- **Pronto quando:** ao ganhar a série a moeda não gira mais até tocar em "Jogar novamente?"; "Zerar tudo" volta ao modo "Cara ou Coroa?" sem pergunta nem textos.
- **Você aprende:** sobrepor telas (`FrameLayout`), `AlertDialog` de confirmação, `autoSize` do texto.
- **Situação:** código pronto e compilando; ainda não testados por toque: "Jogar novamente?" e "Zerar tudo". Falta testar no celular de verdade.

### Etapa 3 — Acabamento
- Ícone, cores, fontes, moeda em relevo e nome Moedinha (D14, D15).
- **Pronto quando:** o visual está bom o suficiente para mostrar a outras pessoas.
- **Você aprende:** recursos do Android (`res/`), `Path` e `RadialGradient` para desenhar, fontes e design.
- **Situação:** código pronto e compilando; tela principal conferida no emulador. Falta conferir widget e janelinha e testar no celular.

### Etapa 4 — APK de teste
- Gerar o APK pelo Android Studio e instalar em outro celular.
- **Pronto quando:** o app funciona instalado, fora do Android Studio.

### Etapas 5 a 8 — Fechar e publicar
O passo a passo completo, com listas de verificação, está em [`publicacao.md`](publicacao.md). Resumo:
- **Etapa 5 (Fase A):** fechar o MVP com testes por toque no emulador e no S23.
- **Etapa 6 (Fase B):** tela "Sobre o desenvolvedor" (D20), versão 0.6.0.
- **Etapa 7 (Fases C e D):** nome e `applicationId` definitivos, `targetSdk` 36, AAB assinado, política de privacidade, ficha da loja, conta na Play, teste fechado (12 testadores por 14 dias) e produção, versão 1.0.0.
- **Etapa 8 (Fase E):** "Doe um café" pelo Google Play Billing, só depois de publicado (D20).

## 5. Definição de "pronto" para qualquer etapa
- [ ] O projeto compila no Android Studio sem erros.
- [ ] Testei no celular ou no emulador, e o app abre sem erros.
- [ ] Eu consigo explicar o que o código novo faz.
- [ ] README, requisitos e `docs/decisoes.md` estão atualizados.
- [ ] Fiz o commit com uma mensagem clara e o `git push`.

## 6. Regras para trabalhar no 2º período
1. **Uma coisa por vez.**
2. **Commits pequenos e frequentes.**
3. **Entenda antes de seguir.** Peça explicações; os comentários em português existem para isso.
4. **Poucas bibliotecas novas.** Hoje só usamos `core-ktx` e `appcompat`. A única exceção prevista é o Google Play Billing, na Etapa 8 (D20).
5. **Registre decisões** em `docs/decisoes.md`.
6. **Teste no celular a cada etapa**, não só no final.

## 7. Como retomar em outro computador
1. Instale o **Android Studio** e o **Git**. Feche e reabra o terminal.
2. Escolha uma pasta **local** (por exemplo `C:\dev`), fora do Google Drive ou do OneDrive.
3. Clone o projeto:

```bash
git clone https://github.com/deverasrenato/cara-ou-coroa.git
```

4. Configure sua identidade **só neste repositório**: `git config user.name "[Nome]"` e `git config user.email "[email noreply]"`.
5. Abra a pasta no Android Studio e espere o Gradle sincronizar.
6. Abra o Claude Code **dentro da pasta do projeto** e peça para ele ler `CLAUDE.md`, este arquivo e `docs/decisoes.md`.
7. Antes de começar qualquer trabalho: `git pull`. Ao terminar: `git add`, `git commit` e `git push`.

## 8. O que ainda preciso saber
- **Prazo e formato da entrega** da matéria, se houver.
- Se o professor exige alguma tecnologia específica (este app usa Kotlin; veja D01).
- Quanto tempo por semana posso dedicar ao projeto.
