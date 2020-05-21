package de.patrickable.melderhelfer.core.parsers

import de.patrickable.melderhelfer.core.models.AlarmSMS

data class IndexedLine(val index: Int, val content: String)

class AlarmSMSParser {
    companion object {
        private const val ADDRESS_PREFIX = "EINSATZORT:"
        private const val CODEWORD_PREFIX = "EINSATZGRUND:"

        fun parseMessage(message: String): AlarmSMS {
            return AlarmSMSParser().parse(message)
        }
    }

    private fun parse(message: String): AlarmSMS {
        val lines = message.lines()
            .map {
                it.trim()
            }
            .mapIndexed { index, element ->
                IndexedLine(index, element)
            }

        val alarm = AlarmSMS(message)

        // Optional time stamp at the top of the alarm
        if (isTimeLike(lines[0].content)) {
            alarm.time = lines[0].content
        }

        val addressLike = lines.filter {
            isAddressLike(it.content)
        }

        if (addressLike.count() > 0) {
            val address = addressLike.joinToString(" ") { it.content }
            val nextLine = lines.getOrNull(addressLike.last().index + 1)

            if (
                nextLine !== null
                && nextLine.content.isNotEmpty()
                && !isCodewordLike(nextLine.content)
            ) {
                alarm.addressObjectName = nextLine.content
            }

            alarm.address = optimizeAddress(address)
        }

        val codewordLike = lines.filter {
            isCodewordLike(it.content)
        }

        if (codewordLike.count() > 0) {
            val codeword = codewordLike.joinToString(" ") { it.content }

            alarm.codeword = optimizeCodeword(codeword)

            val nextLine = lines.getOrNull(codewordLike.last().index + 1)
            if (nextLine !== null) {
                alarm.dispatchType = nextLine.content
            }
        }

        return alarm
    }

    /**
     * Checkers
     */

    private fun isTimeLike(input: String): Boolean {
        return Regex("""\d\d:\d\d""").matches(input)
    }

    private fun isAddressLike(input: String): Boolean {
        return input.startsWith(ADDRESS_PREFIX)
    }

    private fun isCodewordLike(input: String): Boolean {
        return input.startsWith(CODEWORD_PREFIX)
    }

    /**
     * Optimizers
     */

    private fun optimizeCodeword(codeword: String): String {
        return codeword
            .removePrefix(CODEWORD_PREFIX)
            .trim()
    }

    private fun optimizeAddress(address: String): String {
        return address
            .removePrefix(ADDRESS_PREFIX)
            .trim()
            .replace("Haus-Nr.:", "")
            .replace(",", "")
            .replace(Regex("""\s+"""), " ")
            .let {
                replaceDuplicateWords(it)
            }
    }

    private fun replaceDuplicateWords(input: String): String {
        val matches = Regex("""([\p{L}\p{N}_]+)(\s+|\s+[-|_]\s+)\1""").findAll(input)

        var result = input
        for (match in matches) {
            val groupValues = match.groupValues
            result = input.replace(groupValues[0], groupValues[1])
        }

        return result
    }
}
