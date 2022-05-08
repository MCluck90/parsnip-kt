package com.github.mcluck90.parsnip

class ParseError(val line: Int, val column: Int, override val message: String?, val source: Source, val expected: String?, val actual: String?): java.lang.Exception() {
  override fun toString(): String {
    var errorMessage = "${this.message} [${this.line}:${this.column}]"
    if (this.expected != null && this.actual != null) {
      errorMessage += "\nExpected: ${this.expected}\nActual: ${actual}\n"
    }
    return super.toString()
  }
}
