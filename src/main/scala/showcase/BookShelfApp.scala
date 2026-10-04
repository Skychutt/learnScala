package showcase

import scala.io.StdIn.readLine

/**
 * 命令行个人图书馆。
 *
 * 这个小项目有自己的入口 @main def bookShelf，在 IDE 里打开本文件点绿色三角即可。
 * 也可以：sbt "runMain showcase.bookShelf"
 * 自动演示：sbt "runMain showcase.bookShelf preview"
 */
object BookShelfApp {

  def runInteractive(): Unit = {
    banner()
    var lib = Storage.load()
    println(s"已载入 ${lib.books.size} 本书。数据文件：${Storage.defaultPath}")
    println("输入数字选择功能，输入 q 退出。")

    var running = true
    while (running) {
      printMenu()
      readLine(">> ").trim match {
        case "1" => printBooks("全部藏书", lib.books)
        case "2" =>
          printBooks("在架", lib.availableOnly)
          printBooks("已借出", lib.borrowedOnly)
        case "3" =>
          val q = readLine("关键词（书名/作者/分类）：")
          printBooks(s"搜索「$q」", lib.find(q))
        case "4" =>
          val title = readLine("书名：")
          val author = readLine("作者：")
          val year = readLine("年份：").toIntOption.getOrElse(2026)
          val category = readLine("分类：")
          if (title.trim.isEmpty) println("书名不能为空。")
          else {
            lib = lib.add(title, author, year, category)
            Storage.save(lib)
            println(s"已添加，编号 ${lib.books.last.id}。")
          }
        case "5" =>
          val id = readLine("要借的编号：").toIntOption
          val who = readLine("借给谁：")
          id match {
            case None => println("编号必须是数字。")
            case Some(n) =>
              lib.borrow(n, who) match {
                case Left(err) => println(err)
                case Right(next) =>
                  lib = next
                  Storage.save(lib)
                  println("借出成功。")
              }
          }
        case "6" =>
          readLine("要还的编号：").toIntOption match {
            case None => println("编号必须是数字。")
            case Some(n) =>
              lib.giveBack(n) match {
                case Left(err) => println(err)
                case Right(next) =>
                  lib = next
                  Storage.save(lib)
                  println("已归还。")
              }
          }
        case "7" =>
          readLine("要删除的编号：").toIntOption match {
            case None => println("编号必须是数字。")
            case Some(n) =>
              lib.remove(n) match {
                case Left(err) => println(err)
                case Right(next) =>
                  lib = next
                  Storage.save(lib)
                  println("已删除。")
              }
          }
        case "8" => println(lib.stats)
        case "9" =>
          Storage.save(lib)
          println(s"已保存到 ${Storage.defaultPath.toAbsolutePath}")
        case "q" | "Q" | "0" =>
          Storage.save(lib)
          println("已保存，再见。")
          running = false
        case other =>
          println(s"不认识的选项：$other")
      }
    }
  }

  /** 自动走一遍流程，给学习时看输出，也方便确认功能没坏。 */
  def preview(): Unit = {
    banner()
    println("【预览模式】不读键盘，自动演示增删借还和存盘。")
    println()

    var lib = Library.sample
    println("1. 初始藏书")
    printBooks("样例数据", lib.books)

    lib = lib.add("Scala 3 小册", "Anonymous", 2024, "编程")
    println("2. 加入一本新书")
    println("   " + lib.books.last.line)

    println("3. 搜索「Scala」")
    printBooks("结果", lib.find("Scala"))

    println("4. 把 1 号书借给 小明")
    lib.borrow(1, "小明") match {
      case Right(next) =>
        lib = next
        println("   " + lib.byId(1).map(_.line).getOrElse(""))
      case Left(err) => println("   " + err)
    }

    println("5. 再借一次同一本（应当失败）")
    lib.borrow(1, "小红") match {
      case Left(err) => println("   预期中的失败：" + err)
      case Right(_)  => println("   出错了，不应该成功")
    }

    println("6. 归还 1 号")
    lib = lib.giveBack(1).getOrElse(lib)
    println("   " + lib.byId(1).map(_.line).getOrElse(""))

    println("7. 统计")
    println("   " + lib.stats)

    val previewFile = java.nio.file.Path.of("target", "bookshelf-preview.txt")
    Storage.save(lib, previewFile)
    val loaded = Storage.load(previewFile)
    println(s"8. 存盘并再读回来，仍有 ${loaded.books.size} 本")
    println(s"   文件：${previewFile.toAbsolutePath}")
    println()
    println("交互模式请运行：  sbt \"run demo\"")
  }

  private def banner(): Unit = {
    println("=" * 56)
    println("  BookShelf  命令行个人图书馆")
    println("  用 Scala 3 写的小项目：藏书、借还、搜索、存文件")
    println("=" * 56)
  }

  private def printMenu(): Unit = {
    println()
    println("  1 列出全部   2 按在架/借出看   3 搜索")
    println("  4 添加新书   5 借出          6 归还")
    println("  7 删除      8 统计          9 立即保存")
    println("  0 / q  保存并退出")
  }

  private def printBooks(title: String, books: List[Book]): Unit = {
    println(s"── $title（${books.size}）──")
    if (books.isEmpty) println("  （没有书）")
    else books.sortBy(_.id).foreach(b => println("  " + b.line))
  }
}

/** 图书馆自己的程序入口。在 IDE 里点本文件左侧绿色三角就能运行。 */
@main
def bookShelf(args: String*): Unit = {
  args.headOption.map(_.toLowerCase) match {
    case Some("preview") => BookShelfApp.preview()
    case _               => BookShelfApp.runInteractive()
  }
}
