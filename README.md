This project adds iterator constructors to scalaz.stream.Process and scalaz.stream.io. It will become deprecated if scalaz-stream merge request 416 is accepted.

```scala
import scalaz.stream._
import me.jeffshaw.scalaz.stream.IteratorConstructors._

Process.iterator(create iterator)
io.iterator(acquire resource)(create iterator from resource)(release resource)
```
