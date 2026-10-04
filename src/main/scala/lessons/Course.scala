package lessons

/** 课程目录。 */
object Course {

  private val catalog: List[(Int, String, () => Unit)] = List(
    (1,  "起步：入口、注释、输出、表达式", Lesson01_Hello.run),
    (2,  "变量与常量 val / var / lazy", Lesson02_Variables.run),
    (3,  "基本数据类型与类型层级", Lesson03_Types.run),
    (4,  "运算符", Lesson04_Operators.run),
    (5,  "字符串与插值", Lesson05_Strings.run),
    (6,  "条件表达式 if / else", Lesson06_IfElse.run),
    (7,  "循环 while / for", Lesson07_Loops.run),
    (8,  "方法 def", Lesson08_Methods.run),
    (9,  "函数、匿名函数与高阶函数", Lesson09_Functions.run),
    (10, "元组与集合", Lesson10_Collections.run),
    (11, "集合常用操作", Lesson11_CollectionOps.run),
    (12, "类与构造器", Lesson12_Classes.run),
    (13, "单例对象与伴生对象", Lesson13_Objects.run),
    (14, "继承与抽象类", Lesson14_Inheritance.run),
    (15, "特质 Trait", Lesson15_Traits.run),
    (16, "Case Class 与枚举", Lesson16_CaseClassEnum.run),
    (17, "模式匹配", Lesson17_PatternMatching.run),
    (18, "Option / Either / Try 与异常", Lesson18_OptionAndError.run),
    (19, "泛型与 for 推导式", Lesson19_GenericsAndFor.run),
    (20, "Scala 3 常用新语法", Lesson20_Scala3.run),
    (21, "包与 import", Lesson21_Packages.run),
    (22, "控制台与文件 IO", Lesson22_IO.run)
  )

  def start(args: Seq[String]): Unit = {
    if (args.isEmpty) {
      printMenu()
      println()
      println("未指定课号，将按顺序运行全部课程。")
      println("只看一课：sbt \"run 3\"    小项目：sbt \"run preview\"")
      println()
      runAll()
    } else {
      val ids = args.flatMap(parseId)
      if (ids.isEmpty) {
        println("课号无效。请输入 1 到 22 的整数，例如：sbt \"run 1\"")
        printMenu()
      } else {
        ids.foreach(runOne)
      }
    }
  }

  def printMenu(): Unit = {
    println("=" * 54)
    println("  Scala 基础语法课程（共 22 课）")
    println("=" * 54)
    catalog.foreach { case (id, title, _) =>
      println(f"  第$id%02d课  $title")
    }
    println("=" * 54)
  }

  private def runAll(): Unit = catalog.foreach { case (id, _, _) => runOne(id) }

  private def runOne(id: Int): Unit = {
    catalog.find(_._1 == id) match {
      case Some((_, title, run)) =>
        println()
        println("#" * 56)
        println(s"#  第${f"$id%02d"}课  $title")
        println("#" * 56)
        run()
      case None =>
        println(s"没有第 $id 课，有效范围是 1 到 ${catalog.size}。")
    }
  }

  private def parseId(raw: String): Option[Int] =
    raw.toIntOption.filter(id => catalog.exists(_._1 == id))
}
