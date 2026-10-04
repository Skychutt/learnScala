package lessons

import java.io.{BufferedReader, BufferedWriter, FileNotFoundException, FileReader, FileWriter, IOException}
import java.nio.file.{Files, Path}
import scala.io.Source
import scala.util.Using

/** 第 22 课：控制台输入输出 + 文件读写。对应老师讲义 Chapter 2 后半。 */
object Lesson22_IO {

  def run(): Unit = {
    Teach.why("程序要和人说话（键盘输入、屏幕输出），也要记住数据（读写文件）。老师第 2 讲的重点就在这里。")

    val dir = Path.of("target", "io-demo")
    Files.createDirectories(dir)
    val namesFile = dir.resolve("names.txt")
    val helloFile = dir.resolve("hello.txt")

    Teach.section("1. 屏幕输出：print / println / printf")
    Teach.say("println 打印并换行；print 不换行；printf 用 %d %s %.2f 这种格式。")
    printf("  printf 例子：整数 %d，小数 %.2f，字符串 %s%n", 7, 3.14159, "Scala")
    Teach.say("printf 的格式和 f\"$x%.2f\" 是同一套（Java Formatter）。")
    Teach.vsJava("这些方法其实来自 System.out，Scala 预引进了，所以能直接当函数用。")

    Teach.section("2. 键盘输入：scala.io.StdIn")
    Teach.say("常用：import scala.io.StdIn.readLine")
    Teach.say("  val name = readLine(\"请输入姓名：\")   // 先提示，再读一整行，得到 String")
    Teach.say("还有 readInt / readDouble / readBoolean 等，但那一行必须「整行都是这个值」。")
    Teach.pitfall("readInt() 读到 abc 会抛 NumberFormatException。更稳的做法：readLine() 再 toIntOption。")
    Teach.say("本课自动运行时不真正等待键盘，避免卡在 sbt run。自己写程序时把注释里的代码打开即可。")
    Teach.say("示范（模拟用户输入了 Alice）：")
    val simulated = "Alice"
    println(s"  Hello, $simulated!")

    Teach.section("3. 用 Java 对象写文件：BufferedWriter")
    val writer = BufferedWriter(FileWriter(helloFile.toFile))
    try {
      writer.write("Hello, World!")
      writer.newLine()
      writer.write("This is a test file.\n")
    } finally {
      writer.close()
    }
    Teach.say(s"已写入 $helloFile")
    Teach.say("写完一定 close。不关闭可能丢数据（还在缓冲区里）。")

    Teach.section("4. 用 Java 对象读文件：BufferedReader")
    val reader = BufferedReader(FileReader(helloFile.toFile))
    try {
      var line = reader.readLine()
      while (line != null) {
        println(s"  读到：$line")
        line = reader.readLine()
      }
    } finally {
      reader.close()
    }
    Teach.say("readLine() 返回 null 表示文件结束。这是 Java 习惯，Scala 里能用，但不是最地道的写法。")

    Teach.section("5. 异常：Scala 不强制你写 try")
    Teach.say("Java 里 IOException 是 checked exception，方法签名必须声明。")
    Teach.say("Scala 把它们都当成运行时异常：不写 try 也能编译，但文件不存在仍会崩溃。")
    Teach.say("真正给别人用的程序，还是要捕获。")
    try {
      BufferedReader(FileReader("definitely-missing-xyz.txt")).close()
    } catch {
      case _: FileNotFoundException => Teach.say("捕获 FileNotFoundException：文件不存在时走这里")
      case e: IOException           => Teach.say(s"其它 IO 问题：${e.getMessage}")
    }

    Teach.section("6. 用 Scala Source 读文件（更常见）")
    Files.writeString(namesFile, "Alice\nBob\nCarol\n")
    Teach.say("Source.fromFile 得到一个字符迭代器：")
    Teach.say("  getLines()  → 一行一行； mkString → 整个文件变成一个大字符串")
    val content = Using.resource(Source.fromFile(namesFile.toFile, "UTF-8")) { src =>
      src.getLines().toList
    }
    content.foreach(name => println(s"  Hello, $name"))
    Teach.pitfall("Source 用完必须 close。用 scala.util.Using 可以保证关，即使中途出错。")
    Teach.say("Scala 标准库没有和 Source 对称的「写文件」类，写文件继续用 Java 或 java.nio。")

    Teach.section("7. 现代写法：java.nio.file.Files")
    val nioFile = dir.resolve("nio.txt")
    Files.writeString(nioFile, "用 NIO 一行写完\n第二行\n")
    val nioText = Files.readString(nioFile)
    val nioLines = Files.readAllLines(nioFile)
    Teach.say(s"readString 全文：${nioText.replace("\n", "\\n")}")
    Teach.say(s"readAllLines：${nioLines.toArray.mkString(" | ")}")
    Teach.say("小项目里 Files.readString / writeString 往往比自己管缓冲区更省事。")

    Teach.section("8. 老师讲义还提到 OS-Lib")
    Teach.say("OS-Lib 用 os.pwd / \"file.txt\" 这种路径，os.read / os.write.over / os.write.append。")
    Teach.say("需要额外依赖，本课程先不引入。会用 Source + Files 就够完成作业和考试。")

    Teach.summary(
      "输出用 println / print / printf；输入用 StdIn.readLine，再自己转类型更安全",
      "Java 的 BufferedReader/Writer 能用，记得 close，并用 try/catch 处理找不到文件",
      "读文件优先 Source.getLines 或 Files.readString；写文件用 Files.writeString 或 BufferedWriter",
      "Scala 不强制处理异常，但文件程序必须处理"
    )
    Teach.practice("写程序：readLine 问用户一句话，把它追加写入 memo.txt，再把文件全部读出来打印。")
  }
}
