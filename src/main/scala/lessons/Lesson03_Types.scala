package lessons

/** 第 03 课：基本类型、显式转换、Scala 类型层级。 */
object Lesson03_Types {

  def run(): Unit = {
    Teach.why("Scala 是静态强类型：编译期就知道每个值是什么类型，并且不会悄悄把一种类型当成另一种。先把常用类型记熟，后面看报错才不慌。")

    Teach.section("1. 最常用的几种")
    val i: Int = 42
    val l: Long = 42L
    val d: Double = 3.14
    val f: Float = 3.14f
    val ok: Boolean = true
    val c: Char = 'A'
    val s: String = "ABC"
    val u: Unit = ()
    println(s"  Int=$i  Long=$l  Double=$d  Float=$f")
    println(s"  Boolean=$ok  Char=$c  String=$s  Unit=$u")
    Teach.say("整数默认是 Int，小数默认是 Double。Long 加 L，Float 加 f。")
    Teach.say("Char 用单引号 'A'，String 用双引号 \"A\"。写反了类型就错。")
    Teach.say("Unit 表示「没有有意义的返回值」，只有一个值 ()。类似 Java 的 void，但它是真正的类型。")

    Teach.section("2. 转换必须自己写")
    val n = 100
    println(s"  100.toLong = ${n.toLong}  100.toDouble = ${n.toDouble}  100.toString = ${n.toString}")
    println(s"  \"123\".toInt = ${"123".toInt}")
    println(s"  Int 范围：${Int.MinValue} ~ ${Int.MaxValue}")
    Teach.pitfall("\"3.14\".toInt 会抛异常。先 toDouble，或用 \"123\".toIntOption 得到 Option[Int]。")
    Teach.say(s"  \"3.14\".toDouble.toInt = ${"3.14".toDouble.toInt}  （先变小数再截成整数）")
    Teach.say(s"  \"abc\".toIntOption = ${"abc".toIntOption}  （失败是 None，不会炸）")

    Teach.section("3. 类型层级（先记住这张图）")
    Teach.say("Any 是所有类型的顶。")
    Teach.say("  AnyVal：值类型 Int Double Boolean Char Unit ...")
    Teach.say("  AnyRef：所有 class，约等于 Java 的 Object")
    Teach.say("Null 是所有 AnyRef 的子类型（对应 Java null，日常别用）。")
    Teach.say("Nothing 是所有类型的子类型，常出现在 throw 的类型上。")
    val anyVal: AnyVal = 1
    val anyRef: AnyRef = "hi"
    val any: Any = 1
    println(s"  AnyVal=$anyVal  AnyRef=$anyRef  Any=$any")
    Teach.vsJava("Java 有 int 和 Integer 两套。Scala 的 Int 既像基本类型又像对象，可以写 1.toString。")

    Teach.summary(
      "整数 Int，小数 Double，真假 Boolean，字符 Char，文本 String，无返回值 Unit",
      "类型转换用 toInt / toDouble / toString；可能失败时用 toIntOption",
      "Any / AnyVal / AnyRef / Null / Nothing 是整棵类型树"
    )
    Teach.practice("把 \"3.14\" 转 Double 再转 Int；再用 \"hello\".toIntOption 看结果。")
  }
}
