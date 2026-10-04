package lessons

/** 第 06 课：if / else。它是表达式；没有 else 时结果是 Unit。 */
object Lesson06_IfElse {

  def run(): Unit = {
    Teach.why("程序要做判断。Scala 没有三元运算符 ?: ，因为 if 本身就能算出值，赋给变量。老师讲义还强调：两种写法（花括号 / then）是一个意思。")

    val score = 86
    val x = 20

    Teach.section("1. 当作「去做某事」来写")
    if (score >= 60) println("  及格了") else println("  不及格")

    Teach.section("2. 更地道：当作表达式，算出一个值")
    val level =
      if (score >= 90) "优"
      else if (score >= 80) "良"
      else if (score >= 60) "及格"
      else "不及格"
    println(s"  分数 $score → 等级 $level")
    Teach.say("命中哪一支，那一支最后一行就是整个 if 的值。")

    Teach.section("3. 两种语法，效果相同")
    val a = if (x > 34) "big" else if (x > 10) "mid" else "small"
    val b =
      if x > 34 then "big"
      else if x > 10 then "mid"
      else "small"
    println(s"  花括号/圆括号风格：$a")
    println(s"  Scala 3 的 then 风格：$b")
    Teach.say("本课程两种都认。作业里选一种写到底，不要混着花。")

    Teach.section("4. 没有 else 会怎样？")
    Teach.say("如果写成：val s: String = if (x > 34) \"big\"")
    Teach.say("会编译失败。缺 else 时，Scala 把整个 if 的类型定成 Unit，不能当成 String 用。")
    Teach.pitfall("想拿到一个真正的值，必须把 else 也写上，保证每一条路都有结果。")
    val onlyIf: Unit = if (x > 34) ()
    println(s"  没有 else 的 if，类型是 Unit，值是 $onlyIf")

    Teach.section("5. 两支类型不一样")
    val mixed: String | Int = if (x > 34) "big" else 5
    println(s"  if 一支 String 一支 Int，类型变成 String | Int，当前值 = $mixed")
    Teach.say("这叫联合类型，第 20 课还会见到。初学时尽量让两支返回同一种类型，代码更好用。")

    Teach.section("6. 代码块作为一支")
    val message = if (score >= 60) {
      val bonus = 5
      s"通过，额外加分 $bonus"
    } else {
      "未通过"
    }
    println(s"  $message")

    Teach.section("7. 多分支还可以用 match")
    val choice = 2
    val text = choice match {
      case 1 => "开始"
      case 2 => "停止"
      case _ => "重置"
    }
    println(s"  match 也是表达式：choice=$choice → $text")
    Teach.say("分支一多，match 通常比一长串 else if 更清晰。第 17 课展开。")

    Teach.summary(
      "if 有值，所以 val x = if (c) a else b 完全合法",
      "没有 else 的 if 结果是 Unit，不要指望拿到那支的字符串",
      "两支类型不同会变成 A | B；多分支优先考虑 match"
    )
    Teach.practice("温度 t：<0 结冰，0 到 20 凉爽，>20 温暖。用 if 表达式赋给 val 再打印。")
  }
}
