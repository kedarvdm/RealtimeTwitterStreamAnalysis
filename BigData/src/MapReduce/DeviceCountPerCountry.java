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
import java.util.Arrays;
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
 * @author rishikaidnani
 */
public class DeviceCountPerCountry {

    String[] androidList = {"Twitter for Android", "Twitter for Android Tablets"};
    String[] iosList = {"Twitter for iPhone", "Twitter for iPad"};
    String[] otherList = {"Twitter for BlackBerry", "Twitter for BlackBerryÂ®", "Twitter for Windows", "Twitter for Windows Phone"};

    private static class MapClass extends TableMapper<Text, LongWritable> {

        private final static LongWritable one = new LongWritable(1);
        private KeyValueTuple kvt = new KeyValueTuple();
        ArrayList<String> androidList = new ArrayList(Arrays.asList("Twitter for Android", "Twitter for Android Tablets"));
        ArrayList<String> iosList = new ArrayList(Arrays.asList("Twitter for iPhone", "Twitter for iPad"));
        ArrayList<String> otherList = new ArrayList(Arrays.asList("Twitter for BlackBerry", "Twitter for BlackBerryÂ®", "Twitter for Windows", "Twitter for Windows Phone"));

        public void map(ImmutableBytesWritable rowKey, Result result, Mapper.Context context) throws UnsupportedEncodingException, IOException, InterruptedException {
            LongWritable one = new LongWritable(1);

            String country = Bytes.toStringBinary(result.getValue(Bytes.toBytes("place"), Bytes.toBytes("placecountry")));
            if (country.equals("null")) {
                country = "Unspecified Country";
            }
            else
            {
                country = new String(result.getValue(Bytes.toBytes("place"), Bytes.toBytes("placecountry")),"UTF-8");
            }
            String htmlString = Bytes.toStringBinary(result.getValue(Bytes.toBytes("tweet"), Bytes.toBytes("tweetsource")));
            String deviceType = TweetUtils.getText(htmlString);
            String deviceSource = "Web";

            if (androidList.contains(deviceType)) {
                deviceSource = "Android";
            } else if (iosList.contains(deviceType)) {
                deviceSource = "ioS";
            } else if (otherList.contains(deviceType)) {
                deviceSource = "Others";
            } else {
                deviceSource = "Web";
            }

            String countryAndSource = country + "-" + deviceSource;

            context.write(new Text(countryAndSource), one);
        }
    }

    private static class ReducerClass extends Reducer<Text, LongWritable, Text, LongWritable> {

        private SortedSet<KeyValueTuple> deviceAndCountry = new TreeSet<>();

        public void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
            long total = 0;
            for (LongWritable value : values) {
                total += value.get();
            }
            context.write(key, new LongWritable(total));
            KeyValueTuple kvt = new KeyValueTuple();
            kvt.setKey(key.toString());
            kvt.setValue(total);
            deviceAndCountry.add(kvt);
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            int i = 0;
            String filename = TweetUtils.OUTPUT_PREFIX+"DeviceCount/DeviceCount.csv";
            ArrayList<KeyValueTuple> deviceCountCountry = new ArrayList<>();
            for (KeyValueTuple kvt : deviceAndCountry) {
                if(i==40) break;
                if(!kvt.getKey().startsWith("Unspecified Country"))
                {
                    deviceCountCountry.add(kvt);
                    i++;
                }
            }
            CSVUtils.createCSVForDeviceCount(deviceCountCountry, "Country, Android, ioS, Web, Other", filename);
        }
    }

    public static void main(String args[]) throws IOException, InterruptedException, ClassNotFoundException {
        Configuration conf = new HBaseConfiguration();

        conf.addResource(TweetUtils.HBASE_CONF);
        Job job = Job.getInstance(conf, "Device Count");
        job.setJarByClass(DeviceCountPerCountry.class);

        Scan sc = new Scan();
        sc.setCaching(500);
        sc.setCacheBlocks(false);

        TableMapReduceUtil.initTableMapperJob(
                "tweetdata", // input table
                sc, // Scan instance to control CF and attribute selection
                DeviceCountPerCountry.MapClass.class, // mapper class
                Text.class, // mapper output key
                LongWritable.class, // mapper output value
                job);

        job.setMapperClass(DeviceCountPerCountry.MapClass.class);
        job.setReducerClass(DeviceCountPerCountry.ReducerClass.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);
        String dest = TweetUtils.OUTPUT_PREFIX+"DeviceCount";
        if (args.length > 0) {
            dest = args[0];
        }
        File destination = new File(dest);
        FileUtil.fullyDelete(destination);
        FileOutputFormat.setOutputPath(job, new Path(dest));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
