package com.github.partition.compose

interface Screen<T> {
  fun view(state: T)
}