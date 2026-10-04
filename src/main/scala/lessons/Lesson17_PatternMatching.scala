package lessons

/** 第 17 课：match / case。老师在 Chapter 1 就引入了，这里完整展开。 */
object Lesson17_PatternMatching {

  case class Point(x: Int, y: Int)

  def describe(x: Any): String = x match {
    case 0                      => "整数零"
    case n: Int if n < 0        => s"负数 $n"
    case n: Int                 => s"正整数 $n"
    case "scala"                => "字符串 scala"
    case s: String              => s"其它字符串：$s"
    case Point(0, 0)            => "原点"
    case Point(x, 0)            => s"在 X 轴上，x=$x"
    case Point(x, y)            => s"点 ($x, $y)"
    case list: List[_] if list.length > 3 => s"比较长的列表：$list"
    case a :: b :: Nil          => s"恰好两个元素：$a, $b"
    case head :: tail           => s"非空列表，头=$head 尾=$tail"
    case Nil                    => "空列表"
    case (a, b)                 => s"二元组 ($a, $b)"
    case _                      => "其它情况"
  }

  def run(): Unit = {
    Teach.why("if/else 适合简单判断。值一多、结构一复杂，match 更清楚：按值、按类型、按结构拆开。它也是表达式，可以赋给 val。小项目的菜单就是一个大 match。")

    Teach.section("1. 从上往下，命中第一条就停")
    val samples: List[Any] = List(
      0, -5, 8, "scala", "hello",
      Point(0, 0), Point(4, 0), Point(1, 2),
      List(1, 2), List(1, 2, 3, 4), Nil, ("a", 1)
    )
    samples.foreach(v => println(s"    $v  =>  ${describe(v)}"))

    Teach.section("2. 几种常见模式")
    Teach.say("常量：case 0 =>")
    Teach.say("类型：case n: Int =>")
    Teach.say("守卫：case n: Int if n < 0 =>    先配上类型，再额外判断")
    Teach.say("构造器：case Point(x, y) =>     要求是 case class 或有 unapply")
    Teach.say("列表：case head :: tail =>      :: 拆头尾；Nil 是空列表")
    Teach.say("元组：case (a, b) =>")
    Teach.say("通配：case _ =>                 必须放最后，否则后面永远走不到")

    Teach.section("3. 直接在 val 上解构")
    val Point(px, py) = Point(3, 4)
    println(s"    val Point(px, py) = ...  得到 px=$px py=$py")
    Teach.pitfall("如果右边不是 Point，这行会抛 MatchError。不确定时用 match 或 Option。")

    Teach.section("4. match 是表达式")
    val code = 404
    val http = code match {
      case 200 => "OK"
      case 404 => "Not Found"
      case 500 => "Server Error"
      case _   => "Unknown"
    }
    println(s"    HTTP $code → $http")

    Teach.summary(
      "从上往下匹配，_ 放最后",
      "可以按值、按类型、按守卫、按 case class / 列表 / 元组的结构拆",
      "match 有值，所以可以 val x = y match { ... }"
    )
    Teach.practice("写 describeHttp(code: Int)：200/404/500 返回对应短语，其它 Unknown。再 match 一个 Option[Int]。")
  }
}
