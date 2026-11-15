# Moedinha — Caminho até a Play Store

Este arquivo reúne o que falta para **fechar o app** e **publicar** na Google Play, na ordem em que vamos fazer. As regras da loja mudam com frequência: **confirme nas páginas do Google antes de pagar ou enviar** (links no fim). Atualizado em 15/11/2026.

## Visão geral das fases
| Fase | O que entrega | Versão |
|---|---|---|
| A | Fechar o MVP: testes no celular de verdade e ajustes | 0.5.x |
| B | Tela "Sobre o desenvolvedor" (apresentação e como me achar) | 0.6.0 |
| C | Preparar a publicação (nome, API 36, AAB assinado, ficha da loja) | 0.9.0 |
| D | Conta na Play, teste fechado de 14 dias e produção | **1.0.0** |
| E | "Doe um café" pela própria Play (Google Play Billing) | 1.1.0 |

## Fase A — Fechar o MVP
- [ ] Testar por toque, no emulador e no **S23** (letra pequena): layouts salvos (D19), "Salvar", "Jogar novamente?", "Zerar tudo" (app e widget), vibração, deslize, abertura e boas-vindas.
- [ ] Conferir o widget e a janelinha no visual novo (D15) e a fluidez da animação.
- [ ] Testar em mais de um celular (tamanhos de tela e de fonte diferentes).
- [ ] Alinhar `docs/requisitos.md` e `docs/plano-de-execucao.md` com as decisões D14 a D19 (ainda falam de "Melhor de 7" e textos antigos).
- [ ] Decidir se entram layouts de exemplo prontos (ex.: "Sim ou Não") e som ao girar (opcional).

## Fase B — Tela "Sobre o desenvolvedor"
Decisão registrada em D20. Plano de trabalho a fazer antes de programar:
- Um link discreto no fim da tela principal (perto de "rever boas-vindas"); nada aparece sozinho ao abrir o app.
- Conteúdo: apresentação curta (estudante de ADS), foto (opcional), links de perfil (GitHub, LinkedIn, Instagram...) que abrem no navegador (não exige permissão).
- Sem e-mail pessoal à vista. Sem pedido de dinheiro nesta fase.
- Deixar o lugar reservado para o "Doe um café" (Fase E).
- **Falta decidir:** quais perfis mostrar, se tem foto e o tom do texto.

## Fase C — Preparar a publicação
- [ ] **Nome e `applicationId` definitivos.** Hoje é `com.deverasrenato.caraoucoroa` (D14) e **não muda mais depois de publicado**. Conferir se "Moedinha" está livre na loja.
- [ ] **Subir o `targetSdk` de 34 para 36.** Desde 31/08/2026, apps novos e atualizações precisam mirar o Android 16 (API 36). Provavelmente exige atualizar o Android Gradle Plugin, o Gradle e o `compileSdk`. Depois, testar tudo de novo, principalmente o widget.
- [ ] **AAB assinado** (release): gerar a chave de assinatura, guardar em lugar seguro, **nunca** subir ao Git.
- [ ] **Política de privacidade** com um link (pode ser uma página do próprio GitHub). O app não coleta dados.
- [ ] **Ficha da loja:** ícone 512×512, imagem de destaque 1024×500, pelo menos 2 capturas de tela, descrição curta e longa.
- [ ] Revisar licenças: fontes Baloo 2 e Nunito (OFL, já em `docs/`) e a licença do app ("todos os direitos reservados").

## Fase D — Conta, teste fechado e produção
- [ ] Criar a **conta de desenvolvedor** (taxa única de US$ 25 e verificação de identidade). Conta pessoal.
- [ ] Preencher os formulários do Play Console: classificação etária, público-alvo, anúncios (o app não tem) e **Segurança dos dados**.
- [ ] **Teste fechado:** contas pessoais criadas depois de nov/2023 precisam de **12 testadores** inscritos por **14 dias seguidos** antes de pedir a produção. Teste aberto não conta. Começar a juntar os testadores cedo (amigos e colegas, cada um com conta Google).
- [ ] Enviar o questionário de acesso à produção e publicar.

## Fase E — "Doe um café" (Google Play Billing)
Decisão registrada em D20. Só depois que o app estiver na loja (pelo menos em teste interno), porque não dá para testar compra com APK instalado à mão.
- [ ] Perfil de pagamentos do Google (dados fiscais e bancários — responsabilidade do usuário, não do Claude).
- [ ] Cadastrar no Play Console um produto avulso (ex.: "Café", R$ 5).
- [ ] Adicionar a biblioteca oficial **Google Play Billing** (primeira biblioteca extra além do AndroidX), com o código explicado passo a passo.
- [ ] Ajustar a política de privacidade, o formulário "Segurança dos dados" e a seção de privacidade do README.
- [ ] Esconder o botão em aparelhos sem a Play Store.
- [ ] Antes de lançar, conferir na política de Pagamentos do Google se o texto do botão está dentro das regras.
- Lembretes: o Google retém uma parte de cada compra (confira o percentual) e o valor recebido é renda a declarar (não é aconselhamento fiscal).

## Segundo app (ideia em aberto)
Um app com o tema do leprechaun, aproveitando a moeda desenhada por código. Seria outra ficha na mesma conta, com outro nome, ícone e `applicationId`. Só depois do Moedinha publicado.

## Decisões que ainda faltam
1. Nome final e `applicationId`.
2. Perfis, foto e texto da tela "Sobre".
3. Valor e texto do "Doe um café".
4. Onde hospedar a política de privacidade.

## Links oficiais (conferir sempre)
- [Requisitos de teste para contas pessoais](https://support.google.com/googleplay/android-developer/answer/14151465?hl=en)
- [Requisito de API alvo](https://support.google.com/googleplay/android-developer/answer/11926878?hl=en-419)
