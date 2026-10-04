package lessons

/** 第 01 课：程序从哪里开始、注释、打印，以及「表达式」这个核心观念。 */
object Lesson01_Hello {

  def run(): Unit = {
    Teach.why("任何语言先过三关：怎么写注释、怎么把字打到屏幕上、程序从哪一行开始跑。Scala 还多一个关键观念：几乎所有东西都是表达式，都有值。")

    Teach.section("1. 注释有三种")
    Teach.say("双斜杠 // 到行尾。用来解释「为什么这样写」。")
    Teach.say("斜杠星 /* ... */ 可跨行。临时关掉一段代码时常用。")
    Teach.say("文档注释 /** ... */ 给类和方法写说明，IDE 把鼠标放上去能看到。")

    Teach.section("2. 程序入口")
    Teach.say("本项目入口是 src/main/scala/main.scala 里的 @main def main(...)")
    Teach.say("@main 表示：用 sbt run 或 IDE 的 Run 时，从这里开始。")
    Teach.vsJava("Java 要写 public static void main(String[] args)。Scala 3 用 @main 即可。")
    Teach.say("一个项目里如果有多个 @main，sbt run 会问你跑哪一个。所以本教程只留这一个入口。")

    Teach.section("3. 打印")
    println("  hello, Scala")
    print("  同一行继续 ")
    println("输出")
    printf("  printf：PI ≈ %.2f%n", 3.14159)
    Teach.say("println 换行；print 不换行；printf 按格式输出，和 C 的 printf / Java Formatter 同类。")

    Teach.section("4. 语句 vs 表达式")
    Teach.say("语句：做事，不一定有值。例如 println(...) 的结果是 Unit（可以理解为「没有有用的值」）。")
    Teach.say("表达式：做事，同时算出一个值。例如 1 + 1 的值是 2。")
    Teach.say("Scala 里 if、代码块、甚至 match 都是表达式，所以可以写：val x = if (...) a else b")

    val answer = 1 + 1
    println(s"  1 + 1 = $answer")

    val blockValue = {
      val a = 3
      val b = 4
      a + b
    }
    println(s"  花括号代码块的值 = 最后一行 = $blockValue")
    Teach.say("块里可以有很多行，真正当作结果的是最后那一行。前面的行通常在做准备。")
    Teach.pitfall("如果最后一行是 println(...)，块的值就是 Unit，不是你以为的那个数字。")

    Teach.summary(
      "@main 标记入口；println / print / printf 负责输出",
      "注释不会被执行，写给人和未来的自己看",
      "表达式有值；代码块的值等于最后一行"
    )
    Teach.practice("把 hello 改成你的名字；再写一个代码块计算 (2+3)*4，把它赋给 val 打出来。")
  }
}
