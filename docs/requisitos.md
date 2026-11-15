# Cara ou Coroa — Documento de Requisitos

Versão 0.1 · Projeto acadêmico de ADS

## 1. Visão do produto
**Cara ou Coroa** é um app Android que joga uma moeda virtual. O diferencial é um widget na tela inicial: o usuário gira a moeda, com animação, e vê o resultado sem abrir o app.

**Problema:** decidir algo rápido (quem começa, quem paga, qual opção) exige abrir um app, esperar carregar e tocar em botões.
**Solução:** um widget sempre à mão na tela inicial, com um toque para girar e ver o resultado.

## 2. Público-alvo (personas)
| Persona | Descrição | Necessidade |
|---|---|---|
| Usuário casual | Qualquer pessoa com Android que decide coisas simples com cara ou coroa | Resposta rápida, sem abrir app |

## 3. Escopo do MVP (MoSCoW)
| Prioridade | Funcionalidade |
|---|---|
| **Deve** | Girar a moeda no app e ver CARA ou COROA |
| **Deve** | Widget na tela inicial que gira a moeda ao toque, com animação |
| **Deve** | O resultado aparece no próprio widget |
| **Deveria** | App e widget mostram o mesmo último resultado |
| **Deveria** | Textos personalizados para cara e coroa (ex.: "Pedir delivery" e "Cozinhar em casa"), mostrados no resultado |
| **Deveria** | Pergunta opcional, digitada em uma janelinha aberta pelo lápis do widget, mostrada no resultado |
| **Deveria** | Girar só 1 vez (padrão), modo livre (conta cara e coroa sem fim) e séries "melhor de 5" e "melhor de 7", com placar |
| **Deveria** | Vencedor grande sobre a moeda, com "Jogar novamente?" |
| **Deveria** | Botão "Zerar tudo" |
| **Poderia** | Vibração e som ao girar |
| **Não agora** | Histórico, estatísticas, outras moedas, iOS |

## 4. Requisitos funcionais
| ID | Requisito |
|---|---|
| RF01 | O sistema deve sortear CARA ou COROA com 50% de chance para cada lado. |
| RF02 | O sistema deve mostrar uma animação da moeda girando (subindo, girando e descendo) antes de mostrar o resultado. |
| RF03 | O app deve ter um botão "Girar moeda" e permitir girar tocando na moeda. |
| RF04 | O widget deve girar a moeda ao ser tocado, sem abrir o app. |
| RF05 | O widget deve mostrar a moeda parada no lado sorteado, com CARA ou COROA escrito nela. O lado não é repetido em texto. |
| RF06 | O sistema deve guardar o último resultado e mostrá-lo no app e no widget. |
| RF07 | O sistema deve ignorar novos toques enquanto a moeda está girando. |
| RF08 | O usuário pode digitar um texto para cara e outro para coroa (até **12** caracteres cada). Esses textos aparecem no placar no lugar de "CARA" e "COROA". A moeda continua escrevendo CARA e COROA. |
| RF09 | O usuário pode digitar uma pergunta opcional (até **30** caracteres). Ela aparece logo abaixo da moeda. |
| RF10 | O widget deve ter um botão de lápis que abre uma janelinha por cima da tela inicial, com os campos de pergunta, cara e coroa e o botão "Girar". Tocar na moeda continua girando na hora, sem digitar. |
| RF11 | Os textos de cara e coroa e a pergunta continuam guardados entre os giros, até o usuário mudá-los ou tocar em "Zerar tudo". São vazios por padrão. |
| RF12 | O usuário escolhe o modo marcando uma opção fixa: **Girar só 1 vez** (padrão), **Livre (sem fim)**, **Melhor de 5** ou **Melhor de 7**, nesta ordem. Não há campo para digitar número. |
| RF13 | **Girar só 1 vez:** sem placar nem sequência; cada giro vale sozinho. Aparece a pergunta e, se o usuário digitou textos, o texto do lado que saiu. **Modo livre:** cada toque na moeda é um giro, e o placar conta cara e coroa sem fim. Não há vencedor. A linha da sequência mostra os últimos 5 giros. Quando o app sai da tela, o placar do modo livre é zerado (girar a tela do celular não zera). |
| RF14 | **Melhor de 5 ou 7:** cada toque é um giro. A série termina quando um lado chega a N/2 + 1 vitórias (5 = 3; 7 = 4), e os giros que sobrariam não são jogados. A linha da sequência mostra todos os giros da série. |
| RF15 | Quando a série termina, aparece **sobre a moeda** o vencedor em letras grandes, "venceu!" e o botão **"Jogar novamente?"**. Enquanto isso, tocar na moeda não gira. |
| RF16 | **"Jogar novamente?"** zera o placar e a sequência e começa outra série, mantendo modo, pergunta e textos. |
| RF17 | Mudar o modo zera o placar e a sequência. |
| RF18 | **"Zerar tudo"** (no app e na janelinha do widget, com confirmação) apaga placar, sequência, pergunta, textos de cara e coroa e o último resultado, e volta ao modo "girar só 1 vez". |
| RF19 | Logo abaixo da moeda ficam linhas de texto **fixas e centralizadas**, sempre nos mesmos lugares: (1) a pergunta; (2) o placar em 3 colunas, "● Sim", "2 x 1" e "Não ○", com o "2 x 1" sempre no centro, qualquer que seja o tamanho dos textos; (3) a sequência do que saiu em cada giro, com ● para o lado da cara e ○ para o lado da coroa. Sem textos digitados, os nomes são CARA e COROA. |
| RF20 | O texto abaixo da moeda não muda durante a animação (sem "Girando..."): só é atualizado depois que a moeda para. A moeda não muda de tamanho quando o texto muda. Textos comprometidos com o limite de caracteres diminuem a letra automaticamente para caber. |
| RF21 | Quando a última jogada de uma série termina, o widget e o app esperam cerca de 1 segundo, com a moeda visível, antes de mostrar o vencedor. |

## 5. Requisitos não funcionais
| ID | Requisito |
|---|---|
| RNF01 | **Privacidade:** só o estado do jogo (último resultado, placar, textos e pergunta) é guardado, no próprio aparelho. Sem internet e sem permissões. |
| RNF02 | **Desempenho:** a animação dura cerca de 1,3 segundo e o resultado aparece logo em seguida. |
| RNF03 | **Compatibilidade:** Android 8 (API 26) ou superior. Não há versão para iOS. |

## 6. Regras de negócio
- **RN01:** o sorteio é aleatório e não depende do resultado anterior.
- **RN02:** o lado que fica para cima no fim da animação é sempre o lado sorteado.
- **RN03:** enquanto a moeda gira, um novo toque não inicia outro giro.

## 7. Histórias de usuário (com critérios de aceite)
**HU01 — Girar a moeda no app**
Como usuário, quero tocar na moeda ou no botão, para decidir algo rápido.
- A moeda gira com animação e para mostrando CARA ou COROA.
- A moeda para mostrando o lado; abaixo dela aparece a pergunta e o texto do lado, se o usuário digitou.

**HU02 — Girar a moeda no widget**
Como usuário, quero girar a moeda direto da tela inicial, para não precisar abrir o app.
- Tocar no widget inicia a animação no próprio widget.
- Ao terminar, o widget mostra a moeda parada, com CARA ou COROA escrito nela.
- O app não abre.

**HU03 — Ver o último resultado**
Como usuário, quero ver o último resultado ao voltar para o app ou olhar o widget, para não esquecer o que saiu.
- O app e o widget mostram o mesmo resultado.

**HU04 — Decidir algo específico pelo widget**
Como usuário, quero escrever o que significa cara e coroa (e, se quiser, a pergunta) sem abrir o app, para tomar uma decisão rápida como "pedir delivery: sim ou não".
- Tocar no lápis do widget abre uma janelinha com os campos; o botão "Girar" fecha a janelinha e a moeda gira no widget.
- O widget mostra a moeda no lado sorteado e, embaixo, a pergunta e o texto digitado para esse lado.
- Quem não quiser digitar nada toca na moeda e ela gira normalmente.

**HU05 — Decidir na melhor de cinco**
Como usuário, quero jogar a moeda em uma série (por exemplo, melhor de 5) e ver o placar, para decidir com mais "justiça" do que com um giro só.
- Na janelinha do widget (ou no app), marco "Melhor de 5".
- Cada toque na moeda gira uma vez e atualiza, embaixo da moeda, o placar e a sequência do que saiu em cada giro.
- Quando um lado chega a 3 vitórias, o vencedor aparece grande sobre a moeda, com "Jogar novamente?". Os giros que sobrariam não são jogados.

**HU06 — Jogar sem fim e zerar**
Como usuário, quero ficar jogando a moeda e ver quantas vezes deu cara e coroa, e poder zerar tudo quando quiser.
- No modo livre, o placar continua contando sem parar.
- "Zerar tudo" (com confirmação) apaga placar, pergunta e textos, e o jogo volta a ser só cara ou coroa.

## 8. Casos de uso principais
1. Usuário adiciona o widget à tela inicial.
2. Usuário toca no widget e vê a moeda girar e o resultado.
3. Usuário abre o app e gira a moeda por lá.

## 9. Modelo de dados
Guardados em `SharedPreferences` (arquivo `coin`):
- `side`: número do último lado (0 = CARA, 1 = COROA; ausente = nunca girou).
- `label_0` e `label_1`: texto digitado para cara e para coroa (vazio = nada digitado).
- `question`: pergunta digitada (fica guardada até o usuário mudar ou zerar tudo).
- `series_size`: modo de jogo: 1 = girar só 1 vez (padrão), 0 = livre, 5 ou 7.
- `score_0` e `score_1`: quantas vezes cara e coroa saíram.
- `history`: o que saiu em cada giro, em ordem (por exemplo, `010` = cara, coroa, cara). Guarda só os 7 últimos.
- "Zerar tudo" apaga todas essas chaves.

## 10. Plano de sprints
| Sprint | Entrega |
|---|---|
| 1 | Projeto compilando e app girando a moeda |
| 2 | Widget girando com animação |
| 3 | Acabamento visual e APK de teste |

## 11. Métricas de sucesso do MVP
- O widget gira e mostra o resultado sem abrir o app.
- A animação parece fluida em pelo menos dois celulares diferentes.
- O código é compreensível para o autor (consegue explicar cada arquivo).

## 12. Riscos
- **Animação do widget travada:** alguns launchers limitam a velocidade de atualização do widget. Mitigação: ajustar `FRAME_COUNT` e `FRAME_DELAY_MS`.
- **Erros de compilação na primeira vez:** o código foi escrito sem poder compilar no computador do desenvolvimento. Mitigação: corrigir no Android Studio na Etapa 0.

## 13. Glossário
- **MVP:** Produto Mínimo Viável, a menor versão que já gera valor.
- **MoSCoW:** técnica de priorização (Deve, Deveria, Poderia, Não agora).
- **Widget:** pequeno bloco interativo que fica na tela inicial do celular.
- **Launcher:** o programa que mostra a tela inicial do celular.
- **Bitmap:** imagem formada por pontos (pixels); aqui, cada quadro da animação.
- **Frame (quadro):** cada imagem de uma animação.
