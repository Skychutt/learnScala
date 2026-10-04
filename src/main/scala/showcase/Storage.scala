package showcase

import java.nio.file.{Files, Path}

/** 把图书馆存成纯文本，不依赖第三方库。一行一本书。 */
object Storage {
  val defaultPath: Path = Path.of("data", "bookshelf.txt")

  def save(library: Library, path: Path = defaultPath): Unit = {
    val parent = path.getParent
    if (parent != null) Files.createDirectories(parent)
    val body = library.books.map(Book.toRecord).mkString("\n")
    Files.writeString(path, body + (if (body.isEmpty) "" else "\n"))
  }

  def load(path: Path = defaultPath): Library = {
    if (!Files.exists(path)) Library.sample
    else {
      val books = Files.readString(path)
        .split("\n")
        .toList
        .map(_.trim)
        .filter(_.nonEmpty)
        .flatMap(Book.fromRecord)
      if (books.isEmpty) Library.sample else Library(books)
    }
  }
}
