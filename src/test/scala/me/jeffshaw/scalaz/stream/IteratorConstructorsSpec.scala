package me.jeffshaw.scalaz.stream

import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import scalaz.concurrent.Task
import scalaz.stream._
import me.jeffshaw.scalaz.stream.IteratorConstructors._

class IteratorConstructorsSpec
  extends FunSuite
  with GeneratorDrivenPropertyChecks {

  test("Process.iterator completes immediately from an empty iterator") {
    Process.iterator[Int](Task(Iterator.empty)).runLog.run.isEmpty
  }

  test("Process.iterator uses all its values and completes") {
    forAll { (ints: Vector[Int]) =>
      val iterator = Task(ints.toIterator)
      assertResult(ints)(Process.iterator[Int](iterator).runLog.run)
    }
  }

  test("Process.iterator is re-usable") {
    forAll { (ints: Vector[Int]) =>
      val iterator = Task(ints.toIterator)
      val firstRun = Process.iterator(iterator).runLog.run
      val secondRun = Process.iterator(iterator).runLog.run
      assertResult(ints)(firstRun)
      assertResult(ints)(secondRun)
    }
  }

  //io.iterator tests

  case class IteratorResource[T](items: T*) {
    private var released: Boolean = false

    def release(): Unit = {
      released = true
    }

    def isReleased: Boolean = released

    def iterator: Iterator[T] = items.iterator
  }

  test("io.iterator releases its resource") {
    forAll { (ints: Vector[Int]) =>
      var isReleased: Boolean = false

      def acquire(): Task[IteratorResource[Int]] = {
        Task.delay {
          IteratorResource(ints: _*)
        }
      }

      def release(resource: IteratorResource[_]): Task[Unit] = {
        Task.delay {
          resource.release()
          isReleased = resource.isReleased
        }
      }

      def createIterator(resource: IteratorResource[Int]): Task[Iterator[Int]] = {
        Task.delay {
          resource.iterator
        }
      }

      io.iterator(acquire())(createIterator)(release).run.run

      assert(isReleased)
    }
  }

}
