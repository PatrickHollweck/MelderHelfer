package de.patrickable.melderhelfer.core.models

/**
 * A object that represents a sms that was received for a specific alarm.
 *
 * These SMS always follow this rough structure:
 * """
 * 07:25
 * EINSATZORT: Sample Street House: 4, Washington
 * AS 112
 * EINSATZGRUND: <Emergency-Codeword>
 * RD 2
 * """
 */
data class AlarmSMS(
    val raw: String,
    var time: String? = null,
    var address: String? = null,
    var addressObjectName: String? = null,
    var codeword: String? = null,
    var dispatchType: String? = null
) {
    override fun toString(): String {
        return arrayOf(
            "Time: $time",
            "Address: $address",
            "Object-Name: $addressObjectName",
            "Codeword: $codeword",
            "Type: $dispatchType"
        ).joinToString("\n")
    }

    fun shortCodeword(): String {
        if (codeword == null) {
            return ""
        }

        val secondHashtagIndex = codeword!!
            .substring(1)
            .indexOfFirst { it == '#' }

        return codeword!!
            .substring(secondHashtagIndex + 1)
            .replace("/", " ")
            .replace("#", "")
    }
}
