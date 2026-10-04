package showcase

/**
 * 一本书。borrower = None 表示在架上，Some(姓名) 表示已借出。
 *
 * 用 case class 是因为：打印、比较、copy 改一个字段，这些都自动有。
 */
case class Book(
  id: Int,
  title: String,
  author: String,
  year: Int,
  category: String,
  borrower: Option[String] = None
) {
  def available: Boolean = borrower.isEmpty

  def statusText: String = borrower match {
    case None      => "在架"
    case Some(who) => s"借给 $who"
  }

  def line: String =
    f"$id%3d  ${title}%-24s  ${author}%-14s  $year  ${category}%-8s  $statusText"
}

object Book {
  /** 文件里一行的格式：id|title|author|year|category|borrower */
  def toRecord(book: Book): String = {
    val who = book.borrower.getOrElse("")
    List(book.id, book.title, book.author, book.year, book.category, who).mkString("|")
  }

  def fromRecord(raw: String): Option[Book] = {
    val parts = raw.split("\\|", -1)
    if (parts.length != 6) {
      None
    } else {
      val id = parts(0).toIntOption.getOrElse(0)
      val title = parts(1)
      val author = parts(2)
      val year = parts(3).toIntOption
      val category = parts(4)
      val borrower: Option[String] =
        if (parts(5).isEmpty) None else Some(parts(5))

      year match {
        case Some(y) => Some(Book(id, title, author, y, category, borrower))
        case None    => None
      }
    }
  }
}
