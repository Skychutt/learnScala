package selfTry

case class Book (
  id:Int,
  title:String,
  author:String,
  year:Int,
  category:String,
  borrower:Option[String]=None             
                ) {

  def available:Boolean = borrower.isEmpty
  
  def statusText:String = borrower match {
    case None => "ÔÚ¼Ü"
    case Some(who) => s"½è¸ø$who"
  }
    
  def line:String = f"$id%3d  ${title}%-24s  ${author}%-14s  $year  ${category}%-8s  $statusText"
   
  
  
}


object Book {
  
  def toRecord(book: Book) = String {
    val who = book.borrower.getOrElse("")
    List(book.id,book.title,book.author,book.year,book.category,who).mkString("|")
  }
  
  def toBook(raw: String):Option[Book]={
    val parts = raw.split("\\|", -1)
    if(parts.length != 6){
      None
    }else{
      val id = parts(0).toIntOption.getOrElse(0)
      val title = parts(1)
      val author = parts(2)
      val year = parts(3).toIntOption
      val category = parts(4)
      val borrower: Option[String] = if (parts(5).isEmpty) None else Some(parts(5))
     
      year match{
        case None => None
        case Some(y) => Some(Book(id,title,author,y,category,borrower))
      }
    }
  }
  
  
  
}











