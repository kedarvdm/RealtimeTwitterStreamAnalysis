package HBaseUtils

import TweetUtils.{TwitterDataColumnFamilies, TwitterUserColumnFamily}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
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

  def getHBaseTable(tableName: String): HTable = {
    val hTable = new HTable(getHBaseConfiguration, tableName)
    hTable
  }

  def getHBaseAdmin: HBaseAdmin = {
    val admin = new HBaseAdmin(getHBaseConfiguration)
    admin
  }

  def createTwitterUser: Unit = {
    val admin = getHBaseAdmin
    val tableName = "twitteruser"
    if (!admin.isTableAvailable(tableName)) {
      print("Creating twitteruser Table")
      val tableDesc = new HTableDescriptor(tableName)
      tableDesc.addFamily(new HColumnDescriptor(TwitterUserColumnFamily.Info));
      tableDesc.addFamily(new HColumnDescriptor(TwitterUserColumnFamily.Tweet));
      tableDesc.addFamily(new HColumnDescriptor(TwitterUserColumnFamily.Stat));
      tableDesc.addFamily(new HColumnDescriptor(TwitterUserColumnFamily.Geo));
      tableDesc.addFamily(new HColumnDescriptor(TwitterUserColumnFamily.Followers));
      tableDesc.addFamily(new HColumnDescriptor(TwitterUserColumnFamily.Friends));
      admin.createTable(tableDesc)
    } else {
      print("Table twitteruser already exists!!")
    }
  }

  def createTweetData: Unit = {
    val admin = getHBaseAdmin
    val tableName = "tweetdata"
    if (!admin.isTableAvailable(tableName)) {
      print("Creating tweetdata Table")
      val tableDesc = new HTableDescriptor(tableName)
      tableDesc.addFamily(new HColumnDescriptor(TwitterDataColumnFamilies.Info));
      tableDesc.addFamily(new HColumnDescriptor(TwitterDataColumnFamilies.Tweet));
      tableDesc.addFamily(new HColumnDescriptor(TwitterDataColumnFamilies.Place));
      tableDesc.addFamily(new HColumnDescriptor(TwitterDataColumnFamilies.GeoLocation));
      admin.createTable(tableDesc)
    } else {
      print("Table tweetdata already exists!!")
    }
  }

  def getTweetDataRDD(sparkContext: SparkContext): RDD[(ImmutableBytesWritable, Result)] = {
    val hbaseConf = getHBaseConfigurationWithTable("tweetdata")
    hbaseConf.set(TableInputFormat.INPUT_TABLE, "tweetdata")
    val hBaseRDD = sparkContext.newAPIHadoopRDD(
      hbaseConf,
      classOf[TableInputFormat],
      classOf[ImmutableBytesWritable],
      classOf[Result]
    )
    hBaseRDD
  }

  def getTwitterUserRDD(sparkContext: SparkContext): RDD[(ImmutableBytesWritable, Result)] = {
    val hbaseConf = getHBaseConfigurationWithTable("twitteruser")
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
