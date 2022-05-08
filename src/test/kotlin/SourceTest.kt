import com.github.mcluck90.parsnip.Source
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.test.expect

internal class SourceMatchTest {
  @Test
  fun returnsAParseErrorIfThereIsNotAMatch() {
    val source = Source("a", 0)
    val result = source.match(Regex("b"))
    assert(result.isErr())
  }

  @Test
  fun errorShouldContainLineAndColumn() {
    val source = Source("a", 0, 2, 3)
    val result = source.match(Regex("b"))
    assert(result.isErr())

    val error = result.unwrapErr()
    assertEquals(2, error.line)
    assertEquals(3, error.column)
  }

  @Test
  fun canMatchASimpleString() {
    val source = Source("a", 0)
    val result = source.match(Regex("a"))
    assert(result.isOk())
    assertEquals("a", result.unwrap().value)
  }

  @Test
  fun returnsAnUpdatedColumn() {
    var sourceString = "a"
    var source = Source(sourceString, 0)
    var result = source.match(Regex("a"))
    assert(result.isOk())
    assertEquals(sourceString.length + 1, result.unwrap().column)

    sourceString = "abcdefghijkl"
    source = Source(sourceString, 0)
    result = source.match(Regex("abcdefghijkl"))
    assert(result.isOk())
    assertEquals(sourceString.length + 1, result.unwrap().column)
  }

  @Test
  fun returnsAnUpdatedLine() {
    val sourceString = """
      line1
      line2
      line3
    """.trimIndent()
    val source = Source(sourceString, 0)
    var result = source.match(Regex("line1"))
    assert(result.isOk())
    assertEquals(1, result.unwrap().line)

    result = source.match(Regex("line1\n"))
    assert(result.isOk())
    assertEquals(2, result.unwrap().line)

    result = source.match(Regex("line1\nline2\n"))
    assert(result.isOk())
    assertEquals(3, result.unwrap().line)
  }

  @Test
  fun resetsColumnAfterALineBreak() {
    val sourceString = """
      line1
      line2
    """.trimIndent()
    val source = Source(sourceString, 0)
    val result = source.match(Regex("line1\nline2"))
    assert(result.isOk())
    assertEquals("line2".length + 1, result.unwrap().column)
  }
}