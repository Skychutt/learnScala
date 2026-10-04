package workshheet

class TimePeriod(totalSeconds: Int) {
  // 内部直接拆分总秒数
  private val hours = totalSeconds / 3600
  private val remain1 = totalSeconds % 3600
  private val minutes = remain1 / 60
  private val seconds = remain1 % 60

  def getTimeString: String = {
    s"$hours hours, $minutes minutes, $seconds seconds"
  }
}
