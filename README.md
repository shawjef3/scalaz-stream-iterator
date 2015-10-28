## This project is abandoned, as its functionality can now be found in scalaz-stream 0.8.

This project adds iterator constructors to scalaz.stream.Process and scalaz.stream.io. It will become deprecated if scalaz-stream merge request 416 is accepted.

```scala
import scalaz.stream._
import me.jeffshaw.scalaz.stream.IteratorConstructors._

Process.iterator(create iterator)
io.iterator(acquire resource)(create iterator from resource)(release resource)
```

There are versions for scalaz-stream 0.7.3 and 0.7.3a, Scala 2.10 and 2.11. Remove the "a" suffix from the version if you use scalaz 7.0.x.

```scala
"me.jeffshaw.scalaz.stream" %% "iterator" % "3.0.1a"
```
