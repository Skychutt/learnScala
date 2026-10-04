package showcase

/**
 * 不可变的藏书库：每次增删借还都返回「新的 Library」，原来那份不变。
 * 这是 Scala 里很典型的建模方式，方便推理，也方便撤销/重做。
 */
case class Library(books: List[Book] = Nil) {

  def nextId: Int = books.map(_.id).maxOption.getOrElse(0) + 1

  def byId(id: Int): Option[Book] = books.find(_.id == id)

  def add(title: String, author: String, year: Int, category: String): Library = {
    val book = Book(nextId, title.trim, author.trim, year, category.trim)
    copy(books = books :+ book)
  }

  def remove(id: Int): Either[String, Library] = {
    if (byId(id).isEmpty) Left(s"没有编号 $id 的书")
    else Right(copy(books = books.filterNot(_.id == id)))
  }

  def borrow(id: Int, who: String): Either[String, Library] = {
    byId(id) match {
      case None => Left(s"没有编号 $id 的书")
      case Some(book) if book.borrower.isDefined =>
        Left(s"《${book.title}》已经借给 ${book.borrower.get}")
      case Some(_) if who.trim.isEmpty =>
        Left("借阅人姓名不能为空")
      case Some(book) =>
        Right(replace(book.copy(borrower = Some(who.trim))))
    }
  }

  def giveBack(id: Int): Either[String, Library] = {
    byId(id) match {
      case None => Left(s"没有编号 $id 的书")
      case Some(book) if book.available =>
        Left(s"《${book.title}》本来就在架上")
      case Some(book) =>
        Right(replace(book.copy(borrower = None)))
    }
  }

  def find(keyword: String): List[Book] = {
    val q = keyword.trim.toLowerCase
    if (q.isEmpty) books
    else books.filter { b =>
      List(b.title, b.author, b.category).exists(_.toLowerCase.contains(q))
    }
  }

  def availableOnly: List[Book] = books.filter(_.available)
  def borrowedOnly: List[Book] = books.filterNot(_.available)

  def stats: String = {
    val total = books.size
    val out = borrowedOnly.size
    val grouped = books.groupBy(_.category).toList.sortBy(_._1)
    val byCat = grouped.map { case (cat, xs) => s"$cat ${xs.size} 本" }.mkString("，")
    s"共 $total 本，在架 ${total - out} 本，借出 $out 本。分类：$byCat"
  }

  private def replace(updated: Book): Library =
    copy(books = books.map(b => if (b.id == updated.id) updated else b))
}

object Library {
  def sample: Library = Library(List(
    Book(1, "Programming in Scala", "Odersky", 2021, "编程"),
    Book(2, "Scala for the Impatient", "Horstmann", 2022, "编程"),
    Book(3, "How to Design Programs", "Felleisen", 2018, "函数式"),
    Book(4, "Functional Programming in Scala", "Chiusano", 2023, "函数式"),
    Book(5, "The Pragmatic Programmer", "Hunt", 2019, "工程")
  ))
}
