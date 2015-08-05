package me.jeffshaw.scalaz.stream

import scalaz.concurrent.Task
import scalaz.stream.Process

object ProcessIoIteratorConstructors {
  /**
   * Create a Process from an iterator that requires some external or
   * other mutable resource, while ensuring that the resource is released.
   */
  def iterator[R, O](
    acquire: Task[R]
  )(createIterator: R => Task[Iterator[O]]
  )(release: R => Task[Unit]
  ): Process[Task, O] = {
    Process.await(acquire)(r => Process.eval(createIterator(r)).flatMap(IteratorConstructors.iteratorGo).onComplete(Process.eval_(release(r))))
  }
}
