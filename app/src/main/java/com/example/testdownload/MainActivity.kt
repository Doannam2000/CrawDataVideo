package com.example.testdownload

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.io.UnsupportedEncodingException

class MainActivity : AppCompatActivity() {
    private val headers = HashMap<String, String>()

    //    private val path = "https://fb.watch/inaCbAMvLu/"       //735692064499679
//        private val path = "https://fb.watch/in9a1R8l97/"   //507253684861738
//    private val path = "https://www.facebook.com/gaming/Cxrrupt/videos/873294183705078/"
//    private val path = "https://www.facebook.com/100042029148744/videos/5253219131402719/"
        private val path = "https://www.facebook.com/gaming/AnhMuc.1cr/videos/1583420772164054/"


    private val listID = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        headers["accept"] =
            "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"
        headers["accept-language"] = "en-US,en;q=0.9"
        headers["sec-ch-ua"] =
            "\"Not_A Brand\";v=\"99\", \"Microsoft Edge\";v=\"109\", \"Chromium\";v=\"109\"\n"
        headers["scheme"] = "https"
        headers["sec-ch-ua-mobile"] = "?0"
        headers["sec-ch-ua-platform"] = "Windows"
        headers["sec-fetch-dest"] = "document"
        headers["sec-fetch-mode"] = "navigate"

        CoroutineScope(Dispatchers.IO).launch {
            val document = Jsoup.connect(path)
                .userAgent("Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Mobile Safari/537.36")
                .headers(headers)
                .get()
            var id = ""
            val pattern = "[0-9]{14,}".toRegex()
            if (path.startsWith("https://fb.watch/")) {
                val element = pattern.findAll(document.toString())
                id = element.firstOrNull { it.value.length == 15 }?.value ?:""

            } else if (path.startsWith("https://www.facebook.com/")) {
                id = pattern.findAll(path).last().value
            }
            val link = "https://mbasic.facebook.com/$id"
            getLinkMediaFB(link) {
                Log.d("zzzzz", "onCreate: $it")
            }
        }


    }

    //      get link by mbasic
    private fun getLinkMediaFB(linkBasic: String, callBack: (pathVideo: String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val document = Jsoup.connect(linkBasic)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36 Edg/109.0.1518.70")
                .headers(headers)
                .get()
            val pattern = "video_redirect[/?a-zA-Z0-9%=&._;-]{0,}\"".toRegex()
            var element = pattern.find(document.toString())
            var link = element?.value?.replace("video_redirect/?src=", "")
            try {
                val linkVideo =
                    URLDecoder.decode(link?.replace("\"", "") ?: "", StandardCharsets.UTF_8.name())
                callBack.invoke(linkVideo)
            } catch (ignored: UnsupportedEncodingException) {
                callBack.invoke("")
            }
        }
    }
}