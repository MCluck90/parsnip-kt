import com.github.mcluck90.parsnip.Source
import com.github.mcluck90.parsnip.text
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ParserTextTests {

  @Test
  fun shouldParseASimpleString() {
    val result = text("abc").parse(Source("abcdef", 0))
    assert(result.isOk())
    val output = result.unwrap()
    assertEquals("abc", output.value)
    assertEquals("def", output.source.getRemaining())
  }
}