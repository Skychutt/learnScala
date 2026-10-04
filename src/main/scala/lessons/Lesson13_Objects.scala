package lessons

/** 第 13 课：object 单例、伴生对象、apply / unapply。Scala 没有 static。 */
object Lesson13_Objects {

  object MathUtil {
    val Pi = 3.14159
    def square(n: Int): Int = n * n
  }

  class User private (val name: String, val age: Int) {
    override def toString: String = s"User($name, $age)"
  }

  object User {
    def apply(name: String, age: Int): User = {
      require(age >= 0, "年龄不能为负")
      new User(name, age)
    }
    def unapply(user: User): Option[(String, Int)] = Some((user.name, user.age))
    def guest: User = new User("游客", 0)
  }

  def run(): Unit = {
    Teach.why("有些东西全程序只需要一份：工具方法、配置、入口。Java 用 static，Scala 用 object。和 class 同名的 object 叫伴生对象，常用来当工厂。")

    Teach.section("1. object 是单例")
    println(s"    MathUtil.Pi = ${MathUtil.Pi}")
    println(s"    MathUtil.square(9) = ${MathUtil.square(9)}")
    Teach.say("不用 new。整个 JVM 里只有一份 MathUtil。")
    Teach.vsJava("把 Java 的 static 方法/字段放进 object 即可。")

    Teach.section("2. 伴生对象：和类同名，放在同一文件")
    val u1 = User("Alice", 20)
    val u2 = User.guest
    println(s"    u1 = $u1")
    println(s"    u2 = $u2")
    Teach.say("User(\"Alice\", 20) 实际调用的是 User.apply(...)。")
    Teach.say("主构造器是 private 的，外面不能 new User(...)，只能走工厂，这样校验（年龄 ≥ 0）不会被绕过。")

    Teach.section("3. unapply：让 match 能拆开对象")
    u1 match {
      case User(n, a) => println(s"    匹配成功：名字=$n 年龄=$a")
      case _          => println("    没有匹配到")
    }
    Teach.say("case class 自动生成 apply/unapply。普通类想要同样的写法，就自己在伴生对象里实现。")

    Teach.section("4. 什么该放 class，什么该放 object")
    Teach.say("一份数据一个实例 → class（每个用户不一样）")
    Teach.say("和具体实例无关的工具、工厂、常量 → object")

    Teach.summary(
      "object 是单例，也是放「静态」成员的地方",
      "伴生对象的 apply 让你写成 User(...) 而不写 new",
      "构造校验优先放伴生对象，构造器可以 private"
    )
    Teach.practice("给 Rectangle 写伴生对象 apply，再加一个 square(side) 专门造正方形。")
  }
}
