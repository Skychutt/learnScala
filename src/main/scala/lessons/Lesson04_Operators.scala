package lessons

/** 第 04 课：算术、比较、逻辑。在 Scala 里运算符就是方法。 */
object Lesson04_Operators {

  def run(): Unit = {
    Teach.why("计算和判断每天都要写。Scala 的特别之处是：3 + 2 其实是在调用 3 的名为 + 的方法。所以你以后也会见到 list :+ 3 这种「看起来像运算符」的方法。")

    Teach.section("1. 算术")
    println(s"  3 + 2 = ${3 + 2}    3 - 2 = ${3 - 2}    3 * 2 = ${3 * 2}")
    println(s"  7 / 2 = ${7 / 2}      ← 两个 Int 相除，小数被丢掉，结果仍是 Int")
    println(s"  7 / 2.0 = ${7 / 2.0}  ← 有一边是 Double，结果就是小数")
    println(s"  7 % 2 = ${7 % 2}      ← 余数")
    Teach.pitfall("整数除法是截断不是四舍五入。要平均值、要比例，先把其中一个写成小数。")

    Teach.section("2. 复合赋值。Scala 没有 ++")
    var n = 10
    n += 5
    n -= 2
    println(s"  10 先 +5 再 -2，n = $n")
    Teach.vsJava("没有 ++i / i++。请写 i += 1。")

    Teach.section("3. 比较。== 比的是值")
    println(s"  3 > 2 = ${3 > 2}   3 == 3 = ${3 == 3}   3 != 2 = ${3 != 2}")
    println(s"  List(1,2) == List(1,2) = ${List(1, 2) == List(1, 2)}")
    Teach.say("eq 才比较是不是内存里同一个对象。日常判断相等用 == 即可。")
    Teach.vsJava("Java 的 == 对对象比引用，字符串要用 equals。Scala 的 == 会走 equals，更符合直觉。")

    Teach.section("4. 逻辑与短路")
    def probe(label: String, value: Boolean): Boolean = {
      println(s"    正在计算 $label")
      value
    }
    println("  true || 右边：")
    val orResult = probe("左边 true", true) || probe("右边（不该出现）", false)
    println(s"  结果 $orResult")
    println("  false && 右边：")
    val andResult = probe("左边 false", false) && probe("右边（不该出现）", true)
    println(s"  结果 $andResult")
    Teach.say("|| 左边为真就不再算右边；&& 左边为假就不再算右边。叫短路。")
    Teach.say("这很有用：xs.nonEmpty && xs.head > 0  不会在空列表上取 head。")

    Teach.section("5. 运算符 = 方法")
    println(s"  3.+(2) = ${3.+(2)}")
    println(s"  List(1, 2) :+ 3 = ${List(1, 2) :+ 3}")
    println(s"  0 :: List(1, 2) = ${0 :: List(1, 2)}   :: 是往 List 头部加")

    Teach.summary(
      "整数除法截断；要小数就让一边变成 Double",
      "没有 ++ / --；== 比较值，eq 比较引用",
      "&& 和 || 会短路；看起来像运算符的符号，很多其实是方法"
    )
    Teach.practice("判断 2026 是不是闰年：能被 4 整除，并且（不能被 100 整除 或 能被 400 整除）。")
  }
}
