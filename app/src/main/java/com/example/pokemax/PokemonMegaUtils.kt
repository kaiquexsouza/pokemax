package com.example.pokemax

data class MegaEvolutionInfo(
    val basePokemonId: Int,
    val basePokemonName: String,
    val megaPokemonId: Int,
    val megaPokemonName: String,
    val megaFormDisplayName: String,
    val megaStoneName: String
)

private val megaEvolutionsList = listOf(
    MegaEvolutionInfo(3, "venusaur", 10033, "venusaur-mega", "Mega Venusaur", "Venusaurita"),
    MegaEvolutionInfo(6, "charizard", 10034, "charizard-mega-x", "Mega Charizard X", "Charizardita X"),
    MegaEvolutionInfo(6, "charizard", 10035, "charizard-mega-y", "Mega Charizard Y", "Charizardita Y"),
    MegaEvolutionInfo(9, "blastoise", 10036, "blastoise-mega", "Mega Blastoise", "Blastoisita"),
    MegaEvolutionInfo(15, "beedrill", 10090, "beedrill-mega", "Mega Beedrill", "Beedrillita"),
    MegaEvolutionInfo(18, "pidgeot", 10089, "pidgeot-mega", "Mega Pidgeot", "Pidgeotita"),
    MegaEvolutionInfo(65, "alakazam", 10037, "alakazam-mega", "Mega Alakazam", "Alakazamita"),
    MegaEvolutionInfo(94, "gengar", 10041, "gengar-mega", "Mega Gengar", "Gengarita"),
    MegaEvolutionInfo(115, "kangaskhan", 10042, "kangaskhan-mega", "Mega Kangaskhan", "Kangaskhanita"),
    MegaEvolutionInfo(127, "pinsir", 10040, "pinsir-mega", "Mega Pinsir", "Pinsirita"),
    MegaEvolutionInfo(130, "gyarados", 10043, "gyarados-mega", "Mega Gyarados", "Gyaradosita"),
    MegaEvolutionInfo(142, "aerodactyl", 10044, "aerodactyl-mega", "Mega Aerodactyl", "Aerodactylita"),
    MegaEvolutionInfo(150, "mewtwo", 10043, "mewtwo-mega-x", "Mega Mewtwo X", "Mewtwonita X"),
    MegaEvolutionInfo(150, "mewtwo", 10044, "mewtwo-mega-y", "Mega Mewtwo Y", "Mewtwonita Y"),
    MegaEvolutionInfo(181, "ampharos", 10045, "ampharos-mega", "Mega Ampharos", "Ampharosita"),
    MegaEvolutionInfo(208, "steelix", 10072, "steelix-mega", "Mega Steelix", "Steelixita"),
    MegaEvolutionInfo(212, "scizor", 10046, "scizor-mega", "Mega Scizor", "Scizorita"),
    MegaEvolutionInfo(214, "heracross", 10047, "heracross-mega", "Mega Heracross", "Heracrossita"),
    MegaEvolutionInfo(229, "houndoom", 10048, "houndoom-mega", "Mega Houndoom", "Houndoomita"),
    MegaEvolutionInfo(248, "tyranitar", 10049, "tyranitar-mega", "Mega Tyranitar", "Tyranitarita"),
    MegaEvolutionInfo(254, "sceptile", 10065, "sceptile-mega", "Mega Sceptile", "Sceptilita"),
    MegaEvolutionInfo(257, "blaziken", 10050, "blaziken-mega", "Mega Blaziken", "Blazikenita"),
    MegaEvolutionInfo(260, "swampert", 10064, "swampert-mega", "Mega Swampert", "Swampertita"),
    MegaEvolutionInfo(282, "gardevoir", 10051, "gardevoir-mega", "Mega Gardevoir", "Gardevoirita"),
    MegaEvolutionInfo(302, "sableye", 10066, "sableye-mega", "Mega Sableye", "Sableyita"),
    MegaEvolutionInfo(303, "mawile", 10052, "mawile-mega", "Mega Mawile", "Mawilita"),
    MegaEvolutionInfo(306, "aggron", 10053, "aggron-mega", "Mega Aggron", "Aggronita"),
    MegaEvolutionInfo(308, "medicham", 10054, "medicham-mega", "Mega Medicham", "Medichamita"),
    MegaEvolutionInfo(310, "manectric", 10055, "manectric-mega", "Mega Manectric", "Manectricita"),
    MegaEvolutionInfo(319, "sharpedo", 10070, "sharpedo-mega", "Mega Sharpedo", "Sharpedonita"),
    MegaEvolutionInfo(323, "camerupt", 10071, "camerupt-mega", "Mega Camerupt", "Cameruptita"),
    MegaEvolutionInfo(334, "altaria", 10067, "altaria-mega", "Mega Altaria", "Altarianita"),
    MegaEvolutionInfo(354, "banette", 10056, "banette-mega", "Mega Banette", "Banettita"),
    MegaEvolutionInfo(359, "absol", 10057, "absol-mega", "Mega Absol", "Absolita"),
    MegaEvolutionInfo(362, "glalie", 10074, "glalie-mega", "Mega Glalie", "Glalita"),
    MegaEvolutionInfo(373, "salamence", 10088, "salamence-mega", "Mega Salamence", "Salamencita"),
    MegaEvolutionInfo(376, "metagross", 10076, "metagross-mega", "Mega Metagross", "Metagrossita"),
    MegaEvolutionInfo(380, "latias", 10062, "latias-mega", "Mega Latias", "Latiasita"),
    MegaEvolutionInfo(381, "latios", 10063, "latios-mega", "Mega Latios", "Latiosita"),
    MegaEvolutionInfo(384, "rayquaza", 10079, "rayquaza-mega", "Mega Rayquaza", "Ascensão do Dragão"),
    MegaEvolutionInfo(428, "lopunny", 10087, "lopunny-mega", "Mega Lopunny", "Lopunnita"),
    MegaEvolutionInfo(445, "garchomp", 10058, "garchomp-mega", "Mega Garchomp", "Garchompita"),
    MegaEvolutionInfo(448, "lucario", 10059, "lucario-mega", "Mega Lucario", "Lucarionita"),
    MegaEvolutionInfo(460, "abomasnow", 10060, "abomasnow-mega", "Mega Abomasnow", "Abomasnowita"),
    MegaEvolutionInfo(475, "gallade", 10068, "gallade-mega", "Mega Gallade", "Galladita"),
    MegaEvolutionInfo(531, "audino", 10069, "audino-mega", "Mega Audino", "Audinita"),
    MegaEvolutionInfo(719, "diancie", 10075, "diancie-mega", "Mega Diancie", "Diancita")
)

fun getMegaEvolutionsForPokemon(pokemonId: Int): List<MegaEvolutionInfo> {
    return megaEvolutionsList.filter { it.basePokemonId == pokemonId }
}
