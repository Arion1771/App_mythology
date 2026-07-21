class Place(
    name: String,
    mythology: String,
    val description: String,
    val inhabitants: String? = null
) : Entity(name, mythology)
