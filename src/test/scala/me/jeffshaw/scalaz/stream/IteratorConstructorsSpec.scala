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

    val iterator: Iterator[T] = new Iterator[T] {
      val inner = items.iterator
      override def hasNext: Boolean = {
        ! isReleased && inner.hasNext
      }

      override def next(): T = {
        if (isReleased) {
          throw new Exception("resource was released")
        } else {
          inner.next()
        }
      }
    }
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

  case class IteratorsResource[T](sequences: Seq[T]*) {
    private var released: Boolean = false

    def release(): Unit = {
      released = true
    }

    def isReleased: Boolean = released

    val iterators: Iterable[Iterator[T]] =
      sequences.map { items =>
        new Iterator[T] {
          val inner = items.iterator

          override def hasNext: Boolean = {
            !isReleased && inner.hasNext
          }

          override def next(): T = {
            if (isReleased) {
              throw new Exception("resource was released")
            } else {
              inner.next()
            }
          }
        }
      }
  }

  test("io.iterators releases its resoures") {
    forAll { (ints: Vector[Vector[Int]]) =>
      var isReleased: Boolean = false

      def acquire(): Task[IteratorsResource[Int]] = {
        Task.delay {
          IteratorsResource(ints: _*)
        }
      }

      def release(resource: IteratorsResource[_]): Task[Unit] = {
        Task.delay {
          resource.release()
          isReleased = resource.isReleased
        }
      }

      def createIterators(resource: IteratorsResource[Int]): Task[Iterable[Iterator[Int]]] = {
        Task.delay {
          resource.iterators
        }
      }

      val results = io.iterators(acquire())(createIterators)(release).runLog.run

      assert(isReleased)

      assertResult(ints.flatten.toSet)(results.toSet)
    }
  }

}
