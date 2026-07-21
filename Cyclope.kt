class Cyclope(
    name: String,
    mythology: String,
    val primordial: Boolean,
    val story: String,
    val equivalent: Cyclope? = null
) : Entity(name, mythology)
