package com.example

import com.example.util.ExpressionEvaluator
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testExpressionEvaluation() {
    assertEquals(150.0, ExpressionEvaluator.evaluate("150"), 0.001)
    assertEquals(43.0, ExpressionEvaluator.evaluate("25 + 18"), 0.001)
    assertEquals(42.0, ExpressionEvaluator.evaluate("12 * 3.5"), 0.001)
    assertEquals(85.0, ExpressionEvaluator.evaluate("100 - 15"), 0.001)
    assertEquals(30.0, ExpressionEvaluator.evaluate("120 / 4"), 0.001)
    // Trailing operators should evaluate cleanly without crash
    assertEquals(25.0, ExpressionEvaluator.evaluate("25 + "), 0.001)
  }

  @Test
  fun testAmountFormatting() {
    val formatted = ExpressionEvaluator.formatAmount(1250.50)
    assertEquals("1.250,50", formatted)
  }
}
