# Cara ou Coroa — Plano de execução

Plano pensado para quem está no **2º período de ADS**: poucas coisas por vez, cada etapa entrega algo que dá para ver rodando, e cada etapa ensina um conceito novo. Atualizado em 15/11/2026.

## 1. Onde estamos
**Já existe no código (compila e gera o APK; ainda não testado no celular):**
- Moeda desenhada por código, com giro, subida e sombra (`Coin.kt`).
- Tela do app com botão "Girar moeda" (`MainActivity.kt`).
- Widget 2x2 que gira ao toque (`CoinWidgetProvider.kt`).
- Último resultado guardado no aparelho e compartilhado entre o app e o widget.

**Ainda não existe:**
- Teste no celular ou emulador (o APK já compila).
- Repositório no GitHub.
- Ícone final, visual final do widget, vibração e som.

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

### Etapa 3 — Acabamento
- Ícone, cores, texto do widget e (opcional) vibração.
- **Pronto quando:** o visual está bom o suficiente para mostrar a outras pessoas.
- **Você aprende:** recursos do Android (`res/`) e design.

### Etapa 4 — APK de teste
- Gerar o APK pelo Android Studio e instalar em outro celular.
- **Pronto quando:** o app funciona instalado, fora do Android Studio.

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
4. **Poucas bibliotecas novas.** Hoje só usamos `core-ktx` e `appcompat`.
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
