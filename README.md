# Pokémax - Pokédex Android em Jetpack Compose

O **Pokémax** é um aplicativo Android moderno de Pokédex desenvolvido como projeto para a matéria de desenvolvimento mobile. O aplicativo oferece uma experiência interativa, fluida e nostálgica, combinando a estética clássica da carcaça física da Pokédex com o design moderno declarative do **Jetpack Compose** e a consumo em tempo real da **PokéAPI**.

---

## Tela do Projeto & Aparelho Base

> **Nota sobre o dispositivo base:**  
> O layout e a experiência visual do Pokémax foram desenvolvidos tomando como base de referência o **Google Pixel 10 Pro**.  
> 
> - **Suporte Mínimo (minSdk 24):** Compatível com Android 7.0 (Nougat) até as versões mais recentes (Android 16 / SDK 37), cobrindo mais de 99% dos dispositivos Android ativos.
> - **Layout Responsivo e Adaptável:** Desenvolvido 100% em **Jetpack Compose** utilizando contêineres dinâmicos (`LazyColumn`, `LazyVerticalGrid` com células adaptáveis, `BoxWithConstraints` e dimensionamento em `dp`/`sp`), garantindo que a interface se ajuste a telas menores, telas com alta densidade de pixels, dispositivos dobráveis ou tablets.
> - **Suporte a Teclado e Insets:** Configurado com `windowSoftInputMode="adjustResize"` e suporte *Edge-to-Edge* (`enableEdgeToEdge()`), evitando que campos de login ou busca sejam cobertos pelo teclado virtual em qualquer tamanho de tela.
> - **Decodificação de Imagens/GIFs Retrocompatível:** O gerenciador de mídia (Coil) detecta a versão do sistema operacional e utiliza `ImageDecoderDecoder` em dispositivos com API 28+ e `GifDecoder` em versões legadas do Android, garantindo que os GIFs animados dos Pokémon rodem sem engasgos em qualquer hardware.

---

## Como o Projeto Funciona

O Pokémax utiliza a arquitetura recomendada pelo Android (**MVVM - Model-View-ViewModel**) integrada ao padrão de **Repository** e suporte a concorrência com **Kotlin Coroutines**.

```
com.example.pokemax
├── data / api
│   ├── PokeApiService.kt         # Interface Retrofit para endpoints da PokéAPI
│   ├── TranslationService.kt     # Engine de tradução automática pt-BR
│   └── PokemonRepository.kt     # Gerenciamento de rede, lote e cache em memória
├── ui / presentation
│   ├── MainActivity.kt           # Ponto de entrada com suporte Edge-to-Edge
│   ├── LoginScreen.kt            # Tela de autenticação com logo 3D e modo convidado
│   ├── PokemonScreen.kt          # Tela principal (Navegação Lista vs. Detalhes)
│   ├── PokedexFrame.kt           # Carcaça retrô da Pokédex desenhada via Canvas
│   ├── PokemonComponents.kt     # Cards, badges, barras de status, sprites e modais
│   ├── PokemaxLogo.kt            # Logo estilizada em 3D estilo oficial
│   └── theme/                    # Cores, tipografia e temas Material 3
├── model / utils
│   ├── PokemonResponse.kt        # DTOs de listagem e detalhes do Pokémon
│   ├── PokemonDetailModels.kt    # Modelos da cadeia evolutiva e ataques
│   ├── PokemonMegaUtils.kt        # Mapeamento de Mega Evoluções e Mega Stones
│   ├── PokemonMoveUtils.kt        # Mapeamento e tradução de nomes de ataques
│   ├── PokemonTypeUtils.kt        # Cores e tradução dos tipos de Pokémon
│   └── PokemonViewModel.kt       # ViewModel e gerenciamento de estado da UI
```

---

## Principais Funcionalidades

1. **Autenticação & Acesso Convidado:**
   - Tela de login com campos estilizados, alternância de visibilidade da senha e suporte a entrada imediata como "Convidado".

2. **Moldura Retrô de Pokédex (`PokedexFrameContainer`):**
   - Moldura vermelha com frisos prateados, cantos arredondados e detalhes desenhados via Compose `Canvas` (luzes indicadoras, arcos ciano e acentos da Pokébola).

3. **Filtro por Geração & Busca em Tempo Real:**
   - Seleção de Gerações I a IX (Kanto, Johto, Hoenn, Sinnoh, Unova, Kalos, Alola, Galar e Paldea).
   - Busca instantânea debounced (300ms) por nome ou número do Pokémon (`#001`, `Pikachu`, etc.).

4. **Carregamento Rápido & Cache Inteligente:**
   - Exibição local instantânea com nomes pré-carregados.
   - Enriquecimento de detalhes em segundo plano processado em lotes de 15 itens sem travar a interface do usuário.
   - Cache em memória (`ConcurrentHashMap`) para detalhes, espécies, cadeias evolutivas e ataques.

5. **Tela de Detalhes Completa:**
   - **GIFs Animados Showdown:** Animações oficiais de batalha dos Pokémon (com fallback automático para artwork oficial em caso de falha de conexão).
   - **✦ Modo Shiny:** Botão interativo para alternar instantaneamente entre a forma Normal e a forma Shiny (Brilhante) com GIFs animados correspondentes.
   - **Métricas:** Peso formatado em kg, Altura formatada em metros e Badges coloridos por Tipo.
   - **Navegação por Abas:**
     - **STATS:** Barras animadas de status base (`HP`, `ATQ`, `DEF`, `SP.ATQ`, `SP.DEF`, `VELOC`) com destaque verde para atributos acima de 100, além do card de **Descrição da Pokédex** com paginação e tradução automática para Português.
     - **MOVES:** Habilidades principais e ocultas, lista separada de **Ataques de Dano** e **Ataques de Suporte**.
     - **EVOL:** Visualização intuitiva da **Cadeia Evolutiva** (Forma Base + Requisito/Nível/Item = Resultado). Inclui suporte completo a **Mega Evoluções** (ex: *Mega Charizard X/Y*, *Mega Mewtwo X/Y*, *Mega Rayquaza*, etc.) exibindo as respectivas Mega Pedras!

6. **Modal de Detalhes do Ataque:**
   - Clique em qualquer ataque para abrir o modal com Categoria (Físico, Especial, Suporte), Poder, Precisão, PP e descrição/efeitos secundários traduzidos para o Português.

7. **Engine de Tradução Automática (`TranslationService`):**
   - Tradução em tempo real de textos descritivos e efeitos dos ataques de Inglês para Português do Brasil (`pt-BR`) via integração com higienização de texto e cache local.

---

## ️ Tecnologias Utilizadas

- **Linguagem:** [Kotlin](https://kotlinlang.org/) `2.2.10`
- **Interface:** [Jetpack Compose](https://developer.android.com/jetpack/compose) + [Material 3](https://m3.material.io/)
- **BOM Compose:** `2026.02.01`
- **Arquitetura:** MVVM (Model-View-ViewModel) + Repository Pattern
- **Gerenciamento de Estado:** StateFlow / Compose State
- **Comunicação de Rede:** [Retrofit 2.11.0](https://square.github.io/retrofit/) + Converter Gson + OkHttpClient
- **Carregamento de Imagens e GIFs:** [Coil Compose 2.6.0](https://coil-kt.github.io/coil/) com extensão `coil-gif`
- **Concorrência:** Kotlin Coroutines (`kotlinx-coroutines-android`)

---

## Como Executar o Projeto

### Pré-requisitos
- **Android Studio:** Ladybug (2024.2.1) ou superior recomendado.
- **JDK:** Java 11 ou superior configurado no ambiente/IDE.
- **Dispositivo de Teste:** Emulador Android ou aparelho físico rodando Android 7.0 (API 24) ou superior.
- **Conexão com a Internet:** Necessária para buscar os dados da PokéAPI, GIFs e efetuar traduções.

### Passos para Execução

1. **Clonar o Repositório:**
   ```bash
   git clone https://github.com/seu-usuario/pokemax.git
   cd pokemax
   ```

2. **Abrir no Android Studio:**
   - Abra o Android Studio.
   - Clique em **Open** e selecione a pasta do projeto `pokemax`.
   - Aguarde o Gradle sincronizar as dependências automaticamente (`Gradle Sync`).

3. **Executar via Linha de Comando (Opcional):**
   - Para compilar o APK em modo de depuração:
     ```bash
     ./gradlew assembleDebug
     ```

4. **Executar no Emulador ou Aparelho Físico:**
   - Selecione o dispositivo de destino no menu superior do Android Studio (ex: Emulador Pixel ou seu aparelho via depuração USB).
   - Pressione `Shift + F10` ou clique no botão **Run** (`▶`).

---

## Licença

Este projeto é de caráter acadêmico e educacional. Todos os dados e marcas registradas de Pokémon são de propriedade da *Nintendo*, *Creatures Inc.* e *GAME FREAK inc.* Dados disponibilizados publicamente via [PokéAPI](https://pokeapi.co/).
