class Titan(
    name: String,
    mythology: String,
    val domain: String,
    val equivalent: Titan? = null
) : Entity(name, mythology)
