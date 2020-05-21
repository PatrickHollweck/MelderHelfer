package de.patrickable.melderhelfer

import de.patrickable.melderhelfer.core.parsers.AlarmSMSParser

import org.junit.Test
import org.junit.Assert.*

class AlarmSMSParserTests {
    @Test
    fun parse_optional_timestamp() {
        val withTimestamp = AlarmSMSParser.parseMessage(
            """
            17:15
            EINSATZORT: Sample street Haus-Nr.: 7, TOWN b EXTRA_TOWN
            
            EINSATZGRUND: #R6010#Trauma#vitale Bedrohung - Person verletzt
            RD2
            """.trimIndent()
        )

        assertEquals("17:15", withTimestamp.time)

        val withoutTimestamp = AlarmSMSParser.parseMessage(
            """
            EINSATZORT: Sample street Haus-Nr.: 7, TOWN b EXTRA_TOWN
            
            EINSATZGRUND: #R6010#Trauma#vitale Bedrohung - Person verletzt
            RD2
            """.trimIndent()
        )

        assertEquals(null, withoutTimestamp.time)
    }

    @Test
    fun parse_address() {
        val a = AlarmSMSParser.parseMessage(
            """
            17:15
            EINSATZORT: Sample Straße Haus-Nr.: 140 , TOWN - TOWN b EXTRA_TOWN

            EINSATZGRUND: #R6010#Trauma#vitale Bedrohung - Person verletzt
            RD2
            """.trimIndent()
        )

        assertEquals("Sample Straße 140 TOWN b EXTRA_TOWN", a.address)
        assertEquals(null, a.addressObjectName)

        val b = AlarmSMSParser.parseMessage(
            """
            19:18
            EINSATZORT: STREET Haus-Nr.: 1 , TOWN - EXTRA_TOWN
            3.1.2 SHORTCODE GENERIC_A GENERIC_B TOWN - EXTRA_TOWN
            EINSATZGRUND: #R3000-Herz/Kreislauf#Herz/Kreislauf
            """.trimIndent()
        )

        assertEquals("STREET 1 TOWN - EXTRA_TOWN", b.address)
        assertEquals("3.1.2 SHORTCODE GENERIC_A GENERIC_B TOWN - EXTRA_TOWN", b.addressObjectName)
    }
}
