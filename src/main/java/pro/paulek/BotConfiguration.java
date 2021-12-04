package pro.paulek;

import java.util.List;

public class BotConfiguration {

    private String endpoint;
    private String status;
    List<String> adminIds;

    public BotConfiguration() {}

    public BotConfiguration(String endpoint, String status, List<String> adminIds) {
        this.endpoint = endpoint;
        this.status = status;
        this.adminIds = adminIds;
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
}
