package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.RatesRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read app name from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Cambio Brasil", appName)
  }

  @Test
  fun `rates repository loads default rates correctly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = RatesRepository(context)
    val rates = repo.getRates()
    assertNotNull(rates)
    assertEquals(7.94, rates.brlToUyu, 0.01)
  }
}
