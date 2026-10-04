import lessons.Course
import showcase.BookShelfApp

/**
 * 入口。
 *
 *   sbt run                运行全部课程
 *   sbt "run 5"            只运行第 5 课
 *   sbt "run 21 22"        运行指定几课
 *   sbt "run preview"      把参数 preview 传给本入口，转去图书馆自动演示
 *   sbt "run demo"         把参数 demo 传给本入口，转去图书馆交互模式
 *   sbt "runMain showcase.bookShelf"  直接启动图书馆自己的 main
 */
@main
def main(args: String*): Unit = {
  args.headOption.map(_.toLowerCase) match {
    case Some("demo") | Some("app") | Some("bookshelf") =>
      BookShelfApp.runInteractive()
    case Some("preview") =>
      BookShelfApp.preview()
    case Some("help" | "-h" | "--help") =>
      Course.printMenu()
      println()
      println("  sbt \"run preview\"   小项目自动演示")
      println("  sbt \"run demo\"      小项目交互模式")
    case _ =>
      Course.start(args)
  }
}
