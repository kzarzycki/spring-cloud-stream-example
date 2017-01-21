package com.getindata.examples

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.stream.annotation.{EnableBinding, Input, Output, StreamListener}
import org.springframework.cloud.stream.messaging.{Processor, Sink, Source}
import reactor.core.publisher.Flux

import scala.compat.java8.FunctionConverters._

@SpringBootApplication
@EnableBinding(value = Array(classOf[Processor]))
class ExampleStream {

  @StreamListener
  @Output(Source.OUTPUT)
  def filter(@Input(Sink.INPUT) input: Flux[String]) : Flux[String] = {
    val set = ('A' to 'Z').toSet
    input
      .filter(asJavaPredicate { m => set.contains(m.charAt(0)) })
      .map( asJavaFunction { m => m.toUpperCase } )

  }
}


object ExampleStream extends App {
  SpringApplication.run(classOf[ExampleStream], args :_* )
}
