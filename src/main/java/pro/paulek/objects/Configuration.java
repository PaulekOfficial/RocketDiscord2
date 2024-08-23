package pro.paulek.objects;

import java.util.List;
import java.util.Map;

public class Configuration {

    Map<String, String> mysql;
    List<String> adminIds;
    private String endpoint;
    private String status;
    private String storageType;

    private String youtubeOauth2;
    private boolean initializeOauth2ForYoutube;

    public Configuration() {
    }

    public Configuration(Map<String, String> mysql, List<String> adminIds, String endpoint, String status, String storageType, String youtubeOauth2, boolean initializeOauth2ForYoutube) {
        this.mysql = mysql;
        this.adminIds = adminIds;
        this.endpoint = endpoint;
        this.status = status;
        this.storageType = storageType;
        this.youtubeOauth2 = youtubeOauth2;
        this.initializeOauth2ForYoutube = initializeOauth2ForYoutube;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getAdminIds() {
        return adminIds;
    }

    public void setAdminIds(List<String> adminIds) {
        this.adminIds = adminIds;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public Map<String, String> getMysql() {
        return mysql;
    }

    public void setMysql(Map<String, String> mysql) {
        this.mysql = mysql;
    }

    public String getYoutubeOauth2() {
        return youtubeOauth2;
    }

    public void setYoutubeOauth2(String youtubeOauth2) {
        this.youtubeOauth2 = youtubeOauth2;
    }

    public boolean isInitializeOauth2ForYoutube() {
        return initializeOauth2ForYoutube;
    }

    public void setInitializeOauth2ForYoutube(boolean initializeOauth2ForYoutube) {
        this.initializeOauth2ForYoutube = initializeOauth2ForYoutube;
    }
}
