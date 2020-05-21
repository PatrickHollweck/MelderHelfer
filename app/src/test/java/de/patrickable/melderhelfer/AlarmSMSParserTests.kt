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
            EINSATZGRUND: #R3000/Herz/Kreislauf#Herz/Kreislauf
            """.trimIndent()
        )

        assertEquals("STREET 1 TOWN - EXTRA_TOWN", b.address)
        assertEquals("3.1.2 SHORTCODE GENERIC_A GENERIC_B TOWN - EXTRA_TOWN", b.addressObjectName)
    }

    @Test
    fun parse_codeword() {
        val default = AlarmSMSParser.parseMessage(
            """
            19:18
            EINSATZORT: STREET Haus-Nr.: 1 , TOWN - EXTRA_TOWN
            3.1.2 SHORTCODE GENERIC_A GENERIC_B TOWN - EXTRA_TOWN
            EINSATZGRUND: #R3000#Herz/Kreislauf#Herz/Kreislauf
            """.trimIndent()
        )

        assertEquals("#R3000#Herz/Kreislauf#Herz/Kreislauf", default.codeword)

        val nonDefaultOrder = AlarmSMSParser.parseMessage(
            """
            19:18
            EINSATZGRUND: #R3000#Trauma#Trauma
            EINSATZORT: STREET Haus-Nr.: 1 , TOWN - EXTRA_TOWN
            3.1.2 SHORTCODE GENERIC_A GENERIC_B TOWN - EXTRA_TOWN
            """.trimIndent()
        )

        assertEquals("#R3000#Trauma#Trauma", nonDefaultOrder.codeword)

        val missingKeyword = AlarmSMSParser.parseMessage(
            """
            19:18
            #R2010#Atmung#vitale Bedrohung
            EINSATZORT: STREET Haus-Nr.: 1 , TOWN - EXTRA_TOWN
            3.1.2 SHORTCODE GENERIC_A GENERIC_B TOWN - EXTRA_TOWN
            """.trimIndent()
        )

        assertEquals("#R2010#Atmung#vitale Bedrohung", missingKeyword.codeword)
    }

    @Test
    fun parse_dispatch_type() {
        val default = AlarmSMSParser.parseMessage(
            """
            19:18
            EINSATZORT: STREET Haus-Nr.: 1 , TOWN - EXTRA_TOWN
            3.1.2 SHORTCODE GENERIC_A GENERIC_B TOWN - EXTRA_TOWN
            EINSATZGRUND:#R2010#Atmung#vitale Bedrohung
            RD2
            """.trimIndent()
        )

        assertEquals("RD2", default.dispatchType)

        val wrongOrder = AlarmSMSParser.parseMessage(
            """
            19:18
            EINSATZORT: STREET Haus-Nr.: 1 , TOWN - EXTRA_TOWN
            3.1.2 SHORTCODE GENERIC_A GENERIC_B TOWN - EXTRA_TOWN
            RD2
            EINSATZGRUND:#R2010#Atmung#vitale Bedrohung
            """.trimIndent()
        )

        assertEquals(null, wrongOrder.dispatchType)

        val missing = AlarmSMSParser.parseMessage(
            """
            19:18
            #R2010#Atmung#vitale Bedrohung
            EINSATZORT: STREET Haus-Nr.: 1 , TOWN - EXTRA_TOWN
            3.1.2 SHORTCODE GENERIC_A GENERIC_B TOWN - EXTRA_TOWN
            """.trimIndent()
        )

        assertEquals(null, missing.dispatchType)
    }

    @Test
    fun parse_special_formats() {
        val hightwayAlert = AlarmSMSParser.parseMessage(
            """
            #T3112#VU#mehrere LKW mit eingeklemmten Personen THL 5 THL 6
            EINSATZORT: A7 München (CS) > Nürnberg-- Haus-Nr.: 879.54 kmBAB, - Km814
            mind 3 LKW
            """.trimIndent()
        )

        assertEquals(null, hightwayAlert.time)
        assertEquals(null, hightwayAlert.dispatchType)
        assertEquals("A7 München (CS) > Nürnberg-- 879.54 kmBAB - Km814", hightwayAlert.address)
        assertEquals("mind 3 LKW", hightwayAlert.addressObjectName)
        assertEquals("#T3112#VU#mehrere LKW mit eingeklemmten Personen THL 5 THL 6", hightwayAlert.codeword)

        val minimalAlert = AlarmSMSParser.parseMessage(
            """
            EINSATZORT: Haus-Nr.:,
            
            EINSATZGRUND:
            """.trimIndent()
        )

        assertEquals(null, minimalAlert.time)
        assertEquals(null, minimalAlert.address)
        assertEquals(null, minimalAlert.codeword)
        assertEquals(null, minimalAlert.dispatchType)
        assertEquals(null, minimalAlert.addressObjectName)
    }
}
