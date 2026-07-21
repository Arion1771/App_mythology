enum class GiantType {
    Jotunn,
    Geant,
    GeantDeFeu
}

class Giant(
    name: String,
    mythology: String,
    val giantType: GiantType,
    val opponent: God? = null,
    val equivalent: Giant? = null
) : Entity(name, mythology)
