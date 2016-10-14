package HBaseUtils

import TweetUtils.{TwitterDataColumnFamilies, TwitterUserColumnFamily}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs._
import org.apache.hadoop.hbase.client.{Result, HBaseAdmin, HTable}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.{HBaseConfiguration, HColumnDescriptor, HTableDescriptor}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD


/**
  * Created by kedarvdm on 11/18/15.
  */
object HBaseUtils {

  def getHBaseConfiguration: Configuration = {
    val hbaseConf = HBaseConfiguration.create()
    hbaseConf.addResource(new Path("/usr/local/bin/hbase-1.1.2/conf/hbase-site.xml"))
    hbaseConf
  }

  def getHBaseConfigurationWithTable(tableName: String): Configuration = {
    val hbaseConf = HBaseConfiguration.create()
    hbaseConf.addResource(new Path("/usr/local/bin/hbase-1.1.2/conf/hbase-site.xml"))
    hbaseConf.set(TableInputFormat.INPUT_TABLE, tableName)
    hbaseConf
  }

  def getHBaseTable(tableName:String): HTable ={
    val hTable= new HTable(getHBaseConfiguration, tableName)
    hTable
  }

  def getTweetDataRDD(sparkContext: SparkContext): RDD[(ImmutableBytesWritable, Result)] =
  {
    val hbaseConf= getHBaseConfigurationWithTable("tweetdata")
    hbaseConf.set(TableInputFormat.INPUT_TABLE, "tweetdata")
    val hBaseRDD = sparkContext.newAPIHadoopRDD(
      hbaseConf,
      classOf[TableInputFormat],
      classOf[ImmutableBytesWritable],
      classOf[Result]
    )
    hBaseRDD
  }

  def getTwitterUserRDD(sparkContext: SparkContext): RDD[(ImmutableBytesWritable, Result)] =
  {
    val hbaseConf= getHBaseConfigurationWithTable("twitteruser")
    hbaseConf.set(TableInputFormat.INPUT_TABLE, "twitteruser")
    val hBaseRDD = sparkContext.newAPIHadoopRDD(
      hbaseConf,
      classOf[TableInputFormat],
      classOf[ImmutableBytesWritable],
      classOf[Result]
    )
    hBaseRDD
  }
}
