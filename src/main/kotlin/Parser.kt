package com.github.mcluck90.parsnip

typealias ParseFunction<T> = (source: Source) -> ParseResult<T>
class Parser<T>(val parse: ParseFunction<T>) {
  fun <U> and(parser: Parser<U>): Parser<U> {
    return this.bind { parser }
  }

  fun <U> bind(map: (T) -> Parser<U>): Parser<U> {
    return Parser { source ->
      val result = this.parse(source)
      result.map { map(it.value).parse(it.source) } as ParseResult<U>
    }
  }

  fun <U> map(map: (T) -> U): Parser<U> =
    this.bind { constant(map(it)) }

  fun <U> skip(parser: Parser<U>): Parser<T> =
    this.bind { value -> parser.map { value } }

  fun parseToEnd(input: String): Result<T, ParseError> {
    val source = Source(input, 0)
    val result = this.parse(source)
    if (result.isErr()) {
      return Result.Err(result.unwrapErr())
    }

    val output = result.unwrap()
    if (output.source.index != output.source.source.length) {
      return Result.Err(ParseError(
        output.source.line,
        output.source.column,
        "Incomplete parse",
        source,
        "end of input",
        output.source.getRemaining()
      ))
    }

    return Result.Ok(output.value)
  }
}

fun <T> constant(value: T): Parser<T> =
  Parser { source -> Result.Ok(ParseOutput(value, source, source.line, source.column)) }

fun <T> error(message: String): Parser<T> =
  Parser { source -> Result.Err(
    ParseError(
    source.line,
    source.column,
    message,
    source,
    null,
    null
  )) }

fun text(text: String, message: String? = null): Parser<String> =
  Parser { it.text(text, message) }

fun regexp(regex: Regex, message: String? = null): Parser<String> =
  Parser { it.match(regex, message) }