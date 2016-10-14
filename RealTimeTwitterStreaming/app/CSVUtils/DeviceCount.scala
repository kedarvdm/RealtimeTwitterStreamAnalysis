package CSVUtils

import java.io.PrintWriter

/**
  * Created by rishikaidnani on 12/3/15.
  */
class DeviceCount() {
  var country = ""
  var android = 0
  var ios = 0
  var web = 0
  var others = 0

  def writeCSV(writer: PrintWriter): Unit = {

    val str = country + "," + android + "," + ios + "," + web + "," + others
    writer.write(str)
    writer.println()
  }
}