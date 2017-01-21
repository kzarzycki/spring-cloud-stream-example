package com.getindata.examples.stream

import com.getindata.examples.ExampleStream
import org.junit.Test
import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfterAll, FunSuite, Suite}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.stream.messaging.Processor
import org.springframework.cloud.stream.test.binder.MessageCollector
import org.springframework.core.annotation.{AnnotatedElementUtils, AnnotationAttributes}
import org.springframework.messaging.support.GenericMessage
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.{ContextConfiguration, TestContext, TestContextManager}
import org.springframework.test.context.junit4.{SpringJUnit4ClassRunner, SpringRunner}
import org.springframework.test.context.support.{AnnotationConfigContextLoader, DirtiesContextTestExecutionListener}
import org.springframework.util.Assert

@ContextConfiguration(
  loader = classOf[AnnotationConfigContextLoader],
  classes = Array(classOf[ExampleStream])
)
class ExampleStreamTest extends FunSuite with SpringTest {

  @Autowired var processor: Processor = null
  @Autowired var messageCollector: MessageCollector = null



  test("test the exampleStream") {
    val message = new GenericMessage("Hello!")
    processor.input().send(message)

    val received = messageCollector.forChannel(processor.output()).poll()
    assert(received.getPayload == "HELLO!")
  }

  test("test the exampleStream 2") {
    val message = new GenericMessage("world!")
    processor.input().send(message)

    val received = Option(messageCollector.forChannel(processor.output()).poll())
    assert(received.isEmpty )
  }
}

trait SpringTest extends  BeforeAndAfterAll { this: Suite =>
  private val testContextManager = new TestContextManager(this.getClass)

  abstract override def beforeAll(): Unit = {
    super.beforeAll
    testContextManager. registerTestExecutionListeners(AlwaysDirtiesContextTestExecutionListener)
    testContextManager.beforeTestClass()
    testContextManager.prepareTestInstance(this)

  }

  abstract override def afterAll(): Unit = {
    testContextManager.afterTestClass()
    super.afterAll
  }

}

/**
  * Test execution listener that always dirties the context to ensure that contexts get cleaned after test execution.
  *
  * Note that this class dirties the context after all test methods have run.
  */
protected object AlwaysDirtiesContextTestExecutionListener extends DirtiesContextTestExecutionListener {

  @throws(classOf[Exception])
  override def afterTestClass(testContext: TestContext) {
    val testClass: Class[_] = testContext.getTestClass
    Assert.notNull(testClass, "The test class of the supplied TestContext must not be null")

    val annotationType: String = classOf[DirtiesContext].getName

    val annAttrs: AnnotationAttributes = AnnotatedElementUtils.getMergedAnnotationAttributes(testClass, annotationType)
    val hierarchyMode: DirtiesContext.HierarchyMode = if (annAttrs == null) null else annAttrs.getEnum[DirtiesContext.HierarchyMode]("hierarchyMode")
    dirtyContext(testContext, hierarchyMode)
  }
}