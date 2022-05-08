package com.github.mcluck90.parsnip
private data class LineAndColumn(val line: Int, val column: Int)

data class ParseOutput<T>(val value: T, val source: Source, val line: Int, val column: Int)
typealias ParseResult<T> = Result<ParseOutput<T>, ParseError>

class Source(val source: String, val index: Int, val line: Int = 1, val column: Int = 1) {
  private fun getNewLineAndColumn(value: String): LineAndColumn {
    var line = this.line
    var column = this.column + value.length
    val linesRegex = Regex("\\n.*")
    val lines = linesRegex.findAll(this.source.substring(this.index, this.index + value.length)).toList()
    if (lines.isNotEmpty()) {
      line += lines.size
      column = lines.last().value.length
    }

    return LineAndColumn(line, column)
  }

  fun getRemaining(): String {
    return this.source.substring(this.index)
  }

  fun match(regexp: Regex, message: String? = null): ParseResult<String> {
    val match = regexp.find(this.source, this.index)
    if (match != null) {
      val value = if (match.groupValues.isNotEmpty()) {
        match.groupValues[0]
      } else {
        match.value
      }

      val newIndex = this.index + value.length
      val (line, column) = this.getNewLineAndColumn(value)
      val source = Source(this.source, newIndex, line, column)
      return Result.Ok(ParseOutput(value, source, line, column))
    }

    return Result.Err(ParseError(
      this.line,
      this.column,
      message,
      this,
      null,
      null
    ))
  }

  fun text(text: String, message: String? = null): ParseResult<String> {
    val newIndex = this.index + text.length
    val substring =this.source.substring(this.index, newIndex)
    if (substring == text) {
      val (line, column) = this.getNewLineAndColumn(text)
      val source = Source(this.source, newIndex, line, column)
      return Result.Ok(ParseOutput(text, source, line, column))
    }

    return Result.Err(
      ParseError(
      this.line,
      this.column,
      message,
      this,
      text,
      substring
    ))
  }
}
