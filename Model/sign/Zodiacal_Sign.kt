package com.example.app_mythology.Model.sign

import com.example.app_mythology.Model.base.Entity
import com.example.app_mythology.Model.enum.ZodiacType

class Zodiacal_Sign(
    name: String,
    mythology: String,
    val zodiacType: ZodiacType,
    val chineseEquivalent: String? = null
) : Entity(name, mythology)
