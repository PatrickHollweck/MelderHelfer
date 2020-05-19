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

        val alarm = AlarmSMS()

        if (isTimeLike(lines[0].content)) {
            alarm.time = lines[0].content
        }

        val addressLike = lines.filter {
            isAddressLike(it.content)
        }

        if (addressLike.count() > 0) {
            val address = addressLike
                .map { it.content }
                .joinToString(" ")

            val nextLine = lines.getOrNull(addressLike.last().index + 1)
            if (nextLine !== null) {
                if (!isCodewordLike(nextLine.content)) {
                    alarm.addressObjectName = nextLine.content
                }
            }

            alarm.address = optimizeAddress(address)
        }

        val codewordLike = lines.filter {
            isCodewordLike(it.content)
        }

        if (codewordLike.count() > 0) {
            val codeword = codewordLike
                .map { it.content }
                .joinToString(" ")

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
    }
}
