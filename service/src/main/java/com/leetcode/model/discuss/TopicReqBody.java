package com.leetcode.model.discuss;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leetcode.model.cookie.LeetcodeCookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TopicReqBody implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(TopicReqBody.class);

    private String uri;
    private Integer id;
    private String title;
    private String content;
    private List<String> tags = new ArrayList<>();

    private List<LeetcodeCookie> cookies;
    @JsonIgnore
    private BasicCookieStore cookieStore = new BasicCookieStore();

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<LeetcodeCookie> getCookies() {
        return cookies;
    }

    public void setCookies(List<LeetcodeCookie> cookies) {
        if (cookies == null) return;
        this.cookies = cookies;
        for (LeetcodeCookie c : cookies) {
            BasicClientCookie cookie = new BasicClientCookie(c.getName(), c.getValue());
            cookie.setCreationDate(c.getCreationDate());
            cookie.setPath(c.getPath());
            cookie.setVersion(c.getVersion());
            cookie.setComment(c.getComment());
            cookie.setDomain(c.getDomain());
            Date expire = c.getExpiryDate();
            cookie.setExpiryDate(expire);
            cookie.setSecure(c.getSecure());
            cookieStore.addCookie(cookie);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            df.setTimeZone(TimeZone.getDefault());
            LOGGER.info("{}:{}\n expired time:{}", c.getName(), c.getValue(),
                    expire != null ? df.format(expire) : "null");
        }
    }

    public BasicCookieStore getCookieStore() {
        return cookieStore;
    }

    public void setCookieStore(BasicCookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }
}
