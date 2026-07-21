class Heroes(
    name: String,
    mythology: String,
    val story: String,
    val killer: String? = null,
    val ascendant: God? = null
) : Entity(name, mythology)
