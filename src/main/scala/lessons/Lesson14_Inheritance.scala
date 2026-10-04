package lessons

/** 第 14 课：继承、抽象类、override、sealed。 */
object Lesson14_Inheritance {

  abstract class Animal(val name: String) {
    def speak(): String
    def intro(): String = s"我是 $name"
  }

  class Dog(name: String) extends Animal(name) {
    override def speak(): String = "汪汪"
    override def intro(): String = super.intro() + "，是一只狗"
  }

  final class Cat(name: String) extends Animal(name) {
    override def speak(): String = "喵"
  }

  sealed abstract class Shape
  class Circle(val r: Double) extends Shape
  class Square(val side: Double) extends Shape

  def area(shape: Shape): Double = shape match {
    case c: Circle => math.Pi * c.r * c.r
    case s: Square => s.side * s.side
  }

  def run(): Unit = {
    Teach.why("很多类型有公共部分，又有各自不同的行为。父类抽公共，子类写差异。sealed 让编译器帮你检查 match 有没有漏分支。")

    Teach.section("1. 抽象类不能直接 new")
    Teach.say("abstract class 里可以有抽象方法（没方法体），也可以有已经实现的方法。")
    Teach.say("子类用 extends 父类(传给父构造器的参数)。")

    Teach.section("2. override 和 super")
    val dog: Animal = Dog("旺财")
    val cat: Animal = Cat("小花")
    println(s"    ${dog.intro()}，叫声：${dog.speak()}")
    println(s"    ${cat.intro()}，叫声：${cat.speak()}")
    Teach.say("覆盖必须写 override。调用父类那一版用 super。")
    Teach.say("final class 不能再被继承。Cat 再被 extends 会编译失败。")
    Teach.say("父类类型可以指向子类对象：val dog: Animal = Dog(...)  这叫多态。")

    Teach.section("3. sealed：子类必须写在同一文件")
    println(f"    半径 2 的圆面积 ≈ ${area(Circle(2))}%.2f")
    println(s"    边长 3 的正方形面积 = ${area(Square(3))}")
    Teach.say("sealed 的好处：area 里的 match 如果漏了某种 Shape，编译器会警告。")
    Teach.say("一组「就这几种可能」的类型，用 sealed（或下一课的 enum）最合适。")

    Teach.summary(
      "extends 父类(父构造器参数)；覆盖写 override；调用父类用 super",
      "抽象类不能 new；final 类不能再继承",
      "sealed 把子类锁在同一文件，match 更安全"
    )
    Teach.practice("抽象类 Employee(name)，子类 Manager 和 Engineer 各自实现 paycheck: Int。")
  }
}
