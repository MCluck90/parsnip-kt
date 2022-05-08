package com.github.mcluck90.parsnip

@Suppress("UNCHECKED_CAST")
class Result<T, E>(private val value: T?, private val error: E?) {
  companion object {
    fun <T, E> Ok(value: T): Result<T, E> = Result(value, null)
    fun <E, T> Err(error: E): Result<T, E> = Result(null, error)
  }

  fun isOk(): Boolean = error == null
  fun isErr(): Boolean = error != null

  fun unwrap(): T {
    if (this.isOk()) {
      return this.value!!
    }
    if (this.error is Throwable) {
      throw this.error
    }
    throw Exception("${this.error}")
  }

  fun unwrapErr(): E {
    if (this.isErr()) {
      return this.error!!
    }
    throw Exception("Expected error, got ${this.value}")
  }

  fun <U> map(map: (T) -> U): Result<U, E> {
    if (this.isOk()) {
      return Ok(map(this.value!!))
    }
    return this as Result<U, E>
  }

  fun <U> mapErr(map: (E) -> U): Result<T, U> {
    if (this.isErr()) {
      return Err(map(this.error!!))
    }
    return this as Result<T, U>
  }
}