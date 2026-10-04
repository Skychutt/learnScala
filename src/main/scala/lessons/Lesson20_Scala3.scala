package lessons

/** 第 20 课：Scala 3 日常会碰到的新语法。本项目本身就是 Scala 3。 */
object Lesson20_Scala3 {

  extension (s: String) {
    def shout: String = s.toUpperCase + "!"
    def words: List[String] = s.split("\\s+").toList.filter(_.nonEmpty)
  }

  extension (n: Int) {
    def isEven: Boolean = n % 2 == 0
  }

  def showId(id: Int | String): String = id match {
    case n: Int    => s"数字学号 $n"
    case s: String => s"字符串学号 $s"
  }

  trait Show[T] {
    def show(value: T): String
  }

  given Show[Int] with {
    def show(value: Int): String = s"Int($value)"
  }

  given Show[String] with {
    def show(value: String): String = s"String($value)"
  }

  def printShow[T](value: T)(using s: Show[T]): Unit =
    println("    " + s.show(value))

  object Ages {
    opaque type Age = Int
    object Age {
      def apply(n: Int): Age = {
        require(n >= 0 && n <= 150, "年龄不合法")
        n
      }
      extension (age: Age) def value: Int = age
    }
  }

  def run(): Unit = {
    Teach.why("Scala 3 改了不少写法：可选花括号、扩展方法、given/using、联合类型。课上两种语法都会出现，认准「意思一样」即可。")

    Teach.section("1. 扩展方法：给已有类型加方法")
    println(s"    \"hello\".shout = ${"hello".shout}")
    println(s"    \"scala is fun\".words = ${"scala is fun".words}")
    println(s"    8.isEven = ${8.isEven}")
    Teach.say("不用改 String 的源码，也不用写 StringUtil.shout(s)。")
    Teach.say("extension (x: 类型) { def 新方法 = ... }")

    Teach.section("2. 联合类型 A | B")
    println("    " + showId(1001))
    println("    " + showId("A-1001"))
    Teach.say("适合「就这两种可能」的轻量建模。用的时候必须 match 处理每一种。")
    Teach.say("第 6 课 if 两支类型不同，推断出来的就是这种。")

    Teach.section("3. given / using：需要某种能力时由编译器填入")
    println("    自动选择 Show 实例：")
    printShow(42)
    printShow("hi")
    Teach.say("using Show[T] 表示「调用时请给我一个 Show[T]」。")
    Teach.say("given 是那个实例的定义。旧名字叫 implicit，现在别写 implicit 了。")
    Teach.pitfall("given 找不到时，报错会说 No given instance of type Show[X]。先确认有没有写 given。")

    Teach.section("4. opaque type：对外是新类型，对内是 Int")
    val age = Ages.Age(18)
    println(s"    Age = ${age.value}。不能和普通 Int 随便相加，减少「把年龄当学号」这类错。")

    Teach.section("5. 缩进语法（可选花括号）")
    val xs =
      for
        i <- 1 to 3
        if i != 2
      yield i * 10
    println(s"    不用 {} 的 for = $xs")
    Teach.say("和带花括号的 for 完全等价。老师讲义里 if/while 也给了 then / do 两种对照。")

    Teach.summary(
      "想给 String/Int 加方法：extension，不要再堆 Util",
      "A | B 是联合类型；given/using 是隐式参数的新写法",
      "花括号语法和缩进语法任选一种，作业里保持一致"
    )
    Teach.practice("给 List[Int] 写 extension def average: Double，空列表返回 0.0。")
  }
}
