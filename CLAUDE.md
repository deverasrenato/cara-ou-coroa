# Cara ou Coroa — contexto para o Claude

App de cara ou coroa para Android, para qualquer usuário que precise decidir algo rápido. O diferencial é o **widget**: o usuário gira a moeda, com animação, e vê o resultado direto na tela inicial, sem abrir o app.

App Android nativo em **Kotlin** (views em XML), aberto no **Android Studio**. Android apenas. Veja o `README.md` para a visão geral.

> Este app foge do padrão Expo/React Native de `C:\dev\_padrao-apps` de propósito: widget com animação exige código nativo. O motivo está em `docs/decisoes.md` (D01). Todo o resto do padrão (documentação, idioma, Git, nível do código) continua valendo.

## Por onde começar (leia nesta ordem)
1. `docs/plano-de-execucao.md`: onde estamos, as etapas e o que fazer a seguir.
2. `docs/decisoes.md`: o que foi decidido e por quê.
3. `docs/requisitos.md`: requisitos, histórias de usuário e regras de negócio.

## Sobre este app
- **Público:** qualquer pessoa que precise de um cara ou coroa rápido no celular.
- **Prazo e formato da entrega:** sem prazo definido.
- **Escopo do MVP:** girar a moeda no app; girar a moeda pelo widget com animação; mostrar o resultado; app e widget mostram o mesmo último resultado.
- **Dados:** ficam só no aparelho (guardamos apenas o último resultado). Sem servidor.

## Sobre o usuário que está desenvolvendo
- Estudante de ADS (Análise e Desenvolvimento de Sistemas), 2º período, aprendendo programação.
- Não lê inglês com facilidade, mas quer ter inglês nos apps.
- Pode usar Kotlin e outras linguagens, e o Android Studio, **desde que o código respeite o nível dele**.

## Como trabalhar
- Código simples e didático, no nível do 2º período: funções pequenas, `if`/`for`, variáveis claras, classes simples. Evitar Jetpack Compose, coroutines, injeção de dependência, arquitetura em camadas e outros padrões avançados. O design da interface pode ser mais elaborado.
- Nomes de variáveis, funções, classes e arquivos em inglês; comentários e explicações em português. Os textos que o usuário vê no app ficam em português.
- Ao usar um termo técnico novo, explicar com a tradução na primeira vez.
- Arquivos de documentação em inglês devem ganhar uma tradução em arquivo separado (`.pt-BR.md`), sem alterar o original.
- Chamar quem usa o app de "usuário".
- **Não faça `git commit` nem `git push`.** O usuário faz os commits e os envios no próprio terminal, para aprender Git. Ao terminar uma mudança, diga quais arquivos mudaram e explique, passo a passo, os comandos para ele rodar, sugerindo uma mensagem de commit.
- **Ambiente neste computador (sem Android Studio):** JDK 17 em `%USERPROFILE%\tools\jdk-17`, Gradle em `%USERPROFILE%\tools\gradle-8.7` e Android SDK em `%LOCALAPPDATA%\Android\Sdk`. Para compilar: defina `JAVA_HOME` e `ANDROID_HOME` e rode `.\gradlew.bat assembleDebug`. O arquivo `local.properties` (com o caminho do SDK) não vai para o Git.
- Compilar não prova que funciona: o widget só se testa no aparelho ou no emulador. Não diga que funciona sem o usuário testar.
- Testar no celular (ou no emulador do Android Studio) a cada etapa. Widget só se testa no aparelho ou no emulador.
- Os dados do usuário ficam só no aparelho.

## Registro de decisões
Ao tomar uma decisão relevante (tecnologia, escopo, design, regra de negócio), registre em `docs/decisoes.md` com contexto, decisão, fundamento e alternativas descartadas. Se uma decisão mudar, marque a antiga como Substituída e crie uma nova. Atualize o plano em `docs/plano-de-execucao.md` ao terminar cada etapa.

## Fluxo entre computadores
O projeto vive no GitHub. Em um computador novo: `git clone <endereço>`, abrir a pasta no Android Studio e esperar o Gradle sincronizar. O passo a passo completo está em `docs/plano-de-execucao.md`.
