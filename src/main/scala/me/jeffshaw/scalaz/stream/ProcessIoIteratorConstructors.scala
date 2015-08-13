package me.jeffshaw.scalaz.stream

import scalaz.concurrent.Task
import scalaz.stream._

object ProcessIoIteratorConstructors {
  /**
   * Create a Process from an iterator that requires some external or
   * other mutable resource, while ensuring that the resource is released.
   *
   * Use `iterators` if the resource is associated with multiple iterators.
   */
  def iterator[R, O](
    acquire: Task[R]
  )(createIterator: R => Task[Iterator[O]]
  )(release: R => Task[Unit]
  ): Process[Task, O] = {
    Process.await(acquire)(r => Process.await(createIterator(r))(IteratorConstructors.iteratorGo).onComplete(Process.eval_(release(r))))
  }

  /**
   * Create a Process from an external resource associated with multiple
   * iterators, while ensuring that the resource is released.
   *
   * Use `merge.mergeN` on the result to interleave the iterators, or
   * .flatMap(identity) to emit them in order.
   *
   * @param maxOpen Max number of open (running) processes at a time
   */
  def iterators[R, O](
    maxOpen: Int
  )(acquire: Task[R]
  )(createIterators: R => Task[Iterable[Iterator[O]]]
  )(release: R => Task[Unit]
  ): Process[Task, O] = {
    def createIteratorProcesses(r: R): Task[Iterable[Process[Task, O]]] = {
      for {
        iterators <- createIterators(r)
      } yield iterators.map(iterator => IteratorConstructors.iteratorGo(iterator))
    }
    Process.await(acquire) { r =>
      merge.mergeN(maxOpen)(Process.await(createIteratorProcesses(r)) { iterators =>
        Process.emitAll(iterators.toSeq)
      }).onComplete(Process.eval_(release(r)))
    }
  }

  /**
   * Create a Process from an external resource associated with multiple
   * iterators, while ensuring that the resource is released.
   *
   * Use `merge.mergeN` on the result to interleave the iterators, or
   * .flatMap(identity) to emit them in order.
   */
  def iterators[R, O](
    acquire: Task[R]
  )(createIterators: R => Task[Iterable[Iterator[O]]]
  )(release: R => Task[Unit]
  ): Process[Task, O] = {
    iterators[R, O](0)(acquire)(createIterators)(release)
  }

}
