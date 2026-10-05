package selfTry

case class Library(books:List[Book] = Nil) {

  def nextId: Int = books.map(_.id).maxOption.getOrElse(0)+1

  def byId(id:Int):Option[Book] = books.find(_.id == id)

  def add(title:String,author:String,category:String,year:Int)={

    val newBook = Book(nextId,title.trim,author.trim,year,category.trim)
    copy(books = books:+newBook)
  }

  def remove(id:Int): Either[String,Library] = {
    if (byId(id).isEmpty) Left(s"没有编号为 $id 的书")
    else Right(copy(books = books.filterNot(_.id == id)))
    
  }


  def borrow(id:Int,who:String): Either[String,Library] = {
    
    byId(id) match{
      case None => Left(s"没有编号为 $id 的书")
      case Some(book) if book.borrower.isDefined => Left(s"编号为 $id 的书已经被借走")
      case Some(book) if who.trim.isEmpty => Left("借书的人不能为空")
      case Some(book) if book.borrower.isEmpty => Right(replace(book.copy(borrower = Some(who.trim))))
        
    }
  }
    
  def find(keyword:String): List[Book] = {
    if (keyword.trim.toLowerCase.isEmpty ) books
    else books.filter(
      b => List(b.title,b.author,b.category).exists(_.toLowerCase.contains(keyword.trim.toLowerCase)))
  }

}
