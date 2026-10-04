package lessons

/** 第 05 课：字符串。对应老师讲义 Chapter 2 前半：字面量、方法、s/f/raw 插值。 */
object Lesson05_Strings {

  def run(): Unit = {
    Teach.why("文本处理是作业和考试里最常见的一类题：拼接、截取、格式化、多行。老师第 2 讲几乎整章都在讲 String。")

    val lang = "Scala"
    val year = 2004

    Teach.section("1. Char 和 String")
    Teach.say("Char 是单个字符，单引号：val c: Char = 'A'")
    Teach.say("String 是字符序列，双引号：val s: String = \"Hello\"")
    Teach.say("String 不可变：s.toUpperCase 不会改原来的 s，而是得到一个新字符串。")

    Teach.section("2. 拼接：+ 能用，日常更推荐插值")
    println("  用 + 拼接：" + lang + " 诞生于 " + year)
    println(s"  s 插值： $lang 诞生于 $year，距今 ${2026 - year} 年")
    Teach.say("$变量 直接嵌入；${表达式} 要加大括号。只写 $2026-year 不会算减法。")

    Teach.section("3. f 插值：按宽度和小数位格式化")
    val pi = 3.1415926
    val value = 123
    println(f"  Pi 两位小数：$pi%.2f")
    println(f"  整数：$value%d    占 5 格右对齐：[$value%5d]  左对齐：[$value%-5d]")
    Teach.say("常用格式：%d 整数，%f 小数，%s 字符串，%b 布尔，%c 字符，%x 十六进制。")
    Teach.say("%.2f 表示小数点后 2 位；%5d 表示至少占 5 个字符宽。")
    Teach.vsJava("和 String.format / System.out.printf 是同一套 Formatter。")

    Teach.section("4. raw 插值：不要转义")
    println(s"  普通字符串 \\n 会换行：第一行\n  第二行")
    println(raw"  raw 里 \n 就是两个字符：\n 不会换行")
    Teach.say("路径、正则、LaTeX 这种满是反斜杠的文本，用 raw 不用写 \\\\。")
    println(raw"  例如 LaTeX：\section{Title}\label{sec:title}")

    Teach.section("5. 多行字符串")
    val poem =
      """Scala 很简洁
        |也能和 Java 互操作
        |还擅长写并发
        |""".stripMargin
    print(poem.split("\n").map("  " + _).mkString("\n"))
    println()
    Teach.say("三个双引号包起来。stripMargin 会按每行的 | 对齐，把 | 左边的缩进去掉。")

    Teach.section("6. 常用方法")
    val text = "  Hello Scala  "
    println(s"  原串          [$text]")
    println(s"  trim          [${text.trim}]")
    println(s"  length        ${text.length}")
    println(s"  toUpperCase   ${text.toUpperCase}")
    println(s"  contains      ${text.contains("Scala")}")
    println(s"  startsWith He ${text.trim.startsWith("He")}")
    println(s"  substring(0,5) ${text.trim.substring(0, 5)}")
    println(s"  split         ${text.trim.split(" ").mkString("[", ", ", "]")}")
    println(s"  replace       ${"a-b-c".replace("-", "/")}")
    println(s"  * 重复        ${"ha" * 3}")
    println(s"  reverse       ${"Scala".reverse}")
    Teach.pitfall("substring 的结尾下标不包含；越界会抛 StringIndexOutOfBoundsException。")
    Teach.pitfall("split 的参数是正则。按点切分要 split(\"\\\\.\") 或 split(raw\"\\.\")，不能 split(\".\")。")

    Teach.summary(
      "日常拼接用 s\"$name\"；要对齐数字用 f\"$n%5d\" / f\"$x%.2f\"",
      "反斜杠很多时用 raw；多行用 \"\"\" + stripMargin",
      "String 不可变，所有「修改」都返回新串"
    )
    Teach.practice("用 f 插值打印购物小票：商品名左对齐 10 格，价格保留两位小数。")
  }
}
