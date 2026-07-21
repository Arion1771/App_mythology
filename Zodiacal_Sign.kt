enum class ZodiacType {
    Classique,
    Chinois
}

class Zodiacal_Sign(
    name: String,
    mythology: String,
    val zodiacType: ZodiacType,
    val chineseEquivalent: String? = null
) : Entity(name, mythology)
