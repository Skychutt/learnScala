package lessons

/** 第 08 课：用 def 定义方法。和「函数值」不一样，下一课会对比。 */
object Lesson08_Methods {

  def run(): Unit = {
    Teach.why("把一段逻辑起个名字，才能复用、才能测。def 是方法：每次调用都会执行方法体。老师讲义里的「named function」更接近下一课的 val f = (x) => ...，两边都要会。")

    Teach.section("1. 最简形式")
    def add(a: Int, b: Int): Int = a + b
    println(s"  add(2, 3) = ${add(2, 3)}")
    Teach.say("def 名字(参数: 类型): 返回类型 = 方法体")
    Teach.say("方法体是表达式。单行可以不加大括号；多行用 {}，返回值仍是最后一行。")
    Teach.say("Scala 一般不写 return。写了也可以，但不是习惯用法。")

    Teach.section("2. 只做事、不返回有用值：Unit")
    def greet(name: String): Unit = println(s"  你好，$name")
    greet("小明")
    Teach.vsJava("类似 void 方法。")

    Teach.section("3. 默认参数和命名参数")
    def describe(name: String, age: Int = 18, city: String = "北京"): String =
      s"$name，$age 岁，住在 $city"
    println("  " + describe("李雷"))
    println("  " + describe("韩梅梅", 20))
    println("  " + describe("Jim", city = "伦敦"))
    Teach.say("默认参数让调用变短；命名参数可以跳过中间的参数，也可以换顺序。")

    Teach.section("4. 可变参数")
    def sumAll(nums: Int*): Int = nums.sum
    println(s"  sumAll(1,2,3,4) = ${sumAll(1, 2, 3, 4)}")
    println(s"  把 List 展开：${sumAll(List(10, 20, 30)*)}")
    Teach.say("Type* 表示「0 个或多个」。传入已有集合时，Scala 3 写成 xs* 。")

    Teach.section("5. 方法里再定义方法")
    def factorial(n: Int): Int = {
      def loop(k: Int, acc: Int): Int =
        if (k <= 1) acc else loop(k - 1, acc * k)
      loop(n, 1)
    }
    println(s"  5! = ${factorial(5)}")
    Teach.say("内部方法外面看不见，适合放只为外层服务的辅助逻辑。")

    Teach.section("6. 返回类型能不能省略？")
    def isEven(n: Int) = n % 2 == 0
    println(s"  isEven(4) = ${isEven(4)}")
    Teach.say("能推断就可以不写。递归方法、对外公开的 API，建议写清楚返回类型。")
    Teach.pitfall("递归方法如果不写返回类型，编译器有时会报错。factorial 这种请写 : Int。")

    Teach.summary(
      "def 名字(参数: 类型): 返回类型 = 方法体，最后一行就是返回值",
      "默认参数 + 命名参数能让调用短而清晰",
      "可变参数用 Type*；展开集合用 xs*"
    )
    Teach.practice("写 max3(a, b, c): Int 返回三个数里最大的；再给 city 设默认值写一个自我介绍方法。")
  }
}
