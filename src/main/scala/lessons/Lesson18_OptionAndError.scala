package lessons

import scala.util.{Failure, Success, Try}

/** 第 18 课：Option / Either / Try。文件 IO 的异常处理会用到这里的 try/catch。 */
object Lesson18_OptionAndError {

  def findScore(name: String): Option[Int] = {
    val db = Map("Alice" -> 90, "Bob" -> 80)
    db.get(name)
  }

  def parseInt(raw: String): Either[String, Int] =
    raw.toIntOption.toRight(s"不是整数：$raw")

  def divide(a: Int, b: Int): Try[Int] = Try {
    if (b == 0) throw new ArithmeticException("除数不能为 0")
    a / b
  }

  def run(): Unit = {
    Teach.why("「可能没有」和「可能失败」如果用 null 和乱抛异常，调用方稍不注意就崩溃。Scala 的习惯是：没有值用 Option，失败信息用 Either，包一层可能炸的代码用 Try。")

    Teach.section("1. Option：Some(值) 或 None")
    println(s"    Alice = ${findScore("Alice")}    Carol = ${findScore("Carol")}")
    println(s"    getOrElse 默认 0：Carol → ${findScore("Carol").getOrElse(0)}")
    println(s"    map 只对 Some 生效：Carol+5 → ${findScore("Carol").map(_ + 5).getOrElse(0)}")
    findScore("Bob") match {
      case Some(score) => println(s"    Bob 有成绩：$score")
      case None        => println("    Bob 没有成绩")
    }
    Teach.pitfall("option.get 在 None 时会抛异常，等于白用 Option。请用 getOrElse / match / fold。")
    Teach.say("Map.get、find、toIntOption 都已经返回 Option，顺着用即可。")

    Teach.section("2. Either：Left 失败，Right 成功")
    println(s"    parseInt(\"12\") = ${parseInt("12")}")
    println(s"    parseInt(\"ab\") = ${parseInt("ab")}")
    Teach.say("约定右边是成功（Right 也有「正确」的意思）。小项目借书失败返回 Left(原因) 就是这种。")

    Teach.section("3. Try：把可能抛异常的代码包起来")
    println(s"    10/2 = ${divide(10, 2)}")
    println(s"    10/0 = ${divide(10, 0)}")
    divide(10, 2) match {
      case Success(v) => println(s"    Success：$v")
      case Failure(e) => println(s"    Failure：${e.getMessage}")
    }

    Teach.section("4. try / catch / finally")
    Teach.say("Scala 不强制捕获异常（和 Java checked exception 不同），但文件、网络仍建议捕获。")
    try {
      val n = "123".toInt
      println(s"    try 成功：$n")
    } catch {
      case e: NumberFormatException => println(s"    格式错误：${e.getMessage}")
      case e: Exception             => println(s"    其它异常：${e.getMessage}")
    } finally {
      println("    finally：成功失败都会走，适合 close 文件")
    }
    Teach.say("catch 本身就是 match：按异常类型分发。")

    Teach.summary(
      "可能没有 → Option；不要用 null，也不要乱 .get",
      "带失败原因 → Either[错误, 成功]；Left 失败 Right 成功",
      "可能抛异常 → Try 或 try/catch；关资源用 finally 或 Using"
    )
    Teach.practice("写 firstEven(xs: List[Int]): Option[Int]；再写 safeHead(xs): Either[String, Int]，空列表返回 Left。")
  }
}
