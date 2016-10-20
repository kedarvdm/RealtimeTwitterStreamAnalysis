/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MapReduce;

import CSVUtils.CSVUtils;
import Helper.KeyValueTuple;
import Utils.TweetUtils;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 *
 * @author rishikaidnani
 */
public class SentimentsPerTimezone {

    private static class MapClass extends TableMapper<Text, LongWritable> {

        private final static LongWritable one = new LongWritable(1);
        private KeyValueTuple kvt = new KeyValueTuple();

        private static Configuration config = HBaseConfiguration.create();
        private static HTable table;

        static {
            try {
                table = new HTable(config, "tweetdata");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void map(ImmutableBytesWritable rowKey, Result result, Mapper.Context context) throws UnsupportedEncodingException, IOException, InterruptedException {
            boolean isQualifierTweetId = true;
            String tweetid;
            String temp;
            //get column qualifier for tweet column family, give that qualifier to get command and get the sentiment
            for (KeyValue kv : result.raw()) {
                isQualifierTweetId = true;
                try {
                    tweetid = Bytes.toStringBinary(kv.getQualifier());
                    temp = tweetid;
                    Long.parseLong(temp);
                } catch (NumberFormatException e) {
                    isQualifierTweetId = false;
                }
                if (isQualifierTweetId) {
                    tweetid = Bytes.toStringBinary(kv.getQualifier());
                } else {
                    continue;
                }
                Get g = new Get(Bytes.toBytes(tweetid));
                Result resultOfTweetDataTable = table.get(g);
                String sentiment = Bytes.toStringBinary(resultOfTweetDataTable.getValue(Bytes.toBytes("tweet"), Bytes.toBytes("sentiment")));
                if (!(sentiment.equals("null"))) {
                    if (!(sentiment.trim().equals(""))) {
                        String timeZone = Bytes.toStringBinary(result.getValue(Bytes.toBytes("geo"), Bytes.toBytes("timezone")));
                        String tweetsAndTimezone = timeZone + "-" + sentiment;
                        if (!(timeZone.equals("null"))) {
                            context.write(new Text(tweetsAndTimezone), one);
                        }
                    }
                }
            }
        }
    }

    private static class ReducerClass extends Reducer<Text, LongWritable, Text, LongWritable> {

        private SortedSet<KeyValueTuple> tweetsTimezone = new TreeSet<>();

        public void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
            long total = 0;
            for (LongWritable value : values) {
                total += value.get();
            }
            context.write(key, new LongWritable(total));
            KeyValueTuple kvt = new KeyValueTuple();
            kvt.setKey(key.toString());
            kvt.setValue(total);
            tweetsTimezone.add(kvt);
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            int i = 0;
            //String filename = TweetUtils.OUTPUT_PREFIX + "SentimentsPerTimezone/sentimentTimezone.csv";
            String filename1 = TweetUtils.OUTPUT_PREFIX + "SentimentsPerTimezone/sentimentTimezoneDashboard.js";
            ArrayList<KeyValueTuple> timezones = new ArrayList<>();
            for (KeyValueTuple kvt : tweetsTimezone) {
                if(i==18) break;
                timezones.add(kvt);
                i++;
            }
            //CSVUtils.createCSVForSentimentTimezone(timezones, "Timezone, Number Of Positive, Number of Neutral, Number Of Negative", filename);
            CSVUtils.createCSVForSentimentTimezoneDashboard(timezones,filename1);
        }
    }

    public static void main(String args[]) throws IOException, InterruptedException, ClassNotFoundException {
        Configuration conf = new HBaseConfiguration();

        conf.addResource(TweetUtils.HBASE_CONF);
        Job job = Job.getInstance(conf, "Device count per country");
        job.setJarByClass(SentimentsPerTimezone.class);

        Scan sc = new Scan();
        sc.setCaching(500);
        sc.setCacheBlocks(false);

        TableMapReduceUtil.initTableMapperJob("twitteruser", // input table
                sc, // Scan instance to control CF and attribute selection
                SentimentsPerTimezone.MapClass.class, // mapper class
                Text.class, // mapper output key
                LongWritable.class, // mapper output value
                job);

        job.setMapperClass(SentimentsPerTimezone.MapClass.class);
        job.setReducerClass(SentimentsPerTimezone.ReducerClass.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);
        String dest = TweetUtils.OUTPUT_PREFIX + "SentimentsPerTimezone";
        if (args.length > 0) {
            dest = args[0];
        }
        File destination = new File(dest);
        FileUtil.fullyDelete(destination);
        FileOutputFormat.setOutputPath(job, new Path(dest));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
