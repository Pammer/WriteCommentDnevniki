package com.company;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Map;

public class Main {
    private static final String userAgentString = "Mozilla/9.0 (X11; Ubuntu; Linux i386; rv:50.0) Gecko/20100101 Firefox/50.0";
    private static Map<String, String> cookies;

    public static void main(String[] args) {
        writeIt();
    }


    /**
     * Добавляет комментарий к любому существующему посту , не замечая ограничений доступа.
     */
    public static void writeIt() {
        /*ProxyList.getProxy();
        String[] proxy = ProxyList.getProxyString().split(":");
        System.setProperty("http.proxyHost", proxy[0]);
        System.setProperty("http.proxyPort", proxy[1]);*/
        Connection.Response res = null;


        //Здесь получаем cookies
        try {
            res = Jsoup.connect("http://id.ykt.ru/page/login")
                    .data("nick", "nick kakoinit") // логин пользователя , от чьего имени писать
                    .method(Connection.Method.POST)
                    .data("password", "pass ot etogo nika") // пароль
                    .data("mobile", "false")
                    .data("remember", "on")
                    .data("returnUrl", "http://dnevniki.ykt.ru/")
                    .timeout(200000)
                    .data("generateAccessToken", "false").execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cookies = res.cookies();

        try { //получаем hash

            Connection.Response hashhtml = Jsoup.connect("link to post , whose owner is nick")//ссылка, на пост,который приндадлжет пользователю, под чьим ником пишем. На самом деле , можно и с главной дневников получить, но так удобней
                    .method(Connection.Method.GET).timeout(50000)
                    .cookies(cookies).execute();
            Document doc = hashhtml.parse();
            Element inputhash = doc.select("input[name=hash]").first();
            String hash = inputhash.attr("value");

            //создаем POST запрос,который пишет комментарий
            Connection.Response comment = Jsoup
                    .connect("http://dnevniki.ykt.ru/comment.py")
                    .method(Connection.Method.POST)
                    .cookies(cookies)
                    .timeout(50000)
                    .ignoreContentType(true)
                    .data("action", "save")
                    .data("images", "")
                    .data("postId", "1077265")//пост, где пишем комментарий
                    .data("parentId", "0")
                    .data("commentId", "0")
                    .data("text", "<p>test</p>")//текст комментария
                    .data("hash", hash)
                    .referrer("http://dnevniki.ykt.ru/Pinic/1077265")
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
