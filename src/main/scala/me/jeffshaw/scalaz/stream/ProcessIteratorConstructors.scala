package me.jeffshaw.scalaz.stream

import scalaz.concurrent.Task
import scalaz.stream._

object ProcessIteratorConstructors {

  /**
   * Use a task that creates an iterator as source for a `Process`,
   * which lazily emits the values of the iterator, then halts.
   *
   * Be sure that iteratorCreator uses no external resources.
   *
   * If your iterator uses an external resource, use [[ProcessIoIteratorConstructors.iterator]].
   */
  def iterator[O](iteratorCreator: Task[Iterator[O]]): Process[Task, O] = {
    Process.await(iteratorCreator)(IteratorConstructors.iteratorGo)
  }

}
