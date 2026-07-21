class God(
    name: String,
    mythology: String,
    val domain: String,
    val godType: GodType? = null,
    val equivalent: God? = null,
    val father: God? = null,
    val mother: God? = null
) : Entity(name, mythology)
