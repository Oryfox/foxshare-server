package de.oryfox.foxshare.foxshareserver;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.TimerTask;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Client {
    @JsonIgnore
    private String extIp;
    private String hostname;
    private String localIp;
    private boolean allowed;
    @JsonIgnore
    private TimerTask task;

    public Client(String extIp, String hostname, String localIp, boolean allowed) {
        this.extIp = extIp;
        this.hostname = hostname;
        this.localIp = localIp;
        this.allowed = allowed;
    }

    public boolean removal(String extIp, String localIp) {
        if (this.extIp.equals(extIp) && this.localIp.equals(localIp)) {
            task.cancel();
            return true;
        }
        return false;
    }
}
