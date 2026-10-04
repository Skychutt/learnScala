package workshheet

class Time(val hours: Int, val minutes: Int, val seconds: Int) { 
  
  private def toSecond: Int = hours*3600 + minutes*60 + seconds
  
  def other(other: Time): Int = {
    math.abs(this.toSecond - other.toSecond)
  }
  
  
}
