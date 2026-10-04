package lessons

/** 第 11 课：集合变换。能链式写就少写 for 循环。 */
object Lesson11_CollectionOps {

  def run(): Unit = {
    Teach.why("Scala 代码「看起来像函数式」，主要就是这些方法：map 变换、filter 过滤、flatMap 拆开再拍平、fold 汇总。写熟以后循环会少很多，bug 也会少。")

    val nums = List(1, 2, 3, 4, 5, 6)
    println(s"  原数据 $nums")

    Teach.section("1. 一对一变换、过滤、切片")
    println(s"  map 平方          ${nums.map(n => n * n)}")
    println(s"  filter >3         ${nums.filter(_ > 3)}")
    println(s"  filterNot >3      ${nums.filterNot(_ > 3)}")
    println(s"  take(3)           ${nums.take(3)}")
    println(s"  drop(3)           ${nums.drop(3)}")
    println(s"  slice(1,4)        ${nums.slice(1, 4)}")

    Teach.section("2. 提问类方法")
    println(s"  exists 有偶数？    ${nums.exists(_ % 2 == 0)}")
    println(s"  forall 都 >0？     ${nums.forall(_ > 0)}")
    println(s"  find 第一个 >4    ${nums.find(_ > 4)}")
    println(s"  count 偶数个数     ${nums.count(_ % 2 == 0)}")
    println(s"  sum/max/min       ${nums.sum}, ${nums.max}, ${nums.min}")
    println(s"  mkString          ${nums.mkString(" -> ")}")

    Teach.section("3. flatten 与 flatMap")
    val nested = List(List(1, 2), List(3, 4), List(5))
    println(s"  flatten           ${nested.flatten}")
    println(s"  flatMap 一变二    ${nums.take(3).flatMap(n => List(n, n * 10))}")
    val words = List("scala", "is", "fun")
    println(s"  拆字符 map+flatten ${words.map(_.toList).flatten}")
    println(s"  等价 flatMap       ${words.flatMap(_.toList)}")
    Teach.say("flatMap = map 之后 flatten。Option/List 的 for 推导式底层就是它。")

    Teach.section("4. reduce 与 fold")
    println(s"  reduce(_ + _)         ${nums.reduce(_ + _)}")
    println(s"  foldLeft(100)(_ + _)  ${nums.foldLeft(100)(_ + _)}")
    Teach.say("reduce：从集合里拿出第一个当起点，不能用于空列表。")
    Teach.say("foldLeft(初值)：空列表也安全，结果从初值开始攒。")
    Teach.pitfall("空 List 调 reduce 会抛异常。可能为空就用 foldLeft 或 reduceOption。")

    Teach.section("5. 排序、去重、分组、拉链")
    println(s"  sortBy 倒数       ${nums.sortBy(n => -n)}")
    println(s"  distinct          ${List(1, 1, 2, 2, 3).distinct}")
    println(s"  groupBy 奇偶      ${nums.groupBy(n => if (n % 2 == 0) "偶" else "奇")}")
    println(s"  zip 配对          ${nums.take(3).zip(List("a", "b", "c"))}")
    println(s"  zipWithIndex      ${List("A", "B", "C").zipWithIndex}")

    Teach.section("6. 链式调用")
    val result = nums.filter(_ % 2 == 0).map(_ * 10).take(2)
    println(s"  偶数 → *10 → 前两个 = $result")
    Teach.say("从左到右读：先过滤，再变换，再取前几个。每一步都返回新集合。")

    Teach.summary(
      "map 一对一；filter 过滤；flatMap 一对多再拍平",
      "空集合不要 reduce，用 foldLeft(初值)",
      "能写成 xs.filter(...).map(...) 就不要手写嵌套 for"
    )
    Teach.practice("List(\"apple\",\"pear\",\"banana\") 按长度排序，再留下长度 > 4 的，最后 mkString(\", \")。")
  }
}
