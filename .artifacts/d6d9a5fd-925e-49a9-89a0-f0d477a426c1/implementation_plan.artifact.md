# Implementation Plan - Pokémon Detail Screen

Add a detail screen functionality to the application so that tapping a Pokémon on the list view opens a comprehensive details panel displaying high-resolution images and specialized statistics (speed, attack, special attack, defense, special defense, and hp).

## User Review Required

> [!NOTE]
> To implement this efficiently without adding advanced navigation graph boilerplates (like Jetpack Navigation), we will enrich `PokemonUiState` and `PokemonViewModel` to track a "selected Pokémon ID/detail" state. The UI will adapt conditionally: displaying the master list view or shifting smoothly to the details screen layout based on user selection.

## Proposed Changes

### Data Layer Extensions
Enhance data models to map individual Pokémon stat values returned from `https://pokeapi.co/api/v2/pokemon/{id}/`.

#### [MODIFY] [PokemonResponse.kt](file:///C:/Users/theof/AndroidStudioProjects/Pokemax/app/src/main/java/com/example/pokemax/PokemonResponse.kt)
- Add specialized detailed data models: `PokemonDetail`, `StatSlot`, and `StatInfo` to deserialize full statistics.

#### [MODIFY] [PokeApiService.kt](file:///C:/Users/theof/AndroidStudioProjects/Pokemax/app/src/main/java/com/example/pokemax/PokeApiService.kt)
- Add a new service function: `suspend fun getPokemonDetail(@Path("id") id: Int): PokemonDetail`.

#### [MODIFY] [PokemonRepository.kt](file:///C:/Users/theof/AndroidStudioProjects/Pokemax/app/src/main/java/com/example/pokemax/PokemonRepository.kt)
- Add a network method to retrieve details by id: `suspend fun fetchPokemonDetail(id: Int): PokemonDetail`.

### UI & Architecture Upgrades
Incorporate click callbacks, selection flow tracking, loading states for detail screens, and layout design.

#### [MODIFY] [PokemonViewModel.kt](file:///C:/Users/theof/AndroidStudioProjects/Pokemax/app/src/main/java/com/example/pokemax/PokemonViewModel.kt)
- Add states tracking whether a details panel is currently requested, handles detail fetch requests, and allows clean back navigation safely.

#### [MODIFY] [PokemonScreen.kt](file:///C:/Users/theof/AndroidStudioProjects/Pokemax/app/src/main/java/com/example/pokemax/PokemonScreen.kt)
- Wrap items in click listeners.
- Design `PokemonDetailContent` showing the big sprite image on top followed by a table/column of base statistics.
- Wire back buttons to clear the selection state smoothly.

## Verification Plan

### Automated Verification
- Perform a complete project check with `gradle_sync`.
- Compile via `app:assembleDebug` to assure zero syntax anomalies.
