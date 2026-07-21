enum class MuseType {
    Grecque,
    Beotienne
}

class Muses(
    name: String,
    mythology: String,
    val domain: String,
    val museType: MuseType
) : Entity(name, mythology)
