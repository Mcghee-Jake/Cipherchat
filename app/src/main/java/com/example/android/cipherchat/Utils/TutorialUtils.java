package com.example.android.cipherchat.Utils;

import com.example.android.cipherchat.Objects.Tutorial;

import java.util.ArrayList;
import java.util.List;

public class TutorialUtils {

    public static List<Tutorial> getTutorials() {
        List<Tutorial> list = new ArrayList<>();
        Tutorial tutorial_1 = new Tutorial();
        tutorial_1.setTitle("Why Do I Need Encrypted Communication?");
        tutorial_1.setTutorialContents("If your new to encrypted messaging platforms, you might be thinking \"I don't have anything to hide. Why do I need to use encrypted communication?\" What was just recently a new frontier, digital communication is now one of the main ways we communicate with one another. In many ways this is a great thing - it allows you to connect with friends, family, and colleagues from around the world in just an instant. However one risk that this poses is that a lot more of our private conversations now have a lot more opportunity to be leaked to the outside world. When sending regular text messages using the default texting applications on your android device, all of your messages are vulnerable to exposure to your cell phone provider, your government, and to hackers. You might not give it too much thought - but everyone has personal information that they need to protect. For example, if your primary email account is compromised, an attacker could use account to reset your login information for many of your online accounts, giving them access to those accounts as well in addition to your personal e-mails. Additionally, the more personal information you expose, the more vulnerable you are to identity theft. That is why whenever you need to text about sensitive information, such as personal information, financial information, sensitive business information, account passwords, or even just having conversations close to your personal life, it is strongly recommended by security experts that you should be using encrypted communication. \n" +
                "\n" +
                "In the next lesson you will learn about how encryption helps reinforce your security and privacy during your digital conversations.");
        list.add(tutorial_1);
        Tutorial tutorial_2 = new Tutorial();
        tutorial_2.setTitle("Cryptography Basics");
        list.add(tutorial_2);
        Tutorial tutorial_3 = new Tutorial();
        tutorial_3.setTitle("Limits of Encrypted Communication");
        list.add(tutorial_3);
        Tutorial tutorial_4 = new Tutorial();
        tutorial_4.setTitle("Symmetric Encryption");
        list.add(tutorial_4);
        Tutorial tutorial_5 = new Tutorial();
        tutorial_5.setTitle("Asymmetric Encryption");
        list.add(tutorial_5);
        Tutorial tutorial_6 = new Tutorial();
        tutorial_6.setTitle("How Encryption Is Used Within This App");
        list.add(tutorial_6);
        return list;
    }
}
