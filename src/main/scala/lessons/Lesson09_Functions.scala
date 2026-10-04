package lessons

/** 第 09 课：函数是值。这是 Scala 的核心，老师讲义 Chapter 1 的 Functions 一节。 */
object Lesson09_Functions {

  def run(): Unit = {
    Teach.why("方法 def 是「挂在某个对象上的行为」。函数值是「可以传来传去的数据」。map、filter 要的就是函数值。两者都要会，而且要分得清。")

    Teach.section("1. 匿名函数（lambda）")
    Teach.say("语法：(参数: 类型) => 表达式")
    val plusFive = (num: Int) => num + 5
    println(s"  plusFive(44) = ${plusFive(44)}")
    Teach.say("把它赋给 val，就变成老师讲的 named function：有名字的函数值。")

    Teach.section("2. 把函数类型写出来")
    val plus: (Int, Int) => Int = (a, b) => a + b
    val square: Int => Int = (n: Int) => n * n
    println(s"  plus(3,4) = ${plus(3, 4)}    square(6) = ${square(6)}")
    Teach.say("A => B 表示一个参数； (A, B) => C 表示两个参数。箭头右边是返回类型。")

    Teach.section("3. 占位符 _")
    val double: Int => Int = _ * 2
    println(s"  (_ * 2)(7) = ${double(7)}")
    Teach.say("每个 _ 依次代表一个参数。很短的时候好用。")
    Teach.pitfall("(_ * 2 + _) 这种两个 _ 还勉强；逻辑一长就改回 (x, y) => ...，否则自己都看不懂。")

    Teach.section("4. 高阶函数：参数本身是函数")
    val calculate = (f: (Int, Int) => Int, x: Int, y: Int) => f(x, y)
    val x1 = calculate((a, b) => a * a + b * b, 3, 4)
    val y1 = calculate((a, b) => (a + b) * (a + b), 3, 4)
    val z1 = calculate(plus, 3, 4)
    println(s"  3²+4² = $x1    (3+4)² = $y1    plus(3,4) = $z1")
    Teach.say("这就是老师讲义里的例子：calculate 并不知道你要加还是乘，你把规则传进去。")

    def applyTwice(n: Int, f: Int => Int): Int = f(f(n))
    println(s"  对 3 做两次 +1 = ${applyTwice(3, _ + 1)}")
    println(s"  对 3 做两次平方 = ${applyTwice(3, square)}")

    Teach.section("5. 集合上的高阶函数")
    val nums = List(1, 2, 3, 4, 5)
    println(s"  原列表 $nums")
    println(s"  map(_ * 10)     = ${nums.map(_ * 10)}")
    println(s"  filter(_ % 2==0)= ${nums.filter(_ % 2 == 0)}")
    print("  foreach 打印：")
    nums.foreach(n => print(s" $n"))
    println()

    Teach.section("6. 闭包")
    var factor = 3
    val times = (n: Int) => n * factor
    println(s"  factor=3 → times(4)=${times(4)}")
    factor = 10
    println(s"  factor=10→ times(4)=${times(4)}  函数会「看见」外面的当前值")

    Teach.section("7. 柯里化：参数列表拆成多组")
    def multiply(a: Int)(b: Int): Int = a * b
    val triple = multiply(3)
    println(s"  multiply(3)(5) = ${multiply(3)(5)}    triple(5) = ${triple(5)}")
    Teach.say("先传一部分，得到一个还缺参数的新函数。这在后面会和 using / 隐式参数碰头。")

    Teach.section("8. def 和 val 函数差在哪")
    Teach.say("def add(a:Int,b:Int)=a+b   每次调用都执行方法体，可以有类型参数、默认参数。")
    Teach.say("val add = (a:Int,b:Int)=>a+b  这是一个对象，可以当参数传来传去。")
    Teach.say("需要传给 map 时，def 也会被自动转成函数值（eta expansion）。初学按「能跑就行」即可。")

    Teach.summary(
      "匿名函数：(x: Int) => x + 1；类型写成 Int => Int",
      "高阶函数接收或返回函数，map/filter/calculate 都是",
      "短函数可用 _ ；一复杂就写全参数名"
    )
    Teach.practice("用 filter 和 map，从 List(1,2,3,4,5,6) 取出偶数再各自加 100；再自己写一个 calculate 调用做减法。")
  }
}
