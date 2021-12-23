package pro.paulek.data;

import java.util.List;
import java.util.Map;

public class Configuration {

    private String endpoint;
    private String status;

    private String storageType;
    Map<String, String> mysql;

    List<String> adminIds;

    public Configuration() {
    }

    public Configuration(List<String> adminIds, Map<String, String> mysql, String endpoint, String status, String storageType) {
        this.adminIds = adminIds;
        this.mysql = mysql;
        this.endpoint = endpoint;
        this.status = status;
        this.storageType = storageType;
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
}
