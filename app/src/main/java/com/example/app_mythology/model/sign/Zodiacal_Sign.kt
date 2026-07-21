package com.example.app_mythology.model.sign

import com.example.app_mythology.model.base.Entity
import com.example.app_mythology.model.enum.ZodiacType

class Zodiacal_Sign(
    name: String,
    mythology: String,
    val zodiacType: ZodiacType,
    val chineseEquivalent: String? = null
) : Entity(name, mythology)
