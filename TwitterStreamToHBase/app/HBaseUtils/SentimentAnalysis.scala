package HBaseUtils

import java.util.Properties

import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

/**
  * Created by rishikaidnani on 11/26/15.
  */

object SentimentAnalysis {

  def getSentiment(text: String): String = {
    val nlpProps = {
      val props = new Properties()
      props.setProperty("annotators", "tokenize, ssplit, parse, sentiment")
      props
    }
    val pipeline = new StanfordCoreNLP(nlpProps)

    val annotation = pipeline.process(text)
    var sentiments: ListBuffer[Double] = ListBuffer()
    for (sentence <- annotation.get(classOf[CoreAnnotations.SentencesAnnotation])) {
      val tree = sentence.get(classOf[SentimentCoreAnnotations.AnnotatedTree])
      val sentiment = RNNCoreAnnotations.getPredictedClass(tree)
      sentiments += sentiment.toDouble
    }

    val averageSentiment: Double = {
      if (sentiments.size > 0) sentiments.sum / sentiments.size
      else 2
    }

    averageSentiment match {
      case s if s < 0.0 => {
        return "NOT_UNDERSTOOD"
      }
      case s if s < 1.6 => {
        return "NEGATIVE"
      }
      case s if s <= 2.0 => {
        return "NEUTRAL"
      }
      case s if s < 5.0 => {
        return "POSITIVE"
      }
      case s if s >= 5.0 => {
        return "NOT_UNDERSTOOD"
      }
    }
  }
}
