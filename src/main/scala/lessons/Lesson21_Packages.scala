package lessons

import java.time.LocalDate
import scala.collection.mutable.{Map as MutMap, ArrayBuffer}
import scala.math.{Pi, sqrt}

/** 第 21 课：package 与 import。对应老师讲义 Chapter 1 最后一节。 */
object Lesson21_Packages {

  def run(): Unit = {
    Teach.why("代码一多就要分文件、分文件夹。package 决定代码住在哪，import 决定你能直接叫谁的名字。")

    Teach.section("1. package 是什么")
    Teach.say("本文件第一行是 package lessons，所以这个 object 的全名是 lessons.Lesson21_Packages")
    Teach.say("文件夹路径要和包名对应：src/main/scala/lessons/....scala")
    Teach.say("package 必须写在文件最上面（注释可以在它前面）。")
    Teach.vsJava("和 Java 几乎一样，只是行尾不需要分号。")

    Teach.section("2. 不 import 也能用：写全名")
    val today = java.time.LocalDate.now()
    Teach.say(s"java.time.LocalDate.now() = $today  （一次两次可以，写多了就烦）")

    Teach.section("3. import 一个类型")
    Teach.say(s"上面已经 import java.time.LocalDate，所以可以直接 LocalDate.now() = ${LocalDate.now()}")

    Teach.section("4. 只 import 某一个方法 / 值")
    Teach.say("import scala.math.{Pi, sqrt} 只引进两个名字，不会把 math 里全部东西倒进来。")
    Teach.say(f"Pi ≈ $Pi%.5f，sqrt(9) = ${sqrt(9)}")

    Teach.section("5. import 一整包：package.*")
    Teach.say("import scala.io.StdIn.*  会把 StdIn 里的 readLine、readInt 等都引进来。")
    Teach.say("方便，但名字冲突时更难查。能写清楚就写清楚。")

    Teach.section("6. 一次 import 多项，以及改名 as")
    val buf = ArrayBuffer(1, 2, 3)
    buf += 4
    val table: MutMap[String, Int] = MutMap("Alice" -> 90)
    Teach.say(s"ArrayBuffer = $buf")
    Teach.say(s"Map as MutMap，避免和不可变 Map 重名： $table")
    Teach.say("写法：import scala.collection.mutable.{Map as MutMap, ArrayBuffer}")

    Teach.section("7. import 可以写在代码中间")
    def showRandom(): String = {
      import scala.util.Random
      s"0 到 9 的随机数：${Random.nextInt(10)}"
    }
    Teach.say(showRandom())
    Teach.say("语法允许写在函数里，但常规做法仍是放文件顶部，别人打开就能看到依赖。")

    Teach.section("8. 查文档")
    Teach.say("官方 API：https://scala-lang.org/api/3.x/")
    Teach.say("不知道某个方法时，先看 API，再看 IDE 自动补全。")

    Teach.pitfall("import 错包是新手常见问题：用了可变 Map 却 import 了不可变的，或反过来。")
    Teach.pitfall("两个包里都有同名类时，用 as 改名，或干脆写全名。")

    Teach.summary(
      "package 名 = 文件夹路径，且是文件第一条有效语句",
      "import x.y.Z 引进类型；import x.y.Z.method 引进单个方法",
      "import x.y.* 全引进；import x.y.{A, B} 引进多个；as 用来改名"
    )
    Teach.practice("写一个小文件：package demo.hello，object Greet，里面用 import scala.io.StdIn.readLine 读名字并打招呼。")
  }
}
