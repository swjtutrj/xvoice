package com.example.ddvoice

import org.json.JSONObject

/**
 * AIUI语义结果
 */

class SemanticResult {
    var rc: Int = 0
    var text: String? = null
    var service: String? = null
    var answer: String? = null
    var data: JSONObject? = null
    var semantic: JSONObject? = null
}
