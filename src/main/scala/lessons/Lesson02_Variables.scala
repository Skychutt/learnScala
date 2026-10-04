package lessons

/** 第 02 课：val / var / lazy val。优先不可变，是 Scala 的第一习惯。 */
object Lesson02_Variables {

  def run(): Unit = {
    Teach.why("程序要记住数据。Scala 把「能改」和「不能改」分成两种声明。默认用不能改的 val，代码更好推理，并发时也更安全。")

    Teach.section("1. val：绑定一次，不能再改")
    val name: String = "Scala"
    val year = 2004
    println(s"  语言：$name，诞生年：$year")
    Teach.say("year 没写类型，编译器推断成 Int。写不写类型都可以；对外公开的字段建议写上。")
    Teach.say("// year = 2005  这行如果打开，编译失败：reassignment to val")
    Teach.vsJava("接近 Java 的 final 变量。")

    Teach.section("2. var：可以重新赋值")
    var score = 60
    Teach.say(s"一开始 score = $score")
    score = 95
    Teach.say(s"改成 score = $score")
    Teach.pitfall("类型在第一次赋值时就定了。score = \"A\" 编译失败：不能把 String 塞进 Int。")
    Teach.say("能用 val 就不用 var。循环计数、可变缓冲区这种才需要 var。")

    Teach.section("3. 一行拆开多个值")
    val (x, y) = (10, 20)
    println(s"  左边是模式，右边是元组：x = $x, y = $y")
    Teach.say("以后读文件、函数返回两个结果时，这种拆包会经常用。")

    Teach.section("4. lazy val：第一次用到才算，算完就记住")
    lazy val heavy = {
      println("  （现在才开始计算 heavy）")
      1 + 2 + 3
    }
    println("  还没有访问 heavy")
    println(s"  第一次访问 heavy = $heavy")
    println(s"  第二次访问 heavy = $heavy  （不会再算一遍）")
    Teach.say("适合：不一定用得到，或计算比较贵（读大文件、连数据库）。")
    Teach.pitfall("lazy val 不是 var。它只延迟计算，算出以后仍然不能改。")

    Teach.summary(
      "默认 val；只有状态确实会变时才用 var",
      "类型可以手写，也可以让编译器推断，但不能中途改成另一种类型",
      "lazy val 第一次访问才计算，之后缓存结果"
    )
    Teach.practice("声明 val r = 2.0，计算圆周长 2 * 3.14 * r；再试着把 r 改成 var 并改值，看输出变化。")
  }
}
