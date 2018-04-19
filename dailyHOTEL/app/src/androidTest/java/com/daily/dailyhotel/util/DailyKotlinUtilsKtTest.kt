package com.daily.dailyhotel.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertEquals

class DailyKotlinUtilsKtTest {

    @Test
    fun testTakeNotEmpty() {
        assertFalse("Test".isTextEmpty())
        assertTrue("null".isTextEmpty())
        assertTrue("      ".isTextEmpty())
        assertFalse("   null   ".isTextEmpty())
        assertFalse("NULL".isTextEmpty())
        assertFalse("Null".isTextEmpty())
        assertTrue("".isTextEmpty())

        var test: String? = null
        assertTrue(test.isTextEmpty())
    }

    @Test
    fun testIsTextEmpty() {
        assertFalse(isTextEmpty("dafdsaf", "dsfdsdfds"))
        assertTrue(isTextEmpty("", "dsfdsdfds"))
        assertTrue(isTextEmpty("dafdsaf", "null", "dsfdsdfds"))
        assertTrue(isTextEmpty("dafdsaf", "", "dsfdsdfds"))
        assertTrue(isTextEmpty("dafdsaf", "   ", "dsfdsdfds"))
        assertFalse(isTextEmpty("dafdsaf", "NULL", "dsfdsdfds"))
        assertFalse(isTextEmpty("dafdsaf", "    nuLL  ", "dsfdsdfds"))
        assertTrue(isTextEmpty("dafdsaf", null, "dsfdsdfds"))
        assertTrue(isTextEmpty(null))
    }

    @Test
    fun testReturnString() {
        val name01 = "babo".returnString("suggest", null)
        assertEquals("babao", name01)

        val name02 = "babo".returnString("suggest", " me")
        assertEquals("babao me", name02)

        val name03 = null.returnString("suggest", " me")
        assertEquals("suggest", name03)

        val name04 = "".returnString("suggest", " me")
        assertEquals("suggest", name04)
    }
}