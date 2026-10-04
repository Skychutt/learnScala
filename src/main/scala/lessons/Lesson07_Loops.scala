package lessons

/** 第 07 课：while 和 for。while 结果永远是 Unit；要新集合用 yield。 */
object Lesson07_Loops {

  def run(): Unit = {
    Teach.why("重复做事用循环。Scala 能写 while，但日常处理列表时更常用 for 和 map/filter。老师讲义把 while 放在条件表达式一章，并特别说明：while 的值永远是 Unit。")

    Teach.section("1. while：条件为真就重复")
    var i = 1
    print("  while: ")
    while (i <= 3) {
      print(s"$i ")
      i += 1
    }
    println()
    Teach.say("条件必须是 Boolean。循环体里通常要改那个条件相关的 var，否则会无限循环。")
    Teach.say("Scala 3 也可以写成：while i <= 3 do ...")
    Teach.pitfall("while 的结果是 Unit，不能 val xs = while (...) ... 来收集数据。要收集就用下面的 for yield。")

    Teach.section("2. to / until / by")
    Teach.say("1 to 5     包含 5：1,2,3,4,5")
    Teach.say("1 until 5  不含 5：1,2,3,4")
    Teach.say("1 to 9 by 2  步长 2：1,3,5,7,9")
    Teach.say("5 to 1 by -1 倒序：5,4,3,2,1")
    print("  to: "); for (k <- 1 to 5) print(s"$k "); println()
    print("  until: "); for (k <- 1 until 5) print(s"$k "); println()
    print("  by 2: "); for (k <- 1 to 9 by 2) print(s"$k "); println()
    print("  倒序: "); for (k <- 5 to 1 by -1) print(s"$k "); println()

    Teach.section("3. 守卫：for 里直接过滤")
    print("  1 到 6 的偶数: ")
    for (k <- 1 to 6 if k % 2 == 0) print(s"$k ")
    println()

    Teach.section("4. 多个生成器 = 嵌套循环")
    println("  小九九前三行：")
    for (row <- 1 to 3; col <- 1 to row) {
      print(s"  $col*$row=${col * row}\t")
      if (col == row) println()
    }

    Teach.section("5. yield：从「做事」变成「产出新集合」")
    val squares = for (k <- 1 to 5) yield k * k
    println(s"  1 到 5 的平方：$squares")
    Teach.say("for (...) yield e  等价于  (范围).map(k => e)")
    Teach.say("遍历用 for；要得到新列表用 yield 或 map。")

    Teach.section("6. 和 while 怎么选")
    Teach.say("已知要走遍一个集合、一个范围 → for / foreach / map")
    Teach.say("不知道转几圈、靠条件停 → while（读文件直到 null 就是这种，见第 22 课）")

    Teach.summary(
      "to 含尾，until 不含尾，by 控制步长",
      "for 可写多个生成器和 if 守卫；要新集合就 yield",
      "while 的值永远是 Unit，不要用它来「算出一个列表」"
    )
    Teach.practice("用 for yield 得到 1 到 20 里能被 3 整除的数；再用 while 打印 1 到 5。")
  }
}
