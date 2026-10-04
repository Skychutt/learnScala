package lessons

/** 第 19 课：泛型 [T]，以及 for 推导式（map / flatMap / filter 的语法糖）。 */
object Lesson19_GenericsAndFor {

  def headOption[T](xs: List[T]): Option[T] = xs match {
    case Nil    => None
    case h :: _ => Some(h)
  }

  def pair[A, B](a: A, b: B): (A, B) = (a, b)

  def maxValue[A](a: A, b: A)(using ord: Ordering[A]): A =
    if (ord.gt(a, b)) a else b

  case class User(name: String, age: Int)

  def run(): Unit = {
    Teach.why("同一套逻辑不该为 Int 写一遍、为 String 再写一遍。泛型 [T] 让类型由调用方决定，又保持类型安全。for 推导式则让「连续的 Option/List 操作」读起来像中文顺序。")

    Teach.section("1. 类型参数 [T]")
    println(s"    headOption(List(1,2,3)) = ${headOption(List(1, 2, 3))}")
    println(s"    headOption(Nil)         = ${headOption[Int](Nil)}")
    println(s"    pair(\"age\", 18)         = ${pair("age", 18)}")
    println(s"    maxValue(3, 9)          = ${maxValue(3, 9)}")
    println(s"    maxValue(\"ab\",\"cd\")     = ${maxValue("ab", "cd")}")
    Teach.say("[T] 是占位符：调用 headOption(List(1,2)) 时 T 就是 Int。")
    Teach.say("maxValue 还要能比较大小，所以要一个 using Ordering[A]——编译器自动提供 Int、String 的排序。第 20 课细讲 given/using。")

    Teach.section("2. for 推导式：多个生成器")
    val nums = List(1, 2, 3)
    val chars = List("a", "b")
    val combos = for {
      n <- nums if n % 2 == 1
      c <- chars
    } yield s"$c$n"
    println(s"    奇数 n 配每个字母 = $combos")
    Teach.say("读法：取出 n（还得是奇数），再取出 c，拼成字符串。底层是 filter + flatMap + map。")

    Teach.section("3. Option 也能写 for")
    val maybeUser = for {
      name <- Some("Alice")
      age  <- Some(20)
    } yield User(name, age)
    val missing = for {
      name <- Some("Bob")
      age  <- Option.empty[Int]
    } yield User(name, age)
    println(s"    两个 Some     = $maybeUser")
    println(s"    有一个 None   = $missing")
    Teach.say("中途任何一个 None，整个结果就是 None。这比一串 if (x == null) 干净。")

    Teach.section("4. 从 Map 里拆键值")
    val scores = Map("Alice" -> 90, "Bob" -> 80, "Carol" -> 70)
    val passed = for {
      (name, score) <- scores.toList if score >= 80
    } yield name
    println(s"    及格名单 = $passed")

    Teach.summary(
      "[T] 让方法/类能处理多种类型，又不会失去类型检查",
      "for { a <- A; b <- B } yield f(a,b)  就是 flatMap + map",
      "Option / List / Either 都能写 for，遇到空/失败会自动停"
    )
    Teach.practice("用 for 把 List(1,2,3) 和 List(10,20) 两两相加；再写 identity[T](x: T): T。")
  }
}
