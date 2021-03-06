raw_data = LOAD 'hbase://twitteruser' USING org.apache.pig.backend.hadoop.hbase.HBaseStorage('info:verifiedAccount', '-loadKey true') AS (tweetid: bytearray, verifiedAccount: chararray);

filteredData = FILTER raw_data BY verifiedAccount == 'true';

temp1 = FOREACH filteredData GENERATE 1 as One;

temp2 = GROUP temp1 all;

numberOfVerifiedAccounts = FOREACH temp2 GENERATE SUM(temp1.One);

DUMP numberOfVerifiedAccounts;
