package me.jeffshaw.scalaz.stream

import scalaz.concurrent.Task
import scalaz.stream.Process
import me.jeffshaw.scalaz.stream.IteratorConstructors._

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
    Process.await(acquire)(r => Process.eval(createIterator(r)).flatMap(IteratorConstructors.iteratorGo).onComplete(Process.eval_(release(r))))
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
  ): Process[Task, Process[Task, O]] = {
    def createIteratorProcesses(r: R): Process[Task, Process[Task, O]] = {
      for {
        iterators <- Process.eval(createIterators(r))
        iterator <- Process.emitAll(iterators.toSeq)
      } yield Process.iterator(Task.delay(iterator))
    }
    Process.await(acquire)(r => createIteratorProcesses(r).onComplete(Process.eval_(release(r))))
  }
}
