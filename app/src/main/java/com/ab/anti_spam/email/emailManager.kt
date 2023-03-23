package com.ab.anti_spam.email

import com.ab.anti_spam.R
import java.util.*
import javax.mail.*
import javax.mail.internet.*
import kotlin.concurrent.thread

fun sendEmail(subject: String, email: String, name: String, description: String, pss:String  ,callback: (Boolean) -> Unit) {
    thread {
        val to = "antispamhelpdesk@gmail.com" // email address of the recipient
        val from = "antispamhelpdesk@gmail.com"
        val host = "smtp.gmail.com"
        val username = "antispamhelpdesk@gmail.com"
        val password = de(pss)

        val properties = Properties()
        properties["mail.smtp.auth"] = "true"
        properties["mail.smtp.starttls.enable"] = "true"
        properties["mail.smtp.host"] = host
        properties["mail.smtp.port"] = "587"

        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(username, password)
            }
        })


        try {
            val message = MimeMessage(session)
            message.setFrom(InternetAddress(from))
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
            message.subject = subject
            message.setText("Email: ${email}\nName: ${name}\n\n=== Description ===\n\n ${description}")

            Transport.send(message)
            callback(true)
        } catch (ex: MessagingException) {
            callback(false)
            ex.printStackTrace()

        }
    }
}
fun de(b:String) : String{
    val salt = "w=="
    val decodedString = Base64.getDecoder().decode(b+salt).decodeToString()
    return decodedString
}
