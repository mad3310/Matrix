package com.letv.common.util;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by liuhao1 on 2015/12/7.
 */
public class SessionUtil {

    private final static Logger logger = LoggerFactory
            .getLogger(SessionUtil.class);

    private static final String BARRIER = "|";
    private static final int SESSION_CONTENT_LENGTH = 4;
    private static final String BARRIER_REGEX = "\\|";

    public static String generateSessionId(String uuid, String client_id,
                                           String client_secret) {
        if (StringUtils.isEmpty(uuid)) {
            return "";
        }
        String realContent = new StringBuilder(uuid).append(BARRIER).append(client_id)
                .append(BARRIER).append(client_secret).append(BARRIER)
                .append(System.nanoTime()).toString();

        try {
            return ZipUtil.compress(realContent);
        } catch (IOException e) {
            logger.error("ZipUtil compress error:{}",e.getMessage());
        }
        return "";
    }

    public static final String getUuidBySessionId(String sessionId) {

        if (StringUtils.isEmpty(sessionId)) {
            return "";
        }
        String realContent = "";
        try {
            realContent = ZipUtil.uncompress(sessionId);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        int length = realContent.indexOf(BARRIER);
        if (length < 1) {
            logger.error("[SESSIONID INVALID], realContent="+realContent);
        }
        return realContent.substring(0, length);
    }

    public static OauthClientIdAndSecret getClientIdAndClientSecretBySessionId(String sessionId) {
        if (StringUtils.isEmpty(sessionId)) {
            return null;
        }
        String realContent = "";
        try {
            realContent = ZipUtil.uncompress(sessionId);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        String[] contents = realContent.split(BARRIER_REGEX);
        if (contents == null || contents.length != SESSION_CONTENT_LENGTH) {
            logger.error("[SESSIONID INVALID], realContent="+realContent);
        }
        return new OauthClientIdAndSecret(contents[1],contents[2]);

    }

    public static class OauthClientIdAndSecret {
        private String client_id;
        private String client_secret;

        public OauthClientIdAndSecret(String client_id, String client_secret) {
            super();
            this.client_id = client_id;
            this.client_secret = client_secret;
        }

        public String getClient_id() {
            return client_id;
        }

        public void setClient_id(String client_id) {
            this.client_id = client_id;
        }

        public String getClient_secret() {
            return client_secret;
        }

        public void setClient_secret(String client_secret) {
            this.client_secret = client_secret;
        }

        @Override
        public String toString() {
            return "OauthClientIdAndSecret [client_id=" + client_id
                    + ", client_secret=" + client_secret + "]";
        }

    }

}