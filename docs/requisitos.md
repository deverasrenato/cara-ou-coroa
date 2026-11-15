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
| **Poderia** | Vibração e som ao girar |
| **Não agora** | Histórico, estatísticas, outras moedas, iOS |

## 4. Requisitos funcionais
| ID | Requisito |
|---|---|
| RF01 | O sistema deve sortear CARA ou COROA com 50% de chance para cada lado. |
| RF02 | O sistema deve mostrar uma animação da moeda girando (subindo, girando e descendo) antes de mostrar o resultado. |
| RF03 | O app deve ter um botão "Girar moeda" e permitir girar tocando na moeda. |
| RF04 | O widget deve girar a moeda ao ser tocado, sem abrir o app. |
| RF05 | O widget deve mostrar o resultado escrito ("Deu CARA!") e a moeda parada no lado sorteado. |
| RF06 | O sistema deve guardar o último resultado e mostrá-lo no app e no widget. |
| RF07 | O sistema deve ignorar novos toques enquanto a moeda está girando. |

## 5. Requisitos não funcionais
| ID | Requisito |
|---|---|
| RNF01 | **Privacidade:** só o último resultado é guardado, no próprio aparelho. Sem internet e sem permissões. |
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
- O resultado aparece escrito abaixo da moeda.

**HU02 — Girar a moeda no widget**
Como usuário, quero girar a moeda direto da tela inicial, para não precisar abrir o app.
- Tocar no widget inicia a animação no próprio widget.
- Ao terminar, o widget mostra a moeda parada e o texto "Deu CARA!" ou "Deu COROA!".
- O app não abre.

**HU03 — Ver o último resultado**
Como usuário, quero ver o último resultado ao voltar para o app ou olhar o widget, para não esquecer o que saiu.
- O app e o widget mostram o mesmo resultado.

## 8. Casos de uso principais
1. Usuário adiciona o widget à tela inicial.
2. Usuário toca no widget e vê a moeda girar e o resultado.
3. Usuário abre o app e gira a moeda por lá.

## 9. Modelo de dados
Um único dado, guardado em `SharedPreferences` (arquivo `coin`):
- `side`: número do último lado (0 = CARA, 1 = COROA; ausente = nunca girou).

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
