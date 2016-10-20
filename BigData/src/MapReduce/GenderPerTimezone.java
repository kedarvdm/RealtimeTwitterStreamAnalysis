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
public class GenderPerTimezone {

    private static class MapClass extends TableMapper<Text, LongWritable> {

        private final static LongWritable one = new LongWritable(1);
        private KeyValueTuple kvt = new KeyValueTuple();
        
        public void map(ImmutableBytesWritable rowKey, Result result, Mapper.Context context) throws UnsupportedEncodingException, IOException, InterruptedException {
            String timezone = new String(result.getValue(Bytes.toBytes("info"), Bytes.toBytes("timezone")),"UTF-8");
            String gender= new String(result.getValue(Bytes.toBytes("info"), Bytes.toBytes("gender")),"UTF-8");
            String timezonAndGender = timezone + "\t" + gender.toUpperCase();
            context.write(new Text(timezonAndGender), one);
        }
    }

    private static class ReducerClass extends Reducer<Text, LongWritable, Text, LongWritable> {

        private SortedSet<KeyValueTuple> genderPerTimezone = new TreeSet<>();

        public void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
            long total = 0;
            for (LongWritable value : values) {
                total += value.get();
            }
            context.write(key, new LongWritable(total));
            KeyValueTuple kvt = new KeyValueTuple();
            kvt.setKey(key.toString());
            kvt.setValue(total);
            genderPerTimezone.add(kvt);
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            int i = 0;
            String filename = TweetUtils.OUTPUT_PREFIX+"GenderPerTimezone/GenderPerTimezone.csv";
            ArrayList<KeyValueTuple> genderPerTimezoneList = new ArrayList<>();
            for (KeyValueTuple kvt : genderPerTimezone) {
                String timezone = (kvt.getKey().split("\t")[0]).trim();
                if(!timezone.equals("null"))
                {
                    genderPerTimezoneList.add(kvt);
                }
            }
            CSVUtils.createCSVForGenderTimezone(genderPerTimezoneList, "Timezone, Male, Female", filename);
        }
    }

    public static void main(String args[]) throws IOException, InterruptedException, ClassNotFoundException {
        Configuration conf = new HBaseConfiguration();
        conf.addResource(TweetUtils.HBASE_CONF);
        Job job = Job.getInstance(conf, "Gender Per Timezone");
        job.setJarByClass(GenderPerTimezone.class);

        Scan sc = new Scan();
        sc.setCaching(500);
        sc.setCacheBlocks(false);

        TableMapReduceUtil.initTableMapperJob("twittergenderprediction", // input table
                sc, // Scan instance to control CF and attribute selection
                GenderPerTimezone.MapClass.class, // mapper class
                Text.class, // mapper output key
                LongWritable.class, // mapper output value
                job);

        job.setMapperClass(GenderPerTimezone.MapClass.class);
        job.setReducerClass(GenderPerTimezone.ReducerClass.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);
        String dest = TweetUtils.OUTPUT_PREFIX+"GenderPerTimezone";
        if (args.length > 0) {
            dest = args[0];
        }
        File destination = new File(dest);
        FileUtil.fullyDelete(destination);
        FileOutputFormat.setOutputPath(job, new Path(dest));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
