package com.charlesawoodson.roolet.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RulesResponse(
    @Json(name = "rules") val rules: List<Rule>
)

@JsonClass(generateAdapter = true)
data class Rule(@Json(name = "rule") val ruleDescription: String)