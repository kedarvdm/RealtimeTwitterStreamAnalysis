/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MapReduce;

import Helper.KeyValueTuple;
import Utils.TweetUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
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
 * @author kedarvdm
 */
public class PopularHashtags {

    public static class MapClass extends TableMapper<Text, LongWritable> {

        private final static LongWritable one = new LongWritable(1);
        private KeyValueTuple kvt = new KeyValueTuple();
        public void map(ImmutableBytesWritable rowKey, Result columns, Mapper.Context context) throws IOException, InterruptedException {
            try {
                String tweettext = new String(columns.getValue(Bytes.toBytes("info"), Bytes.toBytes("tweettext")), "UTF-8").trim();
                String Hashtags[] = tweettext.split(" "); // separating text by spaced
                for (String text : Hashtags) {
                    if (text.trim().startsWith("#")) //Checking if it a Hashtag
                    {
                        //kvt.setKey(text);
                        //kvt.setValue(0);
                        context.write(new Text(text), one);
                    }
                }
            } catch (Exception ex) {
            }
        }
    }

    public static class ReducerClass extends Reducer<Text, LongWritable, Text, LongWritable> {

        private SortedSet<KeyValueTuple> hashtags = new TreeSet<>();

        public void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
            long total = 0;
            for (LongWritable value : values) {
                total += value.get();
            }
            context.write(key, new LongWritable(total));
            KeyValueTuple kvt = new KeyValueTuple();
            kvt.setKey(key.toString());
            kvt.setValue(total);
            hashtags.add(kvt);
        }
        
        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            int i=0;
            String filename = TweetUtils.OUTPUT_PREFIX+"PopularHashtags/PopularHashtags.csv";
            ArrayList<KeyValueTuple> popularHashtags= new ArrayList<>();
            for (KeyValueTuple kvt : hashtags) {
                if(i==10) break;
                popularHashtags.add(kvt);
                i++;
            }
            CSVUtils.CSVUtils.createCSV(popularHashtags, "Hashtag,Count", filename);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new HBaseConfiguration();
        conf.addResource(TweetUtils.HBASE_CONF);
        Job job = Job.getInstance(conf, "Popular Hashtags");
        job.setJarByClass(PopularHashtags.class);

        Scan sc = new Scan();
        sc.setCaching(500);
        sc.setCacheBlocks(false);

        TableMapReduceUtil.initTableMapperJob(
                "tweetdata", // input table
                sc, // Scan instance to control CF and attribute selection
                MapClass.class, // mapper class
                Text.class, // mapper output key
                LongWritable.class, // mapper output value
                job);

        job.setMapperClass(MapClass.class);
        job.setReducerClass(ReducerClass.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);
        
        String dest = TweetUtils.OUTPUT_PREFIX+"PopularHashtags";
        if (args.length > 0) {
            dest = args[0];
        }
        File destination = new File(dest);
        FileUtil.fullyDelete(destination);
        FileOutputFormat.setOutputPath(job, new Path(dest));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
