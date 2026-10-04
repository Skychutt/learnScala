package lessons

/** 第 16 课：case class 与 Scala 3 enum。建模数据时优先用它们。 */
object Lesson16_CaseClassEnum {

  case class Book(title: String, price: Double, authors: List[String] = Nil)

  enum Color {
    case Red, Green, Blue
  }

  enum TrafficLight {
    case Go, Wait, Stop
    def next: TrafficLight = this match {
      case Go   => Wait
      case Wait => Stop
      case Stop => Go
    }
  }

  enum Shape {
    case Circle(r: Double)
    case Rectangle(w: Double, h: Double)
    def area: Double = this match {
      case Circle(r)       => math.Pi * r * r
      case Rectangle(w, h) => w * h
    }
  }

  def run(): Unit = {
    Teach.why("大部分类其实只是「一包数据」。case class 自动给你 apply、equals、toString、copy、模式匹配。有限几种可能（红绿蓝、交通灯）用 enum。小项目 BookShelf 的 Book 就是 case class。")

    Teach.section("1. case class 自动得到什么")
    val b1 = Book("Scala 入门", 59.0, List("张三"))
    val b2 = b1.copy(price = 49.0)
    println(s"    b1 = $b1")
    println(s"    b2 = $b2   ← copy 只改价格，其它照旧")
    println(s"    内容相同就 == ？ ${b1 == Book("Scala 入门", 59.0, List("张三"))}")
    println(s"    字段直接访问：${b1.title} / ${b1.price}")
    Teach.say("普通 class 比的是引用；case class 比的是字段值。")
    Teach.say("不用 new，Book(...) 就是伴生对象的 apply。")
    Teach.pitfall("case class 默认所有参数都是 val。不要把可变的大对象随手放进去还指望 copy 是深拷贝。")

    Teach.section("2. 简单枚举")
    val red = Color.Red
    println(s"    $red，序号 ${red.ordinal}，全部：${Color.values.mkString(", ")}")

    Teach.section("3. 枚举可以有方法")
    var light = TrafficLight.Go
    print("    红绿灯：")
    for (_ <- 1 to 4) {
      print(s"$light -> ")
      light = light.next
    }
    println(light)

    Teach.section("4. 枚举的 case 还可以带数据")
    val shapes = List(Shape.Circle(2), Shape.Rectangle(3, 4))
    shapes.foreach(s => println(f"    $s 面积 = ${s.area}%.2f"))
    Teach.say("这比 sealed abstract class + 一堆子类更短，意思一样。")

    Teach.summary(
      "装数据优先 case class：自动有 copy / equals / toString / 模式匹配",
      "copy(字段 = 新值) 得到改过一个字段的新对象",
      "有限几种可能用 enum；需要带数据就写成 enum 的 case 参数"
    )
    Teach.practice("case class Student(name, score)，copy 把分数改成 100；再写 enum Grade { case A,B,C }。")
  }
}
