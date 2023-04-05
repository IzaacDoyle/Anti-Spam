package com.ab.anti_spam.smswarning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import com.ab.anti_spam.helpers.UIDsave;
import com.ab.anti_spam.localstorage.SMSBlacklistStorage;
import com.ab.anti_spam.localstorage.SettingsStorage;
import com.ab.anti_spam.models.SMSBlacklistModel;
import com.ab.anti_spam.models.SettingsModel;
import com.ab.anti_spam.ui.settings.Settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {

                UIDsave uidSave = UIDsave.INSTANCE;
                String latestUID = uidSave.getUidFromFile(context);

                SettingsModel settings = getSettings(latestUID, context);
                if (settings.getScan_sms()) {



                    //Scan
                    try {
                        SMSBlacklistStorage localStorage = new SMSBlacklistStorage(context);
                        ArrayList<SMSBlacklistModel> model = (ArrayList<SMSBlacklistModel>) localStorage.getAll();

                        ArrayList<String> keywords = new ArrayList<>();
                        ArrayList<String> regexes = new ArrayList<>();

                        //Sorting model & seperating keywords and regex into different arrays.
                        for (int i = 0; i < model.size(); i++) {
                            if (model.get(i).getBy_keyword().equals(model.get(i).getBy_keyword())) {
                                keywords.add(model.get(i).getBy_keyword().trim().toLowerCase());
                            }
                            if (model.get(i).getBy_regex().equals(model.get(i).getBy_regex())) {
                                regexes.add(model.get(i).getBy_regex().trim());
                            }
                        }

                        //Receiving SMS and detecting comparison to trigger warning...
                        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
                            SmsMessage[] smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                            for (SmsMessage message : smsMessages) {

                                //Using the Naive Bayes Classification to detect if message is fraudulent.
                                if (message.getMessageBody().length() > 10) {
                                    Boolean result = BayesNaiveClassifier.INSTANCE.isSpamClassify(message.getMessageBody(),context);
                                    if(result){
                                        System.out.println("Naive Model detected as scam, displaying overlay...");
                                        Intent overlayintent = new Intent(context, overlayservice.class);
                                        overlayintent.putExtra("msg_from", message.getDisplayOriginatingAddress());
                                        context.startService(new Intent(overlayintent));
                                    }else{
                                        System.out.println("Naive Model detect as legit");
                                    }
                                }
                                //if not a scam then check the manual blacklists.
                                else {
                                    //Chopping received SMS message for analysis.
                                    String[] choppedMessage = message.getMessageBody().split(" ");
                                    for (int i = 0; i < choppedMessage.length; i++) {
                                        String comparison = choppedMessage[i].toLowerCase();

                                        //Keywords
                                        for (int ii = 0; ii < keywords.size(); ii++) {
                                            if (comparison.trim().contains(keywords.get(ii).trim())) {
                                                //Trigger warning
                                                Intent overlayintent = new Intent(context, overlayservice.class);
                                                overlayintent.putExtra("msg_from", message.getDisplayOriginatingAddress());
                                                context.startService(new Intent(overlayintent));
                                                break;
                                            }
                                        }
                                        //Regexes
                                        for (int iii = 0; iii < regexes.size(); iii++) {
                                            if (regexes.get(iii).matches(comparison)) {
                                                //Trigger warning
                                                Intent overlayintent = new Intent(context, overlayservice.class);
                                                overlayintent.putExtra("msg_from", message.getOriginatingAddress());
                                                context.startService(new Intent(overlayintent));
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public SettingsModel getSettings(String uid, Context context) {
        try {
            // Gets settings
            SettingsStorage settings = new SettingsStorage(context);
            // Gets all the settings
            List<SettingsModel> allSettings = settings.getAll();
            // Local variable
            SettingsModel userSetting = null;
            // Gets the settings associated to the user ID
            for (SettingsModel i : allSettings) {
                if (i.getUid().equals(uid)) {
                    userSetting = i;
                    break;
                }
            }
            return userSetting;
        } catch (Exception e) {
            return null;
        }
    }

}