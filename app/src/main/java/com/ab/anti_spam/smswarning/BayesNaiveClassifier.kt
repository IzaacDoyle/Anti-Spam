package com.ab.anti_spam.smswarning
import android.content.Context
import com.ab.anti_spam.R
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader



public object BayesNaiveClassifier {
    fun creator(context: Context) {

        val filename = "cleaned_dataset.csv"
        val fileInputStream = context.resources.openRawResource(R.raw.cleaned_dataset)
        val csvReader = BufferedReader(InputStreamReader(fileInputStream))
        val csvLines = csvReader.readLines()
        csvReader.close()

        //Kind of format "['i', 'havent', 'add', 'ü', 'yet', 'right']",ham,0,0,0,0,0,0,0,0

        val ham = arrayListOf<String>()
        val spam = arrayListOf<String>()
        val vocabulary = arrayListOf<String>()
        val spam_vocabulary = arrayListOf<String>()
        val ham_vocabulary = arrayListOf<String>()

        var csvSize = 0

        for (line in csvLines) {
            csvSize++
            if (line.contains(",ham,")) {
                ham.add(line)
            }
            if (line.contains(",spam,")) {
                spam.add(line)
            }
            val vocabu = line.split("]")[0].split(",")
            if (line.contains(",spam,") && line.contains(",ham,")) {
                for (word in vocabu) {
                    val wordfix = word.replace("'", "").replace("[", "").replace("\"", "")
                    vocabulary.add(wordfix)
                    if (line.contains(",spam,")) {
                        spam_vocabulary.add(wordfix)
                    }
                    if (line.contains(",ham,")) {
                        ham_vocabulary.add(wordfix)
                    }
                }
            }
        }
        //this,is,message === 0.0.0.0
        val hamMsgs = arrayListOf<String>()
        val spamMsgs = arrayListOf<String>()


        for (hamLine in ham) {
            val msg = (hamLine.split("']")[0].replace("'", "").replace("[", "").replace("\"", ""))
            val seperator = "==="
            val number = (hamLine.split(",ham,")[1])
            hamMsgs.add(msg + seperator + number)

        }

        for (spamLine in spam) {
            val msg = (spamLine.split("']")[0].replace("'", "").replace("[", "").replace("\"", ""))
            val seperator = "==="
            val number = (spamLine.split(",spam,")[1])
            spamMsgs.add(msg + seperator + number)
        }

        val p_spam = (hamMsgs.size - 1).toFloat() / (csvSize - 1).toFloat()
        val p_ham = (spamMsgs.size - 1).toFloat() / (csvSize - 1).toFloat()

        val n_words_per_spam_message_temp = arrayListOf<Int>()
        val n_words_per_ham_message_temp = arrayListOf<Int>()

        for (msgs in spamMsgs) {
            val sms = msgs.split("===")[1]
            val wordsSizes = sms.split(",")
            var totalnumber = 0
            for (number in wordsSizes) {
                try {
                    totalnumber = totalnumber + number.toInt()
                } catch (e: NumberFormatException) {
                }
            }
            n_words_per_spam_message_temp.add(totalnumber)

        }

        for (msgs in hamMsgs) {
            val sms = msgs.split("===")[1]
            val wordsSizes = sms.split(",")
            var totalnumber = 0
            for (number in wordsSizes) {
                try {
                    totalnumber = totalnumber + number.toInt()
                } catch (e: NumberFormatException) {
                }
            }
            n_words_per_ham_message_temp.add(totalnumber)

        }


        val n_words_per_spam_message = n_words_per_spam_message_temp.drop(1)
        val n_words_per_ham_message = n_words_per_ham_message_temp.drop(1)
        val n_spam = n_words_per_spam_message.sum()
        val n_ham = n_words_per_ham_message.sum()
        val n_vocabulary = vocabulary.toSet().toList().drop(2).size

        val clean_vocabulary = vocabulary.toSet().toList().drop(2)

        val alpha = 1 // LaPlace smoothing

        val cleanspam = spam.drop(1)
        val cleanham = ham.drop(1)


        val parametersSpam = mutableMapOf<String, Float>()
        val parametersHam = mutableMapOf<String, Float>()

        var progress = 0
        //  for (word in vocabulary.drop(2)) {

        //Calculating spam parametes
//        var n_word_given_spam = 0
//
//        for(line in cleanspam){
//            val line = line.split("']")[0]
//            val lineclean = line.replace("'","").replace("[","").replace("\"","")
//            val splitter = lineclean.split(",")
//            for(word2 in splitter){
//                if(word2.trim().equals(word)){
//                    n_word_given_spam++
//                }
//            }
//        }
//
//        val p_word_given_spam = (n_word_given_spam + alpha).toFloat() / (n_spam + alpha * n_vocabulary).toFloat()
//        parametersSpam[word] = p_word_given_spam

        //Calculating ham parametes =============================================================================

//        var n_word_given_ham = 0
//
//        for(hamline in cleanham){
//            val hamlinesplit = hamline.split("']")[0]
//            val linecleanham = hamlinesplit.replace("'","").replace("[","").replace("\"","")
//            val splitter = linecleanham.split(",")
//            for(hamword in splitter){
//                if(hamword.trim().equals(word)){
//                    n_word_given_ham++
//                }
//            }
//        }
//
//        val p_word_given_ham = (n_word_given_ham + alpha).toFloat() / (n_ham + alpha * n_vocabulary).toFloat()
//        parametersHam[word] = p_word_given_ham
//
//
//        progress++
//        println(progress.toString() + " | " + vocabulary.drop(2).size)
//
//    }

        println("===========================")
        println(n_spam)
        println(n_ham)

        // println(parametersSpam.size)
        println(parametersHam.size)
        val gson = Gson()
        val json = gson.toJson(parametersHam)
        // write(context,"parameters_Ham.json",json)


        val spamfileInputStream = context.resources.openRawResource(R.raw.spam_parameters)
        val spamReader = BufferedReader(InputStreamReader(spamfileInputStream))
        val spamReadings = spamReader.readLines()
        csvReader.close()

        val hamfileInputStream = context.resources.openRawResource(R.raw.ham_parameters)
        val hamReader = BufferedReader(InputStreamReader(hamfileInputStream))
        val hamReadings = hamReader.readLines()
        csvReader.close()

        val hamParam = hamReadings.toString()
        val spamParam = spamReadings.toString()

        val parametersHamDict = mutableMapOf<String, Float>()
        val parametersSpamDict = mutableMapOf<String, Float>()

        val hamSplit =
            hamParam.trim().replace("{", "").replace("}", "").replace("\"", "").replace("[", "")
                .replace("]", "").split(",")
        val spamSplit =
            spamParam.trim().replace("{", "").replace("}", "").replace("\"", "").replace("[", "")
                .replace("]", "").split(",")

        for (split in hamSplit) {
            val split2 = split.split(":")
            parametersHamDict[split2[0]] = split2[1].toFloat()
        }
        for (split in spamSplit) {
            val split2 = split.split(":")
            parametersSpamDict[split2[0]] = split2[1].toFloat()
        }


        println(parametersHamDict)
        println(parametersSpamDict)


    }

    fun isSpamClassify(message: String, context: Context): Boolean {

        //Obtaining ham spam parameters

        val spamfileInputStream = context.resources.openRawResource(R.raw.spam_parameters)
        val spamReader = BufferedReader(InputStreamReader(spamfileInputStream))
        val spamReadings = spamReader.readLines()
        spamReader.close()

        val hamfileInputStream = context.resources.openRawResource(R.raw.ham_parameters)
        val hamReader = BufferedReader(InputStreamReader(hamfileInputStream))
        val hamReadings = hamReader.readLines()
        spamReader.close()

        val hamParam = hamReadings.toString()
        val spamParam = spamReadings.toString()

        val parametersHamDict = mutableMapOf<String, Float>()
        val parametersSpamDict = mutableMapOf<String, Float>()

        val hamSplit =
            hamParam.trim().replace("{", "").replace("}", "").replace("\"", "").replace("[", "")
                .replace("]", "").split(",")
        val spamSplit =
            spamParam.trim().replace("{", "").replace("}", "").replace("\"", "").replace("[", "")
                .replace("]", "").split(",")

        for (split in hamSplit) {
            val split2 = split.split(":")
            parametersHamDict[split2[0]] = split2[1].toFloat()
        }
        for (split in spamSplit) {
            val split2 = split.split(":")
            parametersSpamDict[split2[0]] = split2[1].toFloat()
        }

        //Cleaning and Classifying

        var cleanedMessage = message.replace(Regex("\\W"), " ")
        val cleanedMsg = cleanedMessage.toLowerCase().split(" ")

        var p_spam_given_message = 15267F
        var p_ham_given_message = 57469F


        for (word in cleanedMsg) {
            if (parametersSpamDict.containsKey(word.trim())) {
                p_spam_given_message = parametersSpamDict[word]!! * parametersSpamDict[word]!!
            }

            if (parametersHamDict.containsKey(word.trim())) {
                p_ham_given_message = parametersHamDict[word]!! * parametersHamDict[word]!!
            }
        }

        //println("spam: " + p_spam_given_message)
       // println("ham: " + p_ham_given_message)


        if (p_ham_given_message > p_spam_given_message) {
            return false
        } else if (p_ham_given_message < p_spam_given_message) {
            return true
        }

        return true
    }


}