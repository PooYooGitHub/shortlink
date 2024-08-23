package com.project.service.impl;

import com.project.service.UrlTitleService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;


@Service
public class UrlTitleServiceImpl implements UrlTitleService {

    @Override
    public String getOriginLinkTile(String url) {
        HttpURLConnection connection = null;
        try {
            URL tagetUrl = new URL(url);
            connection = (HttpURLConnection) tagetUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Document document = Jsoup.connect(url).get();
                return document.title();
            } else {
                return "Error while fetching title: HTTP response code " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error while fetching title: " + e.getMessage();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

}
